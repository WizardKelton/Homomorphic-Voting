package com.example.voting.data

import Candidate
import com.example.voting.data.model.LoginRequest
import com.example.voting.data.model.LoginResponse
import com.example.voting.data.model.RegisterRequest
import com.example.voting.data.model.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ElectionApiService {
    @GET("api/elections")
    fun getElections(): Call<List<Election>>

    @GET("api/elections/{id}")
    fun getElectionById(@Path("id") electionId: String): Call<Election>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response <LoginResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response <RegisterResponse>

}