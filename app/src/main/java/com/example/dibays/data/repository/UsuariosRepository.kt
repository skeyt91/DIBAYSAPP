package com.example.dibays.data.repository

import com.example.dibays.data.model.Usuario
import org.json.JSONArray
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

class UsuariosRepository(
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun list(accessToken: String): List<Usuario> = withContext(Dispatchers.IO) {
        val rows = get(
            query = "select=id,nombre,email,cuenta_id,created_at&order=created_at.desc",
            accessToken = accessToken,
        )
        buildList {
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                add(
                    Usuario(
                        id = row.optString("id"),
                        nombre = row.optString("nombre", "Sin nombre"),
                        email = row.optString("email", ""),
                        cuentaId = row.optString("cuenta_id", ""),
                        createdAt = row.optString("created_at", ""),
                    )
                )
            }
        }
    }

    private fun get(query: String, accessToken: String): JSONArray {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/usuarios?$query")
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

    private fun extractError(response: String): String {
        if (response.isBlank()) return "No se pudo cargar usuarios."
        return try {
            val json = JSONObject(response)
            when {
                json.has("message") -> json.getString("message")
                json.has("msg") -> json.getString("msg")
                json.has("error_description") -> json.getString("error_description")
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
