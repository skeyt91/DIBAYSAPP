package com.example.dibays.data.dashboard

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardRepository(
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun loadProducts(accessToken: String): List<ProductSummary> = withContext(Dispatchers.IO) {
        val url = URL("${supabaseUrl.trimEnd('/')}/rest/v1/productos?select=id,nombre,codigo,categoria,stock,precio&order=created_at.desc")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $accessToken")
            setRequestProperty("Accept", "application/json")
        }

        val status = connection.responseCode
        val response = read(if (status in 200..299) connection.inputStream else connection.errorStream)
        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException(response.ifBlank { "No se pudieron cargar los productos." })
        }

        val rows = JSONArray(response)
        buildList {
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                add(
                    ProductSummary(
                        id = row.optString("id"),
                        name = row.optString("nombre", "Sin nombre"),
                        code = row.optString("codigo", ""),
                        category = row.optString("categoria", ""),
                        stock = row.optInt("stock", 0),
                        price = row.optDouble("precio", 0.0),
                    )
                )
            }
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
