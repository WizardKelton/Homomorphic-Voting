package com.example.voting

import Candidate
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import com.example.voting.data.Election
import com.example.voting.data.RetrofitInstance
import com.example.voting.databinding.CandidateCardBinding
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private val paillier = Paillier() // Create an instance of the Paillier class

    private var selectedCandidate: Candidate? = null // Track selected candidate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fetch candidates dynamically from the backend
        fetchCandidates("672762abaa4e2b0f75661231") // Replace "electionId" with the actual ID

        val submitVoteButton = findViewById<Button>(R.id.sendVoteButton)
        submitVoteButton.setOnClickListener {
            submitVote()
        }
    }

    private fun fetchCandidates(electionId: String) {
        RetrofitInstance.api.getElectionById(electionId).enqueue(object : Callback<Election> {
            override fun onResponse(call: Call<Election>, response: Response<Election>) {
                if (response.isSuccessful) {
                    val election = response.body()
                    election?.candidates?.let { displayCandidates(it) }
                } else {
                    Log.e("Election Data", "Failed to retrieve election: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Election>, t: Throwable) {
                Log.e("Election Data", "Network error: ${t.message}")
            }
        })
    }


    private fun displayCandidates(candidateList: List<Candidate>) {
        val candidateContainer = findViewById<LinearLayoutCompat>(R.id.electionRecyclerView)
        candidateContainer.removeAllViews()

        for (candidate in candidateList) {
            val cardBinding = CandidateCardBinding.inflate(LayoutInflater.from(this))

            // Set candidate details
            cardBinding.candidateName.text = candidate.name
            cardBinding.candidateDescription.text = candidate.description
            cardBinding.candidateSymbol.setImageResource(candidate.profilePicture)

            // Handle card selection
            cardBinding.root.setOnClickListener {
                selectedCandidate = candidate // Update selected candidate
                updateCardSelection(candidateContainer, cardBinding.root) // Update UI
            }

            val params = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 0, 5, 35)
            cardBinding.root.layoutParams = params

            candidateContainer.addView(cardBinding.root)
        }
    }

    private fun updateCardSelection(container: LinearLayoutCompat, selectedCard: CardView) {
        // Deselect all cards
        for (i in 0 until container.childCount) {
            val card = container.getChildAt(i) as CardView
            card.isSelected = false
            card.setCardBackgroundColor(resources.getColor(android.R.color.white))
        }
        // Select the clicked card
        selectedCard.isSelected = true
        selectedCard.setCardBackgroundColor(resources.getColor(R.color.selected_card_background))
    }



    private fun submitVote() {
        selectedCandidate?.let { candidate ->
            // Call backend API to submit vote for the selected candidate
            Log.d("Vote Submission", "Submitting vote for: ${candidate.name}")
        } ?: run {
            Toast.makeText(this, "Please select a candidate", Toast.LENGTH_SHORT).show()
        }

    }



    fun firstvote(view: View) {
        // Vector to encrypt
        val vector = listOf(BigInteger.valueOf(1), BigInteger.valueOf(0), BigInteger.valueOf(0))
        val encryptedVector = vector.map { paillier.encrypt(it) }

        // Display encrypted values
        Toast.makeText(
            this,
            "Encrypted: ${encryptedVector.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()

        // Optional: Decrypt to verify
        val decryptedVector = encryptedVector.map { paillier.decrypt(it) }
        Toast.makeText(
            this,
            "Decrypted: ${decryptedVector.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()

        sendVotesToServer(encryptedVector)
    }

    fun secondtvote(view: View) {
        // Vector to encrypt
        val vector = listOf(BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger.valueOf(0))
        val encryptedVector = vector.map { paillier.encrypt(it) }

        // Display encrypted values
        Toast.makeText(
            this,
            "Encrypted: ${encryptedVector.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()

        // Optional: Decrypt to verify
        val decryptedVector = encryptedVector.map { paillier.decrypt(it) }
        Toast.makeText(
            this,
            "Decrypted: ${decryptedVector.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()

        sendVotesToServer(encryptedVector)
    }

    fun thirdvote(view: View) {
        // Vector to encrypt
        val vector = listOf(BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(1))
        val encryptedVector = vector.map { paillier.encrypt(it) }

        // Display encrypted values
        Toast.makeText(
            this,
            "Encrypted: ${encryptedVector.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()

        // Optional: Decrypt to verify
        val decryptedVector = encryptedVector.map { paillier.decrypt(it) }
        Toast.makeText(
            this,
            "Decrypted: ${decryptedVector.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()

        sendVotesToServer(encryptedVector)
    }

    private fun sendVotesToServer(encryptedVotes: List<BigInteger>) {
        thread {
            try {
                val url = URL("http://10.0.2.2:5000/vote")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Prepare JSON payload
                val jsonArray = JSONArray(encryptedVotes.map { it.toString() })
                val jsonInputString = "{\"votes\": ${jsonArray.toString()}}"

                // Log the JSON being sent
                Log.d("VotePayload", jsonInputString)

                connection.outputStream.use { os ->
                    val input = jsonInputString.toByteArray()
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread {
                        Toast.makeText(this, "Vote sent successfully!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = connection.inputStream.bufferedReader().readText()
                    Log.e("VoteError", "Response Code: $responseCode, Message: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Failed to send vote: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}

class Paillier {
    private val n: BigInteger
    private val g: BigInteger
    private val lambda: BigInteger
    private val mu: BigInteger

    init {
        //val p = BigInteger.probablePrime(512, Random())
        //val q = BigInteger.probablePrime(512, Random())
        val p = BigInteger("1031")
        val q = BigInteger("2053")
        n = p.multiply(q)
        g = n + BigInteger.valueOf(1) // Use BigInteger.valueOf for 1
        lambda = (p - BigInteger.valueOf(1)).multiply(q - BigInteger.valueOf(1))
            .divide((p - BigInteger.valueOf(1)).gcd(q - BigInteger.valueOf(1)))
        mu = lambda.modInverse(n)


    }

    fun encrypt(m: BigInteger): BigInteger {
        val r = BigInteger(n.bitLength(), Random()).mod(n)
        val c1 = g.modPow(m, n.multiply(n))
        val c2 = r.modPow(n, n.multiply(n))
        return (c1.multiply(c2)).mod(n.multiply(n))
    }

    fun decrypt(c: BigInteger): BigInteger {
        val u =
            c.modPow(lambda, n.multiply(n)).subtract(BigInteger.valueOf(1)).divide(n).multiply(mu)
                .mod(n)
        return u
    }
}
