package com.example.dam_g1_aplication.Mainclasses

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R


class HomeActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val listButton = findViewById<Button>(R.id.bVerLogros)

//HE PUESTO UN VIDEO DE FONDO AL HOME
        videoView = findViewById(R.id.videoView)
        // Configura la URI del video
        val videoUri: Uri = Uri.parse("android.resource://${packageName}/${R.raw.triangulo}") // Asegúrate de que "triangulo" sea el nombre correcto
        // Establece la URI del video en el VideoView
        videoView.setVideoURI(videoUri)
        // Configuración para que el video se repita
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true // Hacer que el video se repita
            videoView.start() // Iniciar la reproducción
        }

//CLICKLISTENER PARA EL BOTON
        listButton.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent) // Iniciar la nueva actividad


        }
    }
}