package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.AchievementsFavorites
import com.example.dam_g1_aplication.utils.NavigationMenuHandler
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AchievementsActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigationMenuHandler: NavigationMenuHandler

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
                        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                        val userId = sharedPreferences.getString("user_id", null)?.toLongOrNull()
                        val achievementLayout = LinearLayout(this@AchievementsActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 8, 0, 8)
                            }
                            orientation = LinearLayout.HORIZONTAL
                        }

                        //LÓGICA BOTÓN FAVORITOS
                        val favoriteButton = Button(this@AchievementsActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                75.dpToPx(),
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )


                            // Texto y color predeterminado
                            text = "+"
                            setBackgroundColor(resources.getColor(android.R.color.holo_red_light))


                            if (isLoggedIn && userId != null) {
                                //Si estas logeado revisa tus favoritos y actualzia su estado
                                fillListViewLogged(userId, achievement)
                            }

                            // Alternar favoritos al hacer clic
                            setOnClickListener {
                                if (!isLoggedIn) {
                                    //Si no estas logado te muestra un mensaje informando que te tienes que logear para utilziar la función
                                    Toast.makeText(
                                        this@AchievementsActivity,
                                        "Inicia sesión para usar favoritos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@setOnClickListener
                                }

                                if (text == "+") {
                                    text = "❤︎︎"
                                    setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                                    //Función que hace llamada a la api para guardar el favorito
                                    saveNewFavorite(userId!!, achievement.id.toLong())
                                } else {
                                    text = "+"
                                    setBackgroundColor(resources.getColor(android.R.color.holo_red_light))

                                    //Función que hace llamada a la api para eliminar el favorito
                                    deleteFavorite(userId!!, achievement.id.toLong())
                                }
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
                                setColor(ContextCompat.getColor(context, R.color.light_purple))
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

// MENU HAMBURGUESA
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.nav_view)

        //Acciones de los botones del menú
        navigationMenuHandler = NavigationMenuHandler(this, drawerLayout, navigationView, isLoggedIn)
        navigationMenuHandler.setupMenu()

        // Establecer el comportamiento del botón hamburguesa
        menuButton.setOnClickListener {
            // Abrir o cerrar el menú lateral (DrawerLayout)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

    }

    private fun Button.fillListViewLogged(
        userId: Long,
        achievement: Achievements
    ) {
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val favoriteCheckCall =
            apiService.findUserFavoriteAchievementsByUserId(userId, achievement.id.toLong())

        favoriteCheckCall.enqueue(object : Callback<AchievementsFavorites> {
            override fun onResponse(
                call: Call<AchievementsFavorites>,
                response: Response<AchievementsFavorites>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // Si el logro es favorito, actualiza el estado inicial
                    text = "❤︎︎"
                    setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                }
            }

            override fun onFailure(call: Call<AchievementsFavorites>, t: Throwable) {
                Toast.makeText(
                    this@AchievementsActivity,
                    "Error al verificar favoritos",
                    Toast.LENGTH_SHORT
                ).show()
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

    //AGREGAR NUEVO FAVORITO
    fun saveNewFavorite(userId: Long, achievementId: Long) {
        // Crear el objeto friendrequest
        val achievementsFavorites = AchievementsFavorites(userId =userId, achievementId = achievementId)

        // Realizar la llamada a la API
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.createAchievementFavorite(achievementsFavorites)

        call.enqueue(object : Callback<AchievementsFavorites> {
            override fun onResponse(call: Call<AchievementsFavorites>, response: Response<AchievementsFavorites>) {
                if (response.isSuccessful) {
                    val createAchievementFavorite = response.body()
                    Toast.makeText(this@AchievementsActivity, "Logro añadido a favoritos", Toast.LENGTH_SHORT).show()
                    println("Logro añadido a favorito: $createAchievementFavorite")
                } else {
                    Toast.makeText(this@AchievementsActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AchievementsFavorites>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    // ELIMINAR FAVORITO
    fun deleteFavorite(userId: Long, achievementId: Long) {
        // Realizar la llamada a la API
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.deleteFavorite(userId, achievementId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AchievementsActivity,
                        "Logro eliminado de favoritos correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@AchievementsActivity,
                        "No se pudo eliminar el logro de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@AchievementsActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
