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

final class SupabaseClient {
    private final String baseUrl;
    private final String anonKey;

    SupabaseClient(String baseUrl, String anonKey) {
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
        this.anonKey = anonKey == null ? "" : anonKey.trim();
    }

    void registerUser(String phone, String countryCode) throws Exception {
        if (baseUrl.isEmpty() || anonKey.isEmpty()) {
            throw new IllegalStateException("Falta configurar Supabase.");
        }

        JSONObject account = new JSONObject();
        account.put("nombre", "DIBAYS FARDOS");
        account.put("tipo", "principal");
        account.put("telefono", phone);
        account.put("pais_codigo", countryCode);

        JSONArray accountResult = post("cuentas", account);
        String accountId = accountResult.getJSONObject(0).getString("id");

        JSONObject user = new JSONObject();
        user.put("cuenta_id", accountId);
        user.put("celular", phone);
        user.put("pais_codigo", countryCode);

        post("usuarios", user);
    }

    private JSONArray post(String table, JSONObject payload) throws Exception {
        URL url = new URL(baseUrl + "/rest/v1/" + table);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("apikey", anonKey);
        connection.setRequestProperty("Authorization", "Bearer " + anonKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Prefer", "return=representation");

        byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = connection.getOutputStream()) {
            output.write(body);
        }

        int status = connection.getResponseCode();
        String response = read(status >= 200 && status < 300
                ? connection.getInputStream()
                : connection.getErrorStream());
        connection.disconnect();

        if (status < 200 || status >= 300) {
            throw new IllegalStateException("Supabase respondio " + status + ": " + response);
        }

        return new JSONArray(response);
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
}
