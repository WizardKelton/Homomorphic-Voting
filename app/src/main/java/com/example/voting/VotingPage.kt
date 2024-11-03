package com.example.voting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.voting.R
import com.example.voting.data.Election
import com.example.voting.databinding.ElectionCardBinding

class VotingPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_page)

        // Assuming you've already fetched election data
        val electionList = listOf(
            // Sample data, replace with your fetched data
            Election("1", "Election Title 1", "Ongoing", "2024-01-01", "2024-01-02"),
            Election("2", "Election Title 2", "Closed", "2024-02-01", "2024-02-02")
        )

        // Reference to the container where cards will be added
        val electionContainer = findViewById<LinearLayoutCompat>(R.id.electionRecyclerView)

        // Inflate and add each election card dynamically
        for (election in electionList) {
            val cardBinding = ElectionCardBinding.inflate(LayoutInflater.from(this))
            cardBinding.voteTitleTextView.text = election.title
            cardBinding.voteStatusTextView.text = election.status
            cardBinding.voteStartTimeTextView.text = election.startTime
            cardBinding.voteEndTimeTextView.text = election.endTime

            val params = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20) // (left, top, right, bottom) margins in pixels or use resources for dp
            cardBinding.root.layoutParams = params

            // Add the card to the container
            electionContainer.addView(cardBinding.root)
        }
    }
    fun navigateToVoting(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
