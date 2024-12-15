package com.example.uas_mobile_argaadinata_514333

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_mobile_argaadinata_514333.network.ApiClient
import com.example.uas_mobile_argaadinata_514333.network.RegisterRequest
import com.example.uas_mobile_argaadinata_514333.network.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var reEnterPasswordInput: EditText
    private lateinit var signupButton: Button
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameInput = findViewById(R.id.input_name)
        addressInput = findViewById(R.id.input_address)
        usernameInput = findViewById(R.id.input_username)
        phoneInput = findViewById(R.id.input_phone)
        passwordInput = findViewById(R.id.input_password)
        reEnterPasswordInput = findViewById(R.id.input_reEnterPassword)
        signupButton = findViewById(R.id.btn_signup)
        loginLink = findViewById(R.id.link_login)

        signupButton.setOnClickListener { register() }
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkUnique(username: String, phone: String, onComplete: (Boolean, String) -> Unit) {
        ApiClient.getInstance().getAllUsers()
            .enqueue(object : Callback<List<UserResponse>> {
                override fun onResponse(call: Call<List<UserResponse>>, response: Response<List<UserResponse>>) {
                    if (response.isSuccessful) {
                        val users = response.body()
                        when {
                            users.isNullOrEmpty() -> onComplete(true, "")
                            users.any { it.username == username } -> onComplete(false, "Username already exists")
                            users.any { it.phone == phone } -> onComplete(false, "Phone number already exists")
                            else -> onComplete(true, "")
                        }
                    } else {
                        onComplete(false, "Error checking uniqueness")
                    }
                }

                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                    onComplete(false, "Network error: ${t.message}")
                }
            })
    }

    private fun register() {
        val name = nameInput.text.toString()
        val address = addressInput.text.toString()
        val username = usernameInput.text.toString()
        val phone = phoneInput.text.toString()
        val password = passwordInput.text.toString()
        val reEnterPassword = reEnterPasswordInput.text.toString()

        if (name.isEmpty() || address.isEmpty() || username.isEmpty() ||
            phone.isEmpty() || password.isEmpty() || reEnterPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != reEnterPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        checkUnique(username, phone) { isUnique, message ->
            if (!isUnique) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                return@checkUnique
            }

            val request = RegisterRequest(name, address, username, phone, password)

            ApiClient.getInstance().register(request).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}