package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var mailTextView: TextView
    private lateinit var biographyTextMultiLine: EditText
    private lateinit var saveButton: Button
    private lateinit var username: String
    private lateinit var mail: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.getString("userId", null)

        username = sharedPreferences.getString("username", null).toString()
        mail = sharedPreferences.getString("mail", null).toString()

        usernameTextView = findViewById(R.id.usernameTextView)
        mailTextView = findViewById(R.id.mailTextView)
        biographyTextMultiLine = findViewById(R.id.biographyTextMultiLine)
        saveButton = findViewById(R.id.saveButton)

        usernameTextView.text = username
        mailTextView.text = mail

        // FOOTER
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        // FOOTER

    }
}