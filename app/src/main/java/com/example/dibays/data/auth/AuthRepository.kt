package com.example.dibays.data.auth

import com.example.dibays.data.session.AuthSession
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun signIn(email: String, pin: String): AuthSession = withContext(Dispatchers.IO) {
        val url = URL("${supabaseUrl.trimEnd('/')}/auth/v1/token?grant_type=password")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $anonKey")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

        val payload = JSONObject()
            .put("email", email.trim())
            .put("password", pin.trim())

        writeBody(connection, payload)

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }

        val json = JSONObject(response)
        AuthSession(
            accessToken = json.getString("access_token"),
            refreshToken = json.optString("refresh_token"),
            userId = json.optJSONObject("user")?.optString("id").orEmpty(),
            email = email.trim(),
        )
    }

    suspend fun signUp(name: String, email: String, pin: String): AuthSession? = withContext(Dispatchers.IO) {
        val url = URL("${supabaseUrl.trimEnd('/')}/auth/v1/signup")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $anonKey")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

        val payload = JSONObject()
            .put("email", email.trim())
            .put("password", pin.trim())
            .put("data", JSONObject().put("name", name.trim()))

        writeBody(connection, payload)

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }

        val json = JSONObject(response)
        val userId = json.optJSONObject("user")?.optString("id").orEmpty()
        val sessionJson = json.optJSONObject("session")
        val session = sessionJson?.let {
            AuthSession(
                accessToken = it.getString("access_token"),
                refreshToken = it.optString("refresh_token"),
                userId = userId,
                email = email.trim(),
            )
        }

        createInitialRows(
            userId = userId,
            name = name.trim(),
            pin = pin.trim(),
            accessToken = session?.accessToken,
        )

        session
    }

    suspend fun sendRecoveryEmail(email: String) = withContext(Dispatchers.IO) {
        val url = URL("${supabaseUrl.trimEnd('/')}/auth/v1/recover")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $anonKey")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

        val payload = JSONObject()
            .put("email", email.trim())

        writeBody(connection, payload)

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }
    }

    private fun writeBody(connection: HttpURLConnection, payload: JSONObject) {
        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        connection.outputStream.use { output: OutputStream ->
            output.write(bytes)
        }
    }

    private fun createInitialRows(userId: String, name: String, pin: String, accessToken: String?) {
        val authUserId = if (accessToken.isNullOrBlank() || userId.isBlank()) {
            JSONObject.NULL
        } else {
            userId
        }

        postJson(
            table = "cuentas",
            payload = JSONObject()
                .put("auth_user_id", authUserId)
                .put("nombre", name)
                .put("tipo", "principal"),
            accessToken = accessToken,
        )

        postJson(
            table = "usuarios",
            payload = JSONObject()
                .put("auth_user_id", authUserId)
                .put("nombre", name)
                .put("pin_hash", pin),
            accessToken = accessToken,
        )
    }

    private fun postJson(table: String, payload: JSONObject, accessToken: String?) {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/$table")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${accessToken?.takeIf { it.isNotBlank() } ?: anonKey}")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Prefer", "return=representation")
        }

        writeBody(connection, payload)

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }
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
