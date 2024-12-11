package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R

class HomeActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    //INICIAR HOME
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        val categoriesButton: Button = findViewById(R.id.categoriesButton)

        categoriesButton.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }

        // FOOTER
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        profileButton.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

//HE PUESTO UN VIDEO DE FONDO AL HOME
        videoView = findViewById(R.id.videoView)
        // Configura la URI del video
        val videoUri: Uri =
            Uri.parse("android.resource://${packageName}/${R.raw.triangulo}") // Asegúrate de que "triangulo" sea el nombre correcto
        // Establece la URI del video en el VideoView
        videoView.setVideoURI(videoUri)
        // Configuración para que el video se repita
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true // Hacer que el video se repita
            videoView.start() // Iniciar la reproducción
        }

    }
}