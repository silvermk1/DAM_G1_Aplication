package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

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

    private lateinit var usernameTextView: TextView
    private lateinit var mailTextView: TextView
    private lateinit var biographyTextMultiLine: EditText
    private lateinit var saveButton: Button
    private lateinit var username: String
    private lateinit var mail: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.getString("userId", null)

        username = sharedPreferences.getString("username", null).toString()
        mail = sharedPreferences.getString("mail", null).toString()

        usernameTextView = findViewById(R.id.usernameTextView)
        mailTextView = findViewById(R.id.mailTextView)
        biographyTextMultiLine = findViewById(R.id.biographyTextMultiLine)
        saveButton = findViewById(R.id.saveButton)

        usernameTextView.text = username
        mailTextView.text = mail

        // FOOTER
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        // FOOTER
    }

//METODO PARA INGRESAR EL NOMBRE DE USUARIO QUE BUSCAS, SE ABRE UNA VENTANA EMERGENTE--
    private fun busqueda() {

        val usuariobuscado = EditText(this)
        usuariobuscado.hint = "Ingrese nombre usuario"

        // Crear cuadro emergente para agregar valor al edittext anterior
        val dialog = AlertDialog.Builder(this)
            .setMessage("¿A quien buscas?:")
            .setView(usuariobuscado) // Añadir el campo de texto al diálogo
            .setPositiveButton("Buscar") { dialog, _ ->
                val enteredText = usuariobuscado.text.toString()
                usuariobuscadotexto = usuariobuscado.text.toString()
                solicitarusuarioBuscado(usuariobuscadotexto)

                Toast.makeText(this, "Texto ingresado: $enteredText", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Cerrar el diálogo
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Solo cerrar el diálogo
            }
            .create()

        dialog.show()
        //si el usuario buscado texto es null
        if(usuariobuscadotexto == null){
            solicitarusuariosAgregarlista()
        }
        //si el usuario buscado texto no es null
        if (usuariobuscadotexto != null){
            solicitarusuarioBuscado(usuariobuscadotexto)
        }
    }

//LLENAR EL LISTVIEW CON SOLO EL USUARIO BUSCADO
    private fun solicitarusuarioBuscado(usuariobuscado: String) {
        userslist.clear()

        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        apiService.getAllUsers().enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    val filteredUsers = users.filter { it.username == usuariobuscado }

                    if (filteredUsers.isNotEmpty()) {
                        userslist.addAll(filteredUsers.map { it.username })
                    } else {
                        Toast.makeText(this@ProfileActivity, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    println("Error en la respuesta")
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                println("Error en la conexión: ${t.message}")
            }
        })
    }

//LLENAR LISTVIEW CON TODOS LOS USUARIOS
    private fun solicitarusuariosAgregarlista() {
        userslist.clear()

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

