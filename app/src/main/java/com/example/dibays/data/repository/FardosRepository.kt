package com.example.dibays.data.repository

import com.example.dibays.data.model.Fardo
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

class FardosRepository(
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun list(accessToken: String): List<Fardo> = withContext(Dispatchers.IO) {
        val rows = get(
            table = "productos",
            query = "select=id,nombre,codigo,categoria,stock,precio,costo&order=created_at.desc",
            accessToken = accessToken,
        )
        buildList {
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                add(
                    Fardo(
                        id = row.optString("id"),
                        nombre = row.optString("nombre", "Sin nombre"),
                        codigo = row.optString("codigo", ""),
                        categoria = row.optString("categoria", ""),
                        stock = row.optInt("stock", 0),
                        precio = row.optDouble("precio", 0.0),
                        costo = row.optDouble("costo", 0.0),
                    )
                )
            }
        }
    }

    suspend fun create(
        accessToken: String,
        nombre: String,
        codigo: String,
        categoria: String,
        stock: Int,
        precio: Double,
        costo: Double,
    ): Fardo = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("auth_user_id", JSONObject.NULL)
            .put("nombre", nombre.trim())
            .put("codigo", codigo.trim())
            .put("categoria", categoria.trim())
            .put("stock", stock)
            .put("precio", precio)
            .put("costo", costo)

        post("productos", payload, accessToken).first()
    }

    suspend fun update(
        accessToken: String,
        id: String,
        nombre: String,
        codigo: String,
        categoria: String,
        stock: Int,
        precio: Double,
        costo: Double,
    ): Fardo = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("nombre", nombre.trim())
            .put("codigo", codigo.trim())
            .put("categoria", categoria.trim())
            .put("stock", stock)
            .put("precio", precio)
            .put("costo", costo)

        patch(
            table = "productos",
            id = id,
            payload = payload,
            accessToken = accessToken,
        ).first()
    }

    suspend fun updateStock(accessToken: String, id: String, stock: Int): Fardo = withContext(Dispatchers.IO) {
        patch(
            table = "productos",
            id = id,
            payload = JSONObject().put("stock", stock),
            accessToken = accessToken,
        ).first()
    }

    suspend fun delete(accessToken: String, id: String) = withContext(Dispatchers.IO) {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/productos?id=eq.${encode(id)}")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${accessToken.ifBlank { anonKey }}")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Prefer", "return=representation")
        }

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }
    }

    private fun post(table: String, payload: JSONObject, accessToken: String): List<Fardo> {
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

        writeBody(connection, payload)
        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(extractError(response))
        }

        return parseRows(response)
    }

    private fun patch(table: String, id: String, payload: JSONObject, accessToken: String): List<Fardo> {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/$table?id=eq.${encode(id)}")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "PATCH"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${accessToken.ifBlank { anonKey }}")
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

        return parseRows(response)
    }

    private fun get(table: String, query: String, accessToken: String): JSONArray {
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

    private fun parseRows(response: String): List<Fardo> {
        val rows = JSONArray(response)
        return buildList {
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                add(
                    Fardo(
                        id = row.optString("id"),
                        nombre = row.optString("nombre", "Sin nombre"),
                        codigo = row.optString("codigo", ""),
                        categoria = row.optString("categoria", ""),
                        stock = row.optInt("stock", 0),
                        precio = row.optDouble("precio", 0.0),
                        costo = row.optDouble("costo", 0.0),
                    )
                )
            }
        }
    }

    private fun writeBody(connection: HttpURLConnection, payload: JSONObject) {
        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        connection.outputStream.use { output: OutputStream ->
            output.write(bytes)
        }
    }

    private fun extractError(response: String): String {
        if (response.isBlank()) return "No se pudo completar la operacion."
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

    private fun encode(value: String): String {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name())
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
