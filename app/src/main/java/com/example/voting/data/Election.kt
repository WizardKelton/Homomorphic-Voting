package com.example.voting.data

import Candidate
import com.squareup.moshi.Json
import java.util.Date

data class Election(
    val id: String,
    val title: String,
    val status: String,
    @Json(name = "start_time") val startTime: Date,
    @Json(name = "end_time") val endTime: Date,
    val candidates: List<Candidate>,
    val __v: Int
)