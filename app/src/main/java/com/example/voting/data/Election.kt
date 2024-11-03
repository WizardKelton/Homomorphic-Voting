package com.example.voting.data

import Candidate
import java.util.Date

data class Election(
    val id: String,
    val title: String,
    val status: String,
    val startTime: String,
    val endTime: String,
    val candidates: List<Candidate>,
    val __v: Int
)