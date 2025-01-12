package com.example.dam_g1_aplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class SignupActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var year: EditText
    private lateinit var textSignup: TextView
    private lateinit var apiService: ApiService

    //iniciar registreractivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_activity)

        // Obtener el botón por id
        val signupButton: Button = findViewById(R.id.BotonRegistro)

        email = findViewById(R.id.email)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        year = findViewById(R.id.year)
        textSignup = findViewById(R.id.textSignup)

        // Clicklistener para botón de registro
        signupButton.setOnClickListener {
            registrarse()
        }

        // Inicializa Retrofit y ApiService
        val retrofit = RetrofitClient.getClient()
        apiService = retrofit.create(ApiService::class.java)
    }
    //metodo para registrarse,
    @SuppressLint("SetTextI18n")
    private fun registrarse() {
        // Recibir datos
        val emailregister = email.text.toString()
        val usernameregister = username.text.toString()
        val passwordregister = password.text.toString()
        val yearregister = year.text.toString()

        // Comprobar que todos los datos han sido introducidos
        if (emailregister.isEmpty() || usernameregister.isEmpty() || passwordregister.isEmpty() || yearregister.isEmpty()) {
            textSignup.text = "Por favor, complete todos los datos!"
            return
        } else { //todos los datos introduzidos, empezar registro:
            //IMAGEN POR DEFECTO
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.iconomas, null)

            val bitmap = (drawable as BitmapDrawable).bitmap
            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream) // Reducir calidad
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Crear nuevo usuario nuevo
            val nuevoUsuario = Users(
                id = "", // Deja vacío si es generado por el servidor
                username = usernameregister,
                password = passwordregister,
                birthday = yearregister,
                biography = "",
                profilephoto = base64String,
                mail = emailregister
            )

            // guardar usuario a la bd api
            val callRegister = apiService.createUser(nuevoUsuario)
            callRegister.enqueue(object : Callback<Users> {
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@SignupActivity,
                            "Registro exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        //Puedes hacer algo  mas aqui...
                    } else { //error
                        val errorResponse =
                            response.errorBody()?.string()
                        Toast.makeText(
                            this@SignupActivity,
                            "Error en el registro: ${response.message()} - $errorResponse",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Users>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }
}


