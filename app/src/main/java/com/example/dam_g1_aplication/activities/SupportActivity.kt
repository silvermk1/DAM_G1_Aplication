package com.example.dam_g1_aplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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

    //atributos para el boton hamburguesa:
    private lateinit var panelMenu: LinearLayout
    private lateinit var menuButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_detail)
        titleTextView = findViewById(R.id.titleTextView)
        supportTextView = findViewById(R.id.supportTextView)
        queEsButton = findViewById(R.id.queEsButton)
        contactButton = findViewById(R.id.contactButton)

//MENU INTERACTIVO HAMBURGUESA
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        menuButton = findViewById(R.id.menuButton)
        panelMenu = findViewById(R.id.panelMenu)

        // Cargar las animaciones
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        // Al presionar el ImageView (menú)
        menuButton.setOnClickListener {
            if (panelMenu.visibility == View.GONE) {
                // Mostrar el panel con animación
                panelMenu.startAnimation(slideUp)
                panelMenu.visibility = View.VISIBLE
            } else {
                // Ocultar el panel con animación
                panelMenu.startAnimation(slideDown)
                panelMenu.visibility = View.GONE
            }
        }

        // Configurar botones del panel (opcional)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button1.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }        }

//MENU INTERACTIVO HAMBURGUESA FINAL

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
            supportTextView.text = "Esta aplicación contiene un sinfín de logros de todo tipo para completar en la vida real, desde logros más cotidianos y generales a otros más específicos y concretos. ¡Es entretenimiento en estado puro, sobretodo para entretenerse en tiempos muertos!\""
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
