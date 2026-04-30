package com.example.dibays;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    void requestPhoneOtp(String phone) throws Exception {
        requireConfig();

        JSONObject payload = new JSONObject();
        payload.put("phone", phone);
        postAuth("otp", payload);
    }

    Session verifyPhoneOtp(String phone, String token) throws Exception {
        requireConfig();

        JSONObject payload = new JSONObject();
        payload.put("phone", phone);
        payload.put("token", token);
        payload.put("type", "sms");

        JSONObject result = postAuth("verify", payload);
        return new Session(
                result.getString("access_token"),
                result.getJSONObject("user").getString("id")
        );
    }

    Account registerUser(String name, String pinHash, String phone, String countryCode, Session session) throws Exception {
        requireConfig();

        Account existing = findAccount(phone, countryCode, session.accessToken);
        if (existing != null) {
            return existing;
        }

        String accountId = createAccount(name, phone, countryCode, session.userId, session.accessToken);
        createUser(name, pinHash, phone, countryCode, accountId, session.userId, session.accessToken);
        return new Account(accountId, name, phone, countryCode);
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
        return new Account(accountId, name, "", "");
    }

    List<Account> listAccounts(String accessToken) throws Exception {
        requireConfig();

        JSONArray rows = get("cuentas", "select=id,nombre,telefono,pais_codigo&order=created_at.desc", accessToken);
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.getJSONObject(i);
            accounts.add(new Account(
                    row.optString("id"),
                    row.optString("nombre", "Cuenta principal"),
                    row.optString("telefono"),
                    row.optString("pais_codigo")
            ));
        }
        return accounts;
    }

    private Account findAccount(String phone, String countryCode, String accessToken) throws Exception {
        String query = "select=cuenta_id,cuentas(id,nombre,telefono,pais_codigo)"
                + "&celular=eq." + encode(phone)
                + "&pais_codigo=eq." + encode(countryCode)
                + "&limit=1";
        JSONArray rows = get("usuarios", query, accessToken);
        if (rows.length() == 0) {
            return null;
        }

        JSONObject account = rows.getJSONObject(0).optJSONObject("cuentas");
        if (account == null) {
            return null;
        }

        return new Account(
                account.optString("id"),
                account.optString("nombre", "Cuenta principal"),
                account.optString("telefono"),
                account.optString("pais_codigo")
        );
    }

    private String createAccount(String name, String phone, String countryCode, String userId, String accessToken) throws Exception {
        JSONObject account = new JSONObject();
        account.put("auth_user_id", userId);
        account.put("nombre", name);
        account.put("tipo", "principal");
        account.put("telefono", phone);
        account.put("pais_codigo", countryCode);

        JSONArray accountResult = post("cuentas", account, accessToken);
        return accountResult.getJSONObject(0).getString("id");
    }

    private void createUser(String name, String pinHash, String phone, String countryCode, String accountId, String userId, String accessToken) throws Exception {
        JSONObject user = new JSONObject();
        user.put("auth_user_id", userId);
        user.put("cuenta_id", accountId);
        user.put("celular", phone);
        user.put("pais_codigo", countryCode);
        user.put("nombre", name);
        user.put("pin_hash", pinHash);

        post("usuarios", user, accessToken);
    }

    private void requireConfig() {
        if (baseUrl.isEmpty() || anonKey.isEmpty()) {
            throw new IllegalStateException("Falta configurar Supabase.");
        }
    }

    private JSONObject postAuth(String endpoint, JSONObject payload) throws Exception {
        URL url = new URL(baseUrl + "/auth/v1/" + endpoint);
        HttpURLConnection connection = openJsonConnection(url, anonKey);
        connection.setRequestMethod("POST");
        writeBody(connection, payload);

        int status = connection.getResponseCode();
        String response = read(status >= 200 && status < 300
                ? connection.getInputStream()
                : connection.getErrorStream());
        connection.disconnect();

        if (status < 200 || status >= 300) {
            throw new IllegalStateException(cleanError(response));
        }

        if (response.isEmpty()) {
            return new JSONObject();
        }
        return new JSONObject(response);
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

    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
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

    static final class Session {
        final String accessToken;
        final String userId;

        Session(String accessToken, String userId) {
            this.accessToken = accessToken;
            this.userId = userId;
        }
    }

    static final class Account {
        final String id;
        final String name;
        final String phone;
        final String countryCode;

        Account(String id, String name, String phone, String countryCode) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.countryCode = countryCode;
        }
    }
}
