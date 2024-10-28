package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_activity)

        // Inicializar el contenedor donde se añadirán los botones
        val categoryContainer = findViewById<LinearLayout>(R.id.categoryContainer)

        // Preparar conexión de Retrofit
        val retrofit = Retrofit.Builder()
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
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!

                    // Crea dinámicamente un botón para cada categoría
                    for (category in categories) {
                        val categoryLayout = LinearLayout(this@CategoryActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 8, 0, 8)
                            }
                            orientation = LinearLayout.HORIZONTAL
                        }

                        val favoriteButton = Button(this@CategoryActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                75.dpToPx(), // tamaño fijo para el botón de favoritos
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            text = "❤︎︎"
                            setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                            setOnClickListener {
                                // Acción para el botón de favoritos
                                Toast.makeText(
                                    this@CategoryActivity,
                                    "Favorito seleccionado: ${category.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        val categoryButton = Button(this@CategoryActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                            text = category.name
                            setBackgroundColor(resources.getColor(android.R.color.holo_orange_dark))
                            setTextColor(resources.getColor(android.R.color.white))
                            setOnClickListener {
                                navigateToAchievements(category.id)
                            }
                        }

                        // Añadir los botones al layout de cada categoría
                        categoryLayout.addView(favoriteButton)
                        categoryLayout.addView(categoryButton)

                        // Añadir el layout de la categoría al contenedor principal
                        categoryContainer.addView(categoryLayout)
                    }

                } else {
                    Toast.makeText(this@CategoryActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Categories>>, t: Throwable) {
                Toast.makeText(this@CategoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToAchievements(categoryId: String) {
        val intent = Intent(this, AchievementsListActivity::class.java)
        intent.putExtra("CATEGORY_ID", categoryId)
        startActivity(intent)
    }

    // Función de extensión para convertir dp a px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
