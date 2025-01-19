package com.example.dam_g1_aplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.utils.NavigationMenuHandler
import com.google.android.material.navigation.NavigationView

class SupportActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var supportTextView: TextView
    private lateinit var contactButton: Button
    private lateinit var queEsButton: Button
    private var contactPressed = false
    private var queEsPressed = false
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigationMenuHandler: NavigationMenuHandler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_detail)
        titleTextView = findViewById(R.id.titleTextView)
        supportTextView = findViewById(R.id.supportTextView)
        queEsButton = findViewById(R.id.queEsButton)
        contactButton = findViewById(R.id.contactButton)

        // MENU HAMBURGUESA
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.nav_view)

        //Acciones de los botones del menú
        navigationMenuHandler = NavigationMenuHandler(this, drawerLayout, navigationView, isLoggedIn)
        navigationMenuHandler.setupMenu()

        // Establecer el comportamiento del botón hamburguesa
        menuButton.setOnClickListener {
            // Abrir o cerrar el menú lateral (DrawerLayout)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

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
            queEsButton.setBackgroundColor(0x20D080)
        } else {
            supportTextView.text = ""
            queEsButton.setBackgroundColor(0x202020)
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
            contactButton.setBackgroundColor(0x20D080)
        } else {
            supportTextView.text = ""
            contactButton.setBackgroundColor(0x202020)
        }
    }
}
