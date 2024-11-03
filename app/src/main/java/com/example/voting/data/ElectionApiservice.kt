package com.example.voting.data

import retrofit2.Call
import retrofit2.http.GET

interface ElectionApiService {
    @GET("api/elections")
    fun getElections(): Call<List<Election>>
}