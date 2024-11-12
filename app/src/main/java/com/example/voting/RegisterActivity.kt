package com.example.voting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.voting.data.RetrofitInstance
import com.example.voting.data.model.RegisterRequest
import com.example.voting.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailInput.text.toString().trim()
            val password = binding.registerPasswordInput.text.toString()
            val confirmPassword = binding.registerConfirmPasswordInput.text.toString()

            if (validateInput(email, password, confirmPassword)) {
                registerUser(email, password)
            }
        }

    }

    fun goToLoginPage(view: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)

    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        return when {
            email.isEmpty() -> {
                showToast("Email is required")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter a valid email")
                false
            }
            password.isEmpty() -> {
                showToast("Password is required")
                false
            }
            password != confirmPassword -> {
                showToast("Passwords do not match")
                false
            }
            else -> true
        }
    }

    private fun registerUser(email: String, password: String) {
        val request = RegisterRequest(email, password)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.register(request)

                // Check response status code or body
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        showToast(response.message())
                        finish() // Return to login after registration
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Error: ${response.message()} - ${response.code()}")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Registration failed: ${e.message}")
                }
                // Log the error
                e.printStackTrace()
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}




