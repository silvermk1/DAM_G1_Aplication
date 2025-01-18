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

        // Establecer el comportamiento del botón hamburguesa
        menuButton.setOnClickListener {
            // Abrir o cerrar el menú lateral (DrawerLayout)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        escuchadebotonesmenu()
    }


    //METODOS MENU HAMBURGUESA
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun escuchadebotonesmenu() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_perfil -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    if (isLoggedIn) {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_logros -> {
                    Toast.makeText(this, "Logros", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AchievementDetailActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_categorias -> {
                    Toast.makeText(this, "Categorías", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CategoriesActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_iniciar -> {
                    if (isLoggedIn) {
                        Toast.makeText(this, "Cierra la sesion!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_cerrar -> {
                    sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    sharedPreferences.getString("user_id", null)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        remove("username")
                        remove("user_id")
                        remove("mail")
                        apply()
                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Sesion cerrada, Adios!", Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.nav_contactos -> {
                    if (isLoggedIn) {
                        Toast.makeText(this, "Contactos", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, FriendsActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Inicie sesion Antes!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                R.id.nav_soporte -> {
                    Toast.makeText(this, "Sopporte", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_compartir -> {
                    if (isLoggedIn) {
                        val intent = Intent(this, ProfileSocialActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Inicia Sesión para acceder a este menú",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            // Cierra el Drawer después de la selección
            drawerLayout.closeDrawers()
            true
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





















