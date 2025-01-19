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
import com.example.dam_g1_aplication.utils.NavigationMenuHandler
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
    private lateinit var navigationMenuHandler: NavigationMenuHandler


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

    //METODOS MENU HAMBURGUESA
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
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