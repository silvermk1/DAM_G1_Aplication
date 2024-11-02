package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.dataClasses.Users
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    //inciar login activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        //recibir objetos graficos
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        //iniciar servicio api
        val retrofit = RetrofitClient.getClient()

        apiService = retrofit.create(ApiService::class.java)

        //comprovar ingreso de datos "vacios..."
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                //checkcredentials = comprovar inicio de sesion
                checkCredentials(username, password)
            }
        }

        //mandar al activity de registro
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //metodo para comprovar inicio de sesion
    private fun checkCredentials(username: String, password: String) {
        //retornar usuarios
        val call = apiService.getUsers()
        call.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    //comrovar inicio sesion
                    val user = users.find { it.username == username && it.password == password }
                    if (user != null) { //inicio correcto, mandar al activity adecuado
                        with(sharedPreferences.edit()) {
                            putBoolean("isLoggedIn", true)
                            putString("username", username)
                            putString("user_id", user.id)
                            putString("mail", user.mail)
                            apply()
                        }
                        Toast.makeText(this@LoginActivity,
                            "Bienvenido", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else { //inicio incorrecto, lanzar mensaje
                        Toast.makeText(this@LoginActivity,
                            "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity,
                        "Error al obtener los usuarios", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                Toast.makeText(this@LoginActivity,
                    "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}