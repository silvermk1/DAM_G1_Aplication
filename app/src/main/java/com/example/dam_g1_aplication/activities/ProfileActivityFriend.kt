package com.example.dam_g1_aplication.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.R


class ProfileActivityFriend : AppCompatActivity() {

    lateinit var textousuario: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity_friend)

//recoger el nombre de usuario con getString
        val usuario = intent.getStringExtra("nombreusuario")

//Declarar atributos con sus respectivos objetos
        textousuario = findViewById(R.id.usuario)

        //agregar usuario al list view
        textousuario.setText(usuario)
    }


}