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
        val listButton = findViewById<Button>(R.id.buttonToCategories)

        // INICIO DEL FOOTER
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Si tiene la cuenta iniciada, será true
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val profileButton: Button = findViewById(R.id.profileButton)
        val misobjetivosbutton: Button = findViewById(R.id.MisObjetivos)
        val homeButton: Button = findViewById(R.id.homeButton)

        // Al pulsar el botón de Perfil, si tiene cuenta lo manda a su perfil. Sino, lo manda a iniciar sesión
        profileButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            /* condicional de saver si se ha iniciado o no:
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            */

        }

        //Al pulsar mis objetivos
        misobjetivosbutton.setOnClickListener{


        }

        //te manda a inicio "home, esta misma"
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //te manda a ver las categorias
        listButton.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }
        // FIN DEL FOOTER

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