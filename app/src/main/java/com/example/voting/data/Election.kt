package com.example.voting.data

data class Election(
    val id: String,
    val title: String,
    val status: String,
    val startTime: String,
    val endTime: String
)