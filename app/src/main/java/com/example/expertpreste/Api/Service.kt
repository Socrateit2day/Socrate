package com.example.expertpreste.Api


import retrofit2.Call
import retrofit2.http.GET

interface Service {
    @GET("/Api/prestataires/")
     fun getPrestataires(): Call<ExpertResponse>
}