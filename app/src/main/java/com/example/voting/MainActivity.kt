package com.example.voting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.voting.Paillier
import java.math.BigInteger
import java.util.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    private val paillier = Paillier() // Create an instance of the Paillier class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voting_page)

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
                        Toast.makeText(this, "Failed to send vote: $errorMessage", Toast.LENGTH_LONG).show()
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
        val u = c.modPow(lambda, n.multiply(n)).subtract(BigInteger.valueOf(1)).divide(n).multiply(mu).mod(n)
        return u
    }
}
