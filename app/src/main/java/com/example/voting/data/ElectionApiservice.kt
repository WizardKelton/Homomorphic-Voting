package com.example.voting.data

import Candidate
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ElectionApiService {
    @GET("api/elections")
    fun getElections(): Call<List<Election>>

    @GET("api/elections/{id}")
    fun getElectionById(@Path("id") electionId: String): Call<Election>

}