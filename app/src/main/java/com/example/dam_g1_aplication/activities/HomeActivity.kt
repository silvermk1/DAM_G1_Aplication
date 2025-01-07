package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.AchievementsFavorites
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        val categoriesButton: Button = findViewById(R.id.categoriesButton)
        val searcherButton : Button = findViewById(R.id.searchButton)
        val favoritesListView: ListView = findViewById(R.id.listViewFavorites)
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
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

        // FOOTER
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

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
        apiService.getAchievementById(favorite.achievementId).enqueue(object : Callback<Achievements> {
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