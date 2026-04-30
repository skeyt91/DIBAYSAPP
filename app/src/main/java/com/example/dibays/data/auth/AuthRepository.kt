package com.example.dibays.data.auth

import com.example.dibays.data.session.AuthSession
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun signIn(email: String, pin: String): AuthSession = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim().lowercase()
        val pinHash = hashPin(pin)

        val rows = getJsonArray(
            table = "usuarios",
            query = "select=id,cuenta_id,email,nombre,pin_hash&email=eq.${encode(normalizedEmail)}&limit=1",
            accessToken = anonKey,
        )

        if (rows.length() == 0) {
            throw IllegalStateException("No encontramos una cuenta con ese correo.")
        }

        val user = rows.getJSONObject(0)
        if (!pinHash.equals(user.optString("pin_hash"), ignoreCase = false)) {
            throw IllegalStateException("PIN incorrecto.")
        }

        AuthSession(
            accessToken = anonKey,
            refreshToken = "",
            userId = user.optString("id"),
            email = normalizedEmail,
        )
    }

    suspend fun signUp(name: String, email: String, pin: String): AuthSession? = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim().lowercase()
        val pinHash = hashPin(pin)

        val existing = getJsonArray(
            table = "usuarios",
            query = "select=id&email=eq.${encode(normalizedEmail)}&limit=1",
            accessToken = anonKey,
        )
        if (existing.length() > 0) {
            throw IllegalStateException("Ya existe una cuenta con ese correo.")
        }

        val accountId = postJson(
            table = "cuentas",
            payload = JSONObject()
                .put("auth_user_id", JSONObject.NULL)
                .put("nombre", name.trim())
                .put("tipo", "principal"),
            accessToken = anonKey,
        ).getJSONObject(0).getString("id")

        val userRow = postJson(
            table = "usuarios",
            payload = JSONObject()
                .put("auth_user_id", JSONObject.NULL)
                .put("cuenta_id", accountId)
                .put("email", normalizedEmail)
                .put("nombre", name.trim())
                .put("pin_hash", pinHash),
            accessToken = anonKey,
        ).getJSONObject(0)

        AuthSession(
            accessToken = anonKey,
            refreshToken = "",
            userId = userRow.optString("id"),
            email = normalizedEmail,
        )
    }

    suspend fun sendRecoveryEmail(email: String) = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim().lowercase()
        val rows = getJsonArray(
            table = "usuarios",
            query = "select=id&email=eq.${encode(normalizedEmail)}&limit=1",
            accessToken = anonKey,
        )
        if (rows.length() == 0) {
            throw IllegalStateException("No encontramos una cuenta con ese correo.")
        }
    }

    private fun getJsonArray(table: String, query: String, accessToken: String): JSONArray {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/$table?$query")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${accessToken.ifBlank { anonKey }}")
            setRequestProperty("Accept", "application/json")
        }

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }

        return JSONArray(response)
    }

    private fun postJson(table: String, payload: JSONObject, accessToken: String): JSONArray {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/$table")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${accessToken.ifBlank { anonKey }}")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Prefer", "return=representation")
        }

        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        connection.outputStream.use { output: OutputStream ->
            output.write(bytes)
        }

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }

        return JSONArray(response)
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val encoded = digest.digest(pin.trim().toByteArray(StandardCharsets.UTF_8))
        return buildString(encoded.size * 2) {
            for (byte in encoded) {
                append(byte.toInt().and(0xff).toString(16).padStart(2, '0'))
            }
        }
    }

    private fun encode(value: String): String {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name())
    }

    private fun extractError(response: String): String {
        if (response.isBlank()) {
            return "Supabase no devolvió un detalle de error."
        }
        return try {
            val json = JSONObject(response)
            when {
                json.has("error_description") -> json.getString("error_description")
                json.has("msg") -> json.getString("msg")
                json.has("message") -> json.getString("message")
                else -> response
            }
        } catch (_: Exception) {
            response
        }
    }

    private fun read(stream: InputStream?): String {
        if (stream == null) return ""
        return BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
            buildString {
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    append(line)
                }
            }
        }
    }
}
