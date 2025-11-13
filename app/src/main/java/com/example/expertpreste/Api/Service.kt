package com.example.expertpreste.Api


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface Service {
    @GET("/Api/prestataires/")
    fun getPrestataires(): Call<ExpertResponse>

    @POST("/Api/login/")
    suspend fun login(@Body login: LoginResponse): Response<Unit>

    @GET("/Api/provinces/")
    suspend fun getProvinces(): ProvinceResponse

    @POST("/Api/Demande-Service/")
    suspend fun createDemande(
        @Header("Authorization") token: String,
        @Body demande: DemandeService
    ): Response<Unit>

}

