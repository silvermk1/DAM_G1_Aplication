package com.example.dam_g1_aplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.VideoView
import android.widget.MediaController
import android.net.Uri
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Obtén el VideoView del layout
        val videoView = findViewById<VideoView>(R.id.videoView)

        // Ruta al video en la carpeta raw
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.triangulo}")
        videoView.setVideoURI(videoUri)

        // Agregar MediaController para controles de video (opcional)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Iniciar el video cuando esté listo
        videoView.setOnPreparedListener {
            videoView.start()

            // Establecer un temporizador para redirigir después de 3 segundos (por ejemplo)
            Handler().postDelayed({
                // Al pasar el tiempo, cambia a la HomeActivity
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish() // Finaliza SplashActivity para que no se pueda volver atrás
            }, 2500)  // Retraso de 3000 ms (3 segundos)
        }

        // En caso de que haya algún problema al cargar el video
        videoView.setOnErrorListener { _, _, _ ->
            finish()  // Finaliza SplashActivity en caso de error
            true
        }
    }
}
