package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Categories
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_activity)

        // Inicializar el contenedor donde se añadirán los botones
        val categoryContainer = findViewById<LinearLayout>(R.id.categoryContainer)

        // Preparar conexión de Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        //retornar todas las categorias
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
                        val categoryLayout = LinearLayout(this@CategoriesActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 8, 0, 8)
                            }
                            orientation = LinearLayout.HORIZONTAL
                        }

                        val favoriteButton = Button(this@CategoriesActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                75.dpToPx(), // tamaño fijo para el botón de favoritos
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(8, 8, 8, 8)
                            }

                            text = "❤︎︎"

                            // Fondo con esquinas redondeadas
                            background = GradientDrawable().apply {
                                cornerRadius = 8 * resources.displayMetrics.density // Convertir dp a píxeles
                                setColor(resources.getColor(android.R.color.holo_red_light))
                            }

                            setOnClickListener {
                                Toast.makeText(
                                    this@CategoriesActivity,
                                    "Favorito seleccionado: ${category.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        val categoryButton = Button(this@CategoriesActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                            ).apply {
                                setMargins(16, 8, 16, 8)
                            }

                            text = category.name

                            // Fondo con esquinas redondeadas
                            background = GradientDrawable().apply {
                                cornerRadius = 8 * resources.displayMetrics.density
                                setColor(resources.getColor(android.R.color.holo_orange_dark))
                            }

                            setTextColor(resources.getColor(android.R.color.white))
                            setOnClickListener {
                                navigateToAchievements(category)
                            }
                        }

                        categoryLayout.addView(favoriteButton)
                        categoryLayout.addView(categoryButton)
                        categoryContainer.addView(categoryLayout)
                    }

                } else {
                    Toast.makeText(this@CategoriesActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Categories>>, t: Throwable) {
                Toast.makeText(this@CategoriesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToAchievements(category: Categories) {
        val intent = Intent(this, AchievementsActivity::class.java).apply {
            putExtra("CATEGORY_ID", category.id)
            putExtra("TITLE", category.name)
        }
        startActivity(intent)
    }

    // Función de extensión para convertir dp a px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}