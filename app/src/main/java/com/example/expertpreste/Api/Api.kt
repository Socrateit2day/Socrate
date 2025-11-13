package com.example.expertpreste.Api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://backapi.dodoramarket.com"
    //https://backapi.dodoramarket.com
    private var authToken: String? = null
    private var userId: Int? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // connexion max
        .readTimeout(90, TimeUnit.SECONDS)    // lecture max
        .writeTimeout(90, TimeUnit.SECONDS)   // écriture max
        .retryOnConnectionFailure(true)        // réessaye si la connexion coupe
        .build()

    val api: Service by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // ✅ utilise bien ton client custom
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)
    }

    fun setAuth(token: String, id: Int) {
        authToken = token
        userId = id
    }

    fun getToken(): String? = authToken
    fun getUserId(): Int? = userId

    fun setToken(token: String) { authToken = token }
    fun setUserId(id: Int?) { userId = id }

    // Décode userId depuis JWT si non défini
    fun decodeUserIdFromToken(): Int? {
        return authToken?.let { token ->
            try {
                val parts = token.split(".")
                if (parts.size == 3) {
                    val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                    val payload = org.json.JSONObject(payloadJson)
                    payload.optInt("user_id", -1).takeIf { it != -1 }
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}