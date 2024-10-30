package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailregistro: EditText
    private lateinit var usuarioregistro: EditText
    private lateinit var contraseñaregistro: EditText
    private lateinit var añoregistro: EditText
    private lateinit var textosalida: TextView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register_activity)

        // Obtener el botón por id
        val botonregistro: Button = findViewById(R.id.BotonRegistro)

        // Obtener todos los edittext con cada dato de registro
        emailregistro = findViewById(R.id.EmailRegistro)
        usuarioregistro = findViewById(R.id.UsuarioRegistro)
        contraseñaregistro = findViewById(R.id.ContraseñaRegistro)
        añoregistro = findViewById(R.id.AñoNacimientoRegistro)

        // Edit text de salida
        textosalida = findViewById(R.id.TextoRegistro)

        // Clicklistener para botón de registro
        botonregistro.setOnClickListener {
            registrarse()
        }

        // Inicializa Retrofit y ApiService
        val retrofit = RetrofitClient.getClient()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun registrarse() {
        // Recibir datos
        val email = emailregistro.text.toString()
        val usuario = usuarioregistro.text.toString()
        val contraseña = contraseñaregistro.text.toString()
        val año = añoregistro.text.toString()

        // Comprobar que todos los datos han sido introducidos
        if (email.isEmpty() || usuario.isEmpty() || contraseña.isEmpty() || año.isEmpty()) {
            textosalida.text = "Por favor, complete todos los datos!"
            return
        } else { //todos los datos introduzidos, empezar registro:

            // Crear nuevo usuario nuevo
            val nuevoUsuario = Users(
                id = "", // Deja vacío si es generado por el servidor
                username = usuario,
                password = contraseña,
                birthday = año,
                biography = "",
                mail = email
            )

            // Enviar usuario
            val callRegister = apiService.createUser(nuevoUsuario)
            callRegister.enqueue(object : Callback<Users> {
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registro exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        //Puedes hacer algo  mas aqui...
                    } else { //error
                        val errorResponse =
                            response.errorBody()?.string()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error en el registro: ${response.message()} - $errorResponse",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Users>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}


