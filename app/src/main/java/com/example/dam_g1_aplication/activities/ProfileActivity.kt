package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Categories
import com.example.dam_g1_aplication.dataClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    lateinit var textousuario: TextView
    lateinit var buscarpersona : Button
    var usuariobuscadotexto = "false"
    lateinit var listausuarios : ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity)

//RECIBIR LOS PARAMETROS DEL USUARIO INICIADO QUE CONTIENE EL XML DE CONFIGURACIONES---
        //recibir el nombre de usuario
        val login = LoginActivity()
        val array = login.retornarusuarioiniciado(this)
        val usuarionombre = array[1]

//INSTANCIAR LOS ATRIBUTOS GRAFICOS Y TEXTUALES---
        //instanciar el textview con el nombre usuario
        textousuario = findViewById(R.id.usuario)
        buscarpersona = findViewById(R.id.buscarusuario)
        usuariobuscadotexto = "false"
        listausuarios = findViewById(R.id.otrosPerfiles)

//AGREGAR LOS DATOS A TODOS LOS ELEMENTOS GRAFICOS
        //1.agregar el nombre de usuario al textview
        textousuario.setText(usuarionombre)

        //2.agregar usuarios al listview
        solicitarusuariosAgregarlista()

//CLICKS LISTENERS --
        //ingresar persona que buscas al clickear boton
        buscarpersona.setOnClickListener {
            busqueda()
        }

        //clicklistener para mostrar perfil de un usuario ajeno:
        //crear intent que te dirija acitivityFriend
        listausuarios.setOnItemClickListener { parent, view, position, id ->
            val posicionclickeada = parent.getItemAtPosition(position) as String
            val intent = Intent(this, ProfileActivityFriend::class.java)
            intent.putExtra("nombreusuario", posicionclickeada)
            startActivity(intent)
        }

    }

//METODO PARA INGRESAR EL NOMBRE DE USUARIO QUE BUSCAS, SE ABRE UNA VENTANA EMERGENTE--
    private fun busqueda() {

        val usuariobuscado = EditText(this)
        usuariobuscado.hint = "Ingrese nombre usuario"

        // Crear cuadro emergente para agregar valor al edittext anterior
        val dialog = AlertDialog.Builder(this)
            .setTitle("USUARIO")
            .setMessage("¿A quien buscas?:")
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

    private fun solicitarusuariosAgregarlista() {
    //CREAR LISTA PARA AGREGAR LOS NOMBRES DE USUARIOS
        val userslist = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userslist)
        listausuarios.adapter = adapter

        //RETORNAR TODOS LOS USUARIOS CON RETROFIT
        // Preparar conexión de Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getAllUsers()
        call.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    for (user in users) {
    //AGREGAR LOS USUARIOS AL LISTVIEW
                        val usuario =user.username.toString()
                        userslist.add(usuario)
                    }
    //ACTUALIZAR DATOS ADAPTADOR Y AGREGAR EL ADAPTER AL LISTVIEW
                    adapter.notifyDataSetChanged()
                    listausuarios.adapter = adapter

                } else {
                    println("Error en la respuesta")
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                println("Error en la conexión: ${t.message}")
            }
        })
    }
    }

