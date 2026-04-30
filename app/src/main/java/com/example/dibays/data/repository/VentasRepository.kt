package com.example.dibays.data.repository

import com.example.dibays.data.model.Fardo
import com.example.dibays.data.model.Venta
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

class VentasRepository(
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun recent(accessToken: String, limit: Int = 6): List<Venta> = withContext(Dispatchers.IO) {
        val rows = get(
            table = "ventas",
            query = "select=id,cliente_nombre,estado_pago,total,pago_recibido,saldo,created_at&order=created_at.desc&limit=$limit",
            accessToken = accessToken,
        )
        buildList {
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                add(
                    Venta(
                        id = row.optString("id"),
                        clienteNombre = row.optString("cliente_nombre", "Cliente general"),
                        estadoPago = row.optString("estado_pago", "pendiente"),
                        total = row.optDouble("total", 0.0),
                        pagoRecibido = row.optDouble("pago_recibido", 0.0),
                        saldo = row.optDouble("saldo", 0.0),
                        createdAt = row.optString("created_at", ""),
                    )
                )
            }
        }
    }

    suspend fun create(
        accessToken: String,
        clienteNombre: String,
        estadoPago: String,
        pagoRecibido: Double,
        items: List<SaleDraftItem>,
    ): Venta = withContext(Dispatchers.IO) {
        val total = items.sumOf { it.subtotal }
        val saldo = (total - pagoRecibido).coerceAtLeast(0.0)
        val ventaPayload = JSONObject()
            .put("auth_user_id", JSONObject.NULL)
            .put("cliente_nombre", clienteNombre.trim().ifBlank { "Cliente general" })
            .put("estado_pago", estadoPago.trim().ifBlank { "pendiente" })
            .put("total", total)
            .put("pago_recibido", pagoRecibido)
            .put("saldo", saldo)

        val venta = post("ventas", ventaPayload, accessToken).first()

        for (item in items) {
            val itemPayload = JSONObject()
                .put("venta_id", venta.id)
                .put("producto_id", item.productoId)
                .put("nombre_producto", item.nombre)
                .put("cantidad", item.cantidad)
                .put("precio_unitario", item.precioUnitario)
                .put("subtotal", item.subtotal)
            postRaw("venta_items", itemPayload, accessToken)
        }

        venta
    }

    private fun post(table: String, payload: JSONObject, accessToken: String): List<Venta> {
        val rows = postRaw(table, payload, accessToken)
        return parseVentas(rows)
    }

    private fun postRaw(table: String, payload: JSONObject, accessToken: String): String {
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

        return response
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

    private fun parseVentas(response: String): List<Venta> {
        val rows = JSONArray(response)
        return buildList {
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                add(
                    Venta(
                        id = row.optString("id"),
                        clienteNombre = row.optString("cliente_nombre", "Cliente general"),
                        estadoPago = row.optString("estado_pago", "pendiente"),
                        total = row.optDouble("total", 0.0),
                        pagoRecibido = row.optDouble("pago_recibido", 0.0),
                        saldo = row.optDouble("saldo", 0.0),
                        createdAt = row.optString("created_at", ""),
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

    data class SaleDraftItem(
        val productoId: String,
        val nombre: String,
        val cantidad: Int,
        val precioUnitario: Double,
    ) {
        val subtotal: Double get() = cantidad * precioUnitario
    }
}
