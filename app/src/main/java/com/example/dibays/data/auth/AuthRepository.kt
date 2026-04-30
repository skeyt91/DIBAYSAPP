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

    private fun writeBody(connection: HttpURLConnection, payload: JSONObject) {
        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        connection.outputStream.use { output: OutputStream ->
            output.write(bytes)
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
