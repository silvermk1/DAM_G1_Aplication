package com.example.dam_g1_aplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R

class SupportActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var supportTextView: TextView
    private lateinit var contactButton: Button
    private lateinit var queEsButton: Button
    private var contactPressed = false
    private var queEsPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_detail)
        titleTextView = findViewById(R.id.titleTextView)
        supportTextView = findViewById(R.id.supportTextView)
        queEsButton = findViewById(R.id.queEsButton)
        contactButton = findViewById(R.id.contactButton)

        // FOOTER
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        // FOOTER

        queEsButton.setOnClickListener {
            toggleQueEsStatus()
        }
        contactButton.setOnClickListener {
            toggleContactStatus()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun toggleQueEsStatus() {
        queEsPressed = !queEsPressed
        if (queEsPressed) {
            if (contactPressed) {
                toggleContactStatus()
            }
            supportTextView.text = " Roc Rovira \n roviraroc@gmail.com \n\n Marc Ramírez \n marcramirezmoya@gmail.com \n\n Miguel Velasco \n perezvelasco.miguel@gmail.com"
            queEsButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
        } else {
            supportTextView.text = ""
            queEsButton.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        }
    }
    @SuppressLint("SetTextI18n")
    private fun toggleContactStatus() {
        contactPressed = !contactPressed
        if (contactPressed) {
            if (queEsPressed) {
                toggleQueEsStatus()
            }
            supportTextView.text = " Roc Rovira \n roviraroc@gmail.com \n\n Marc Ramírez \n marcramirezmoya@gmail.com \n\n Miguel Velasco \n perezvelasco.miguel@gmail.com"
            contactButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
        } else {
            supportTextView.text = ""
            contactButton.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        }
    }
}
