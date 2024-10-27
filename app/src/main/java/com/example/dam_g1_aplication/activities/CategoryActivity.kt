package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Categories
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CategoryActivity : AppCompatActivity() {

    // Crea botones por cada categoría
    // NOTA: Habría que hacer que genere tantos botones como categorias haya disponibles
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_activity)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)

        // INICIO DEL FOOTER
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Si tiene la cuenta iniciada, será true
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)

        // Al pulsar el botón de Perfil, si tiene cuenta lo manda a su perfil. Sino, lo manda a iniciar sesión
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
        // FIN DEL FOOTER

        // Prepara conexión del RetroFit
        val retrofit = Retrofit.Builder()
            // IP PRIVADA DEL BACKEND, NO LOCALHOSTS
            .baseUrl("http://192.168.1.21:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val callCategories = apiService.getCategories()
        callCategories.enqueue(object : Callback<List<Categories>> {
            override fun onResponse(
                call: Call<List<Categories>>,
                response: Response<List<Categories>>
            ) {
                // Si el backend responde con éxito, y no es nulo, procede
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!

                    button1.text = categories[0].name
                    button1.setOnClickListener {
                        navigateToAchievements(categories[0].id)
                    }

                    button2.text = categories[1].name
                    button2.setOnClickListener {
                        navigateToAchievements(categories[1].id)
                    }

                    button3.text = categories[2].name
                    button3.setOnClickListener {
                        navigateToAchievements(categories[2].id)
                    }

                    button4.text = categories[3].name
                    button4.setOnClickListener {
                        navigateToAchievements(categories[3].id)
                    }
                    button5.text = categories[4].name
                    button5.setOnClickListener {
                        navigateToAchievements(categories[4].id)
                    }

                    button6.text = categories[5].name
                    button6.setOnClickListener {
                        navigateToAchievements(categories[5].id)
                    }

                } else {
                    Toast.makeText(this@CategoryActivity, "Error en la respuesta", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<Categories>>, t: Throwable) {
                Toast.makeText(
                    this@CategoryActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun navigateToAchievements(categoryId: String) {
        val intent = Intent(this, AchievementsListActivity::class.java)
        intent.putExtra("CATEGORY_ID", categoryId)
        startActivity(intent)
    }
}