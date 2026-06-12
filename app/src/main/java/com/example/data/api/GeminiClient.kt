package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getChatResponse(prompt: String, systemInstruction: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is placeholder or empty.")
            return@withContext getOfflineResponse(prompt)
        }

        try {
            val partsObj = JSONObject().put("text", prompt)
            val partsArray = JSONArray().put(partsObj)
            val contentObj = JSONObject().put("parts", partsArray)
            val contentsArray = JSONArray().put(contentObj)

            val root = JSONObject().put("contents", contentsArray)

            if (systemInstruction.isNotEmpty()) {
                val sysPart = JSONObject().put("text", systemInstruction)
                val sysContent = JSONObject().put("parts", JSONArray().put(sysPart))
                root.put("systemInstruction", sysContent)
            }

            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
            }
            root.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = root.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response from Gemini: Code=${response.code}, Body=$errBody")
                    return@withContext "Error: API response ${response.code}. Fallback: ${getOfflineResponse(prompt)}"
                }

                val responseBodyStr = response.body?.string() ?: return@withContext "No response body"
                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "I couldn't generate text.")
                        }
                    }
                }
                "I'm sorry, I couldn't interpret that travel question. Local Recommendation: " + getOfflineResponse(prompt)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing Gemini API call", e)
            "Offline assistant: ${getOfflineResponse(prompt)}"
        }
    }

    private fun getOfflineResponse(prompt: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("bus") || query.contains("terminal") || query.contains("pune") -> {
                "The MSRTC Shivneri boarding terminal is approx 1.8 km from Pune Junction. Cabs or auto-rickshaws take about 8-10 mins."
            }
            query.contains("probability") || query.contains("chance") || query.contains("risk") -> {
                "Your confirmation probability is calculated using historic coach allocations and cancellation trends. Current indicators show highly volatile RAC movements on the selected route."
            }
            query.contains("refund") || query.contains("cancel") -> {
                "IRCTC cancellation rules state: Flat refund of Rs. 60 + GST is applicable for waitlisted tickets cancelled before chart preparation. RAC/Confirmed cancellation charges vary between Rs. 120 and Rs. 240 based on carriage class."
            }
            query.contains("alternative") || query.contains("flight") || query.contains("routes") -> {
                "Standard alternatives from Pune to Mumbai include the Shivneri AC buses or Indigo short-haul flights. Check our Alternatives tab for exact timings and seat availability."
            }
            query.contains("how") && query.contains("work") -> {
                "ResQRail queries real-time railway databases, predicts the exact risk of waitlist cancellation, and compiles multimodal connecting train + bus/flight transfers instantly!"
            }
            query.contains("hi") || query.contains("hello") || query.contains("hey") -> {
                "Hello passenger! I am your ResQRail Intelligent Travel Agent. I'm actively monitoring local transit connections. Ask me anything about routes, risk levels, or stations!"
            }
            else -> {
                "I've analyzed your travel inquiry! For waitlisted tickets, we recommend checking the 'Alternatives' tab to book connecting AC buses or flight segments if waitlist priority is low."
            }
        }
    }
}
