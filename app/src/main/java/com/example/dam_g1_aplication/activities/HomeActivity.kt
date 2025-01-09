package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.AchievementsFavorites
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        val categoriesButton: Button = findViewById(R.id.categoriesButton)
        val searcherButton : Button = findViewById(R.id.searchButton)
        val favoritesListView: ListView = findViewById(R.id.listViewFavorites)
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = sharedPreferences.getString("user_id", null)?.toLongOrNull()

        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        if (isLoggedIn && userId != null) {

            fillFavoriteListView(apiService, userId, favoritesListView)

        } else {
            val favoriteItems = listOf("Inicia sesión para poder ver los logros favoritos")
            val adapter = ArrayAdapter(
                this@HomeActivity,
                android.R.layout.simple_list_item_1,
                favoriteItems
            )
            favoritesListView.adapter = adapter
        }

        searcherButton.setOnClickListener {
            val intent = Intent(this, SearcherActivity::class.java)
            startActivity(intent)
        }

        categoriesButton.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }

// MENU HAMBURGUESA

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
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
                        Toast.makeText(this, "Cierra la sesion!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_cerrar -> {
                    sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    sharedPreferences.getString("user_id", null)
                    if (isLoggedIn) {

                            with(sharedPreferences.edit()) {
                                putBoolean("isLoggedIn", false)
                                remove("username")
                                remove("user_id")
                                remove("mail")
                                apply()

                            }
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)


                        Toast.makeText(this, "Sesion cerrada, Adios!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Ya estava cerrada!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)

                    }
                }

                R.id.nav_contactos -> {

                    if (isLoggedIn) {
                        Toast.makeText(this, "Contactos", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, FriendsActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Inicie sesion Antes!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)

                    }
                }
                R.id.nav_soporte -> {
                    Toast.makeText(this, "Sopporte", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_compartir -> {
                    Toast.makeText(this, "Gracias por comparitr (:", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileSocialActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    Toast.makeText(this, "Opción desconocida", Toast.LENGTH_SHORT).show()
                }
            }
            // Cierra el Drawer después de la selección
            drawerLayout.closeDrawers()
            true
        }
    }
//OTROS METODOS CODIGO
    private fun fillFavoriteListView(
        apiService: ApiService,
        userId: Long,
        favoritesListView: ListView
    ) {
        val call = apiService.getAchievementsFavoritesByUserId(userId)
        call.enqueue(object : Callback<List<AchievementsFavorites>> {
            override fun onResponse(
                call: Call<List<AchievementsFavorites>>,
                response: Response<List<AchievementsFavorites>>
            ) {
                val achievementsFavorites = response.body()
                //Si la llamada de la api devuelve una lista vacia muestra un item en el listview informandolo
                if (achievementsFavorites.isNullOrEmpty()) {
                    val favoriteItems = listOf("No tienes logros favoritos")
                    val adapter = ArrayAdapter(
                        this@HomeActivity,
                        android.R.layout.simple_list_item_1,
                        favoriteItems
                    )
                    favoritesListView.adapter = adapter
                } else {
                    //Si la llamada devuelve una lista que si tiene objetos rellenamos el list view con un item por cada favorito
                    val favoriteItems = mutableListOf<String>()
                    val pendingCalls = achievementsFavorites.size
                    //Cada favorito realiza una seunda busqueda para obtener el titulo y descripcion de cada logro
                    achievementsFavorites.forEach { favorite ->
                        searchForAchievementInfo(
                            apiService,
                            favorite,
                            favoriteItems,
                            pendingCalls,
                            favoritesListView
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<AchievementsFavorites>>, t: Throwable) {
                val favoriteItems = listOf("Error de conexión")
                val adapter = ArrayAdapter(
                    this@HomeActivity,
                    android.R.layout.simple_list_item_1,
                    favoriteItems
                )
                favoritesListView.adapter = adapter
            }
        })
    }

    private fun searchForAchievementInfo(
        apiService: ApiService,
        favorite: AchievementsFavorites,
        favoriteItems: MutableList<String>,
        pendingCalls: Int,
        favoritesListView: ListView
    ){
        apiService.getAchievementById(favorite.achievementId).enqueue(object :
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
                    favoriteItems.add("Error al obtener el logro con ID: ${favorite.achievementId}")
                }
                checkIfAllCallsCompleted(
                    pendingCalls,
                    favoriteItems,
                    favoritesListView
                )
            }

            override fun onFailure(
                call: Call<Achievements>,
                t: Throwable
            ) {
                favoriteItems.add("Error al obtener el logro con ID: ${favorite.achievementId}")
                checkIfAllCallsCompleted(
                    pendingCalls,
                    favoriteItems,
                    favoritesListView
                )
            }
        })
    }

    private fun checkIfAllCallsCompleted(
        pendingCalls: Int,
        favoriteItems: MutableList<String>,
        favoritesListView: ListView
    ) {
        if (favoriteItems.size == pendingCalls) {
            val adapter = ArrayAdapter(
                this@HomeActivity,
                android.R.layout.simple_list_item_1,
                favoriteItems
            )
            favoritesListView.adapter = adapter
        }
    }

}