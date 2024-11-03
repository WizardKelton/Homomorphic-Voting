package com.example.voting.data

    import retrofit2.Call
    import retrofit2.http.GET
    import retrofit2.http.Path

    interface ApiService {
        @GET("elections")
        fun getAllElections(): Call<List<Election>>

        @GET("election/{id}")
        fun getElection(@Path("id") electionId: String): Call<Election>
    }

