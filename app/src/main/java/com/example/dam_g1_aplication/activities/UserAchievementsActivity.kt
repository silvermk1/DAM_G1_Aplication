package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.AchievementsFavorites
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import com.example.dam_g1_aplication.utils.NavigationMenuHandler
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAchievementsActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigationMenuHandler: NavigationMenuHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_achievements_activity)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = sharedPreferences.getString("user_id", null)?.toLongOrNull()
        val completedListView: ListView = findViewById(R.id.completedAchievements)

        // Preparar conexión de Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        if (isLoggedIn && userId != null) {
            fillAchievementsListView(apiService, userId, completedListView)
        } else {
            val favoriteItems = listOf("Inicia sesión para poder ver los logros completados")
            val adapter = ArrayAdapter(
                this@UserAchievementsActivity,
                android.R.layout.simple_list_item_1,
                favoriteItems
            )
            completedListView.adapter = adapter
        }


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


    private fun fillAchievementsListView(
        apiService: ApiService,
        userId: Long,
        completedListView: ListView
    ) {
        val call = apiService.findUserAchievementsByUserId(userId)
        call.enqueue(object : Callback<List<UserAchievements>> {
            override fun onResponse(
                call: Call<List<UserAchievements>>,
                response: Response<List<UserAchievements>>
            ) {
                val achievementsCompleted = response.body()
                // Si la llamada de la API devuelve una lista vacía, muestra un ítem en el ListView informándolo
                if (achievementsCompleted.isNullOrEmpty()) {
                    val favoriteItems = listOf("No tienes logros completados")
                    val adapter = ArrayAdapter(
                        this@UserAchievementsActivity,
                        android.R.layout.simple_list_item_1,
                        favoriteItems
                    )
                    completedListView.adapter = adapter
                } else {
                    // Si la llamada devuelve una lista que sí tiene objetos, rellenamos el ListView con un ítem por cada favorito
                    val favoriteItems = mutableListOf<String>()
                    val pendingCalls = achievementsCompleted.size
                    // Cada favorito realiza una segunda búsqueda para obtener el título y descripción de cada logro
                    achievementsCompleted.forEach { achievement ->
                        searchForAchievementInfo(
                            apiService,
                            achievement,
                            favoriteItems,
                            pendingCalls,
                            completedListView
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<UserAchievements>>, t: Throwable) {
                // Manejo del error de la llamada a la API
                val favoriteItems = listOf("Error al cargar los logros completados: ${t.message}")
                val adapter = ArrayAdapter(
                    this@UserAchievementsActivity,
                    android.R.layout.simple_list_item_1,
                    favoriteItems
                )
                completedListView.adapter = adapter
            }
        })
    }


    private fun searchForAchievementInfo(
        apiService: ApiService,
        achievements: UserAchievements,
        favoriteItems: MutableList<String>,
        pendingCalls: Int,
        favoritesListView: ListView
    ) {
        apiService.getAchievementById(achievements.achievementid).enqueue(object :
            Callback<Achievements> {
            override fun onResponse(
                call: Call<Achievements>,
                response: Response<Achievements>
            ) {
                if (response.isSuccessful) {
                    val achievement = response.body()
                    if (achievement != null) {
                        favoriteItems.add(" ${achievement.title} - ${achievement.description}")
                    }
                } else {
                    favoriteItems.add("Error al obtener el logro con ID: ${achievements.achievementid}")
                }
                checkIfAllCallsCompleted(
                    pendingCalls,
                    favoriteItems,
                    favoritesListView
                )
            }

            override fun onFailure(call: Call<Achievements>, t: Throwable) {
                // Manejo de errores
                favoriteItems.add("Error de red al obtener el logro con ID: ${achievements.achievementid}")
                checkIfAllCallsCompleted(
                    pendingCalls,
                    favoriteItems,
                    favoritesListView
                )
            }

            private fun checkIfAllCallsCompleted(
                pendingCalls: Int,
                favoriteItems: MutableList<String>,
                favoritesListView: ListView
            ) {
                if (favoriteItems.size == pendingCalls) {
                    val adapter = ArrayAdapter(
                        this@UserAchievementsActivity,
                        android.R.layout.simple_list_item_1,
                        favoriteItems
                    )
                    favoritesListView.adapter = adapter
                }
            }
        })
    }
}





















