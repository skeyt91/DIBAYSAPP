package com.example.dibays;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class SupabaseClient {
    private final String baseUrl;
    private final String anonKey;

    SupabaseClient(String baseUrl, String anonKey) {
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
        this.anonKey = anonKey == null ? "" : anonKey.trim();
    }

    Account registerUser(String name, String pinHash) throws Exception {
        requireConfig();

        JSONObject account = new JSONObject();
        account.put("nombre", name);
        account.put("tipo", "principal");

        JSONArray accountResult = post("cuentas", account, "");
        String accountId = accountResult.getJSONObject(0).getString("id");

        JSONObject user = new JSONObject();
        user.put("cuenta_id", accountId);
        user.put("nombre", name);
        user.put("pin_hash", pinHash);
        post("usuarios", user, "");

        return new Account(accountId, name);
    }

    List<Account> listAccounts(String accessToken) throws Exception {
        requireConfig();

        JSONArray rows = get("cuentas", "select=id,nombre&order=created_at.desc", accessToken);
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.getJSONObject(i);
            accounts.add(new Account(
                    row.optString("id"),
                    row.optString("nombre", "Cuenta principal")
            ));
        }
        return accounts;
    }

    private void requireConfig() {
        if (baseUrl.isEmpty() || anonKey.isEmpty()) {
            throw new IllegalStateException("Falta configurar Supabase.");
        }
    }

    private JSONArray get(String table, String query, String accessToken) throws Exception {
        URL url = new URL(baseUrl + "/rest/v1/" + table + "?" + query);
        HttpURLConnection connection = openJsonConnection(url, accessToken);
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();
        String response = read(status >= 200 && status < 300
                ? connection.getInputStream()
                : connection.getErrorStream());
        connection.disconnect();

        if (status < 200 || status >= 300) {
            throw new IllegalStateException(cleanError(response));
        }

        return new JSONArray(response);
    }

    private JSONArray post(String table, JSONObject payload, String accessToken) throws Exception {
        URL url = new URL(baseUrl + "/rest/v1/" + table);
        HttpURLConnection connection = openJsonConnection(url, accessToken);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Prefer", "return=representation");
        writeBody(connection, payload);

        int status = connection.getResponseCode();
        String response = read(status >= 200 && status < 300
                ? connection.getInputStream()
                : connection.getErrorStream());
        connection.disconnect();

        if (status < 200 || status >= 300) {
            throw new IllegalStateException(cleanError(response));
        }

        return new JSONArray(response);
    }

    private HttpURLConnection openJsonConnection(URL url, String accessToken) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("apikey", anonKey);
        connection.setRequestProperty("Authorization", "Bearer " + (accessToken == null || accessToken.isEmpty() ? anonKey : accessToken));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    private void writeBody(HttpURLConnection connection, JSONObject payload) throws Exception {
        byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = connection.getOutputStream()) {
            output.write(body);
        }
    }

    private String cleanError(String response) {
        if (response == null || response.isEmpty()) {
            return "Supabase no devolvio detalles del error.";
        }
        try {
            JSONObject json = new JSONObject(response);
            if (json.has("msg")) {
                return json.getString("msg");
            }
            if (json.has("message")) {
                return json.getString("message");
            }
            if (json.has("error_description")) {
                return json.getString("error_description");
            }
        } catch (Exception ignored) {
        }
        return response;
    }

    private String read(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    static final class Account {
        final String id;
        final String name;

        Account(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
