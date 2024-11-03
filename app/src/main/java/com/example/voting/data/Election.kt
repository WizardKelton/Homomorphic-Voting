package com.example.voting.data

import java.util.Date

data class Election(
    val id: String,
    val title: String,
    val status: String,
    val startTime: String,
    val endTime: String
)