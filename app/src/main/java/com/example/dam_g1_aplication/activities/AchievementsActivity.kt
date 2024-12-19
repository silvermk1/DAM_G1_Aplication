package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AchievementsActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements_activity)

        // Inicializar el contenedor donde se añadirán los botones
        val achievementContainer = findViewById<LinearLayout>(R.id.achievementContainer)

        // Preparar conexión de Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        //Obtiene el category id que se ha pulsado para llegar aquí
        val categoryId = intent.getStringExtra("CATEGORY_ID")?.toInt() ?: return
        val title = intent.getStringExtra("TITLE")

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = title

        //retornar los logros segun el achievement
        val callAchievementsByCategoryId = apiService.getAchievementsByCategoryId(categoryId)
        callAchievementsByCategoryId.enqueue(object : Callback<List<Achievements>> {
            override fun onResponse(
                call: Call<List<Achievements>>,
                response: Response<List<Achievements>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val achievements = response.body()!!

                    // Crea dinámicamente un botón para cada logro
                    for (achievement in achievements) {
                        val achievementLayout = LinearLayout(this@AchievementsActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 8, 0, 8)
                            }
                            orientation = LinearLayout.HORIZONTAL
                        }

                        val favoriteButton = Button(this@AchievementsActivity).apply {
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
                                    this@AchievementsActivity,
                                    "Favorito seleccionado: ${achievement.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        val achievementButton = Button(this@AchievementsActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                            ).apply {
                                setMargins(16, 8, 16, 8)
                            }

                            text = achievement.title

                            // Fondo con esquinas redondeadas
                            background = GradientDrawable().apply {
                                cornerRadius = 8 * resources.displayMetrics.density
                                setColor(resources.getColor(android.R.color.holo_orange_dark))
                            }

                            setTextColor(resources.getColor(android.R.color.white))
                            setOnClickListener {
                                navigateToAchievements(achievement)
                            }
                        }

                        achievementLayout.addView(favoriteButton)
                        achievementLayout.addView(achievementButton)
                        achievementContainer.addView(achievementLayout)
                    }


                } else {
                    Toast.makeText(this@AchievementsActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Achievements>>, t: Throwable) {
                Toast.makeText(this@AchievementsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToAchievements(achievement: Achievements) {
        val intent = Intent(this, AchievementDetailActivity::class.java).apply {
            putExtra("ACHIEVEMENT_ID", achievement.id)
            putExtra("TITLE", achievement.title)
            putExtra("DESCRIPTION", achievement.description)
        }
        startActivity(intent)
    }

    // Función de extensión para convertir dp a px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
