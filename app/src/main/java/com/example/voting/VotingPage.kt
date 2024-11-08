package com.example.voting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.voting.data.Election
import com.example.voting.data.RetrofitInstance
import com.example.voting.databinding.ElectionCardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class VotingPage : AppCompatActivity() {
    val votingStatus = mutableMapOf<String, Int>() // Dictionary to track if the user has voted
    private val sharedPreferences by lazy {
        getSharedPreferences("VotingAppPreferences", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_page)

        loadVotingStatus() // Load saved voting status on startup
        fetchElections()

        val refreshButton = findViewById<Button>(R.id.refresh)
        refreshButton.setOnClickListener {
            fetchElections() // Refresh the elections list
        }

        //TESTING ______________
        setTestVotingStatus("6728650bec18a3eba8ea4514", 1)
    }

    // Save the voting status dictionary in SharedPreferences


    // Load the voting status dictionary from SharedPreferences
    private fun loadVotingStatus() {
        sharedPreferences.all.forEach { (key, value) ->
            if (value is Int) {
                votingStatus[key] = value
            }
        }
    }

    private fun fetchElections() {
        RetrofitInstance.api.getElections().enqueue(object : Callback<List<Election>> {
            override fun onResponse(call: Call<List<Election>>, response: Response<List<Election>>) {
                if (response.isSuccessful) {
                    val electionList = response.body()
                    electionList?.let { displayElections(it) }
                    Log.d("Election Data", "Response: $electionList")
                } else {
                    Log.e("Election Data", "Failed to retrieve elections: ${response.errorBody()?.string()}")
                }

            }

            override fun onFailure(call: Call<List<Election>>, t: Throwable) {
                Log.e("Election Data", "Network error: ${t.message}")
            }
        })
    }

    private fun displayElections(electionList: List<Election>) {
        val electionContainer = findViewById<LinearLayoutCompat>(R.id.electionRecyclerView)
        electionContainer.removeAllViews()

        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

        for (election in electionList) {
            Log.d("Election Data", "Start Time: ${election.startTime}, End Time: ${election.endTime}")

            val cardBinding = ElectionCardBinding.inflate(LayoutInflater.from(this))
            cardBinding.voteTitleTextView.text = election.title
            cardBinding.voteStatusTextView.text = election.status

            // Parse and format the start and end times
            val startTimeDate = election.startTime
            val endTimeDate = election.endTime
            cardBinding.voteStartTimeTextView.text = dateFormat.format(startTimeDate ?: Date())
            cardBinding.voteEndTimeTextView.text = dateFormat.format(endTimeDate ?: Date())

            // Check if user has voted in this election
            if (votingStatus[election.id] == 1) {
                cardBinding.root.isEnabled = false // Make the card non-clickable
                cardBinding.root.setBackgroundColor(resources.getColor(R.color.greyed_out)) // Grey out the card
            } else {
                // Enable click for elections not yet voted on
                cardBinding.root.isEnabled = true
                cardBinding.root.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("ELECTION_ID", election.id)
                    }
                    startActivity(intent)
                }
            }

            // Set layout parameters
            val params = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            cardBinding.root.layoutParams = params

            electionContainer.addView(cardBinding.root)
        }
    }

    // TESTING

    private fun setTestVotingStatus(electionId: String, status: Int) {
        // Save to SharedPreferences with the correct name
        val editor = sharedPreferences.edit()
        editor.putInt(electionId, status) // Use 1 for voted, 0 for not voted
        editor.apply()

        // Update the in-memory votingStatus map for immediate effect
        votingStatus[electionId] = status
    }


}
