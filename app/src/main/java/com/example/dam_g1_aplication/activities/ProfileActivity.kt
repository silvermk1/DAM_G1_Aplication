package com.example.dam_g1_aplication.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.R

class ProfileActivity : AppCompatActivity() {

    lateinit var textousuario: TextView
    lateinit var buscarpersona : Button
    var usuariobuscadotexto = "false"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity)

//RECIBIR LOS PARAMETROS CON PUTEXTRA Y GETSTRING---
        //recibir el nombre de usuario
        println("debug1")
        val login = LoginActivity()
        println("debug2")
        val array = login.retornarusuarioiniciado(this)
        println("debug3")
        val usuarionombre = array[1]
        println("debug4")

        //val usuarionombre = intent.getStringExtra("usuarionombre")

//INSTANCIAR LOS ATRIBUTOS GRAFICOS Y TEXTUALES---
        //instanciar el textview con el nombre usuario
        textousuario = findViewById(R.id.usuario)
        buscarpersona = findViewById(R.id.buscarusuario)
        usuariobuscadotexto = "false"

//...---
        //agregar el nombre de usuario al textview
        textousuario.setText(usuarionombre)
        println("debug5")


//CLICKS LISTENERS --
        //ingresar persona que buscas al clickear boton
        buscarpersona.setOnClickListener {
            busqueda()
        }

        //clicklistener para mostrar todos los usuarios ajenos en el listview
        //la variable usuariosbuscadotexto alluda a buscar todos "false" o solo el introducido "usuario"


    }

//METODO PARA INGRESAR EL NOMBRE DE USUARIO QUE BUSCAS, SE ABRE UNA VENTANA EMERGENTE--
    private fun busqueda() {

        val usuariobuscado = EditText(this)
        usuariobuscado.hint = "Ingrese nombre usuario"

        // Crear cuadro emergente para agregar valor al edittext anterior
        val dialog = AlertDialog.Builder(this)
            .setTitle("Entrada de texto")
            .setMessage("Por favor, escriba algo:")
            .setView(usuariobuscado) // Añadir el campo de texto al diálogo
            .setPositiveButton("Aceptar") { dialog, _ ->
                val enteredText = usuariobuscado.text.toString()
                usuariobuscadotexto = usuariobuscado.text.toString()
                Toast.makeText(this, "Texto ingresado: $enteredText", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Cerrar el diálogo
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Solo cerrar el diálogo
            }
            .create()

        dialog.show()
    }
    }

