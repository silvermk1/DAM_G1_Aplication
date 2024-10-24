package com.example.dam_g1_aplication.Mainclasses

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val listButton = findViewById<Button>(R.id.bVerLogros)

        listButton.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent) // Iniciar la nueva actividad

        }
    }
}