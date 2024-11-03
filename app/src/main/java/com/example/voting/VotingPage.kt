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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_page)

        // Fetch elections from backend and display them
        fetchElections()

        val refreshButton = findViewById<Button>(R.id.refresh)
        refreshButton.setOnClickListener {
            fetchElections() // Call fetchElections on button click
        }
    }

    private fun fetchElections() {
        RetrofitInstance.api.getElections().enqueue(object : Callback<List<Election>> {
            override fun onResponse(call: Call<List<Election>>, response: Response<List<Election>>) {
                if (response.isSuccessful) {
                    val electionList = response.body()
                    electionList?.let { displayElections(it) }
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

            // Parse the date strings into Date objects
            val startTimeDate = convertStringToDate(election.startTime)
            val endTimeDate = convertStringToDate(election.endTime)

            Log.d("Election Data", "Start Time: ${election.startTime}, End Time: ${election.endTime}")


            // Format the parsed dates before setting them to the TextViews
            cardBinding.voteStartTimeTextView.text = dateFormat.format(startTimeDate ?: Date())
            cardBinding.voteEndTimeTextView.text = dateFormat.format(endTimeDate ?: Date())

            val params = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            cardBinding.root.layoutParams = params

            electionContainer.addView(cardBinding.root)

            Log.d("Election Data", "Start Time: ${election.startTime}, End Time: ${election.endTime}")
        }
    }

    private fun convertStringToDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC") // Ensure the correct time zone
            format.parse(dateString)
        } catch (e: Exception) {
            Log.e("Date Conversion", "Error parsing date: $dateString", e)
            null
        }
    }

    // Method to navigate to another activity
    fun navigateToVoting(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
