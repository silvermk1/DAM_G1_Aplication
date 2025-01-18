package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearcherActivity : AppCompatActivity() {

    //atributos para el boton hamburguesa:
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searcher_activity)

        val searchEditText: EditText = findViewById(R.id.searcherEditText)
        val searchButton: Button = findViewById(R.id.searchButton)
        val resultsListView: ListView = findViewById(R.id.resultsListView)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = sharedPreferences.getString("user_id", null)?.toLongOrNull()

        // Preparar Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Acción al hacer clic en el botón de búsqueda
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                // Llamar al API para obtener los logros
                searchAchievements(apiService, query, resultsListView)
            } else {
                Toast.makeText(this, "Por favor, introduce algo para buscar.", Toast.LENGTH_SHORT)
                    .show()
            }
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
                    val intent = Intent(this, UserAchievementsActivity::class.java)
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
                    } else {
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
                    } else {
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
                    } else {
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


    private fun searchAchievements(
        apiService: ApiService,
        query: String,
        resultsListView: ListView
    ) {
        //Realiza la llamada a la api pasando como parametro lo introducido en el buscador
        val call = apiService.searchAchievements(query)
        call.enqueue(object : Callback<List<Achievements>> {
            override fun onResponse(
                call: Call<List<Achievements>>,
                response: Response<List<Achievements>>
            ) {
                if (response.isSuccessful) {
                    val achievements = response.body()
                    //Si la respuesta es exitosa pero la lista es vacia lo informa
                    if (achievements.isNullOrEmpty()) {
                        val noResults = listOf("No existen logros que contengan '$query'.")
                        val adapter = ArrayAdapter(
                            this@SearcherActivity,
                            android.R.layout.simple_list_item_1,
                            noResults
                        )
                        resultsListView.adapter = adapter
                    } else {
                        // Crea una lista con el titulo y descripción de cada logro que tiene algun elemento coincidente con lo introducido.
                        val simpleItems = achievements.map {
                            "${it.title} - ${it.description}"
                        }
                        val adapter = ArrayAdapter(
                            this@SearcherActivity,
                            android.R.layout.simple_list_item_1,
                            simpleItems
                        )
                        resultsListView.adapter = adapter
                    }
                } else {
                    val errorItems = listOf("Error al obtener los logros.")
                    val adapter = ArrayAdapter(
                        this@SearcherActivity,
                        android.R.layout.simple_list_item_1,
                        errorItems
                    )
                    resultsListView.adapter = adapter
                }
            }


            override fun onFailure(call: Call<List<Achievements>>, t: Throwable) {
                val errorItems = listOf("Error de conexión: ${t.message}")
                val adapter = ArrayAdapter(
                    this@SearcherActivity,
                    android.R.layout.simple_list_item_1,
                    errorItems
                )
                resultsListView.adapter = adapter
            }
        })
    }
}