package org.futo.inputmethod.keyboard.internal

import android.util.Log
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object CloudTranslationHandler {
    private const val API_URL = "https://deep-translator-api.azurewebsites.net/google/"

    suspend fun translate(source: String, target: String, text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL(API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val requestBody = JSONObject().apply {
                put("source", source)
                put("target", target)
                put("text", text)
                put("proxies", JSONArray())
            }

            connection.outputStream.use { os ->
                val input = requestBody.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                Result.success(jsonResponse.getString("translation"))
            } else {
                Result.failure(Exception("Translation failed. HTTP status code: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    class TranslationException(message: String, cause: Throwable? = null) : Exception(message, cause)
}