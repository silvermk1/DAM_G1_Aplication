package com.example.dam_g1_aplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.utils.NavigationMenuHandler
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AchievementDetailActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var completeButton: Button
    private var achievementId: String? = null
    private var userId: String? = null
    private var isCompleted = false
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigationMenuHandler: NavigationMenuHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievement_detail_activity)



        // Inicializar ApiService usando RetrofitClient
        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        completeButton = findViewById(R.id.completeButton)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        achievementId = intent.getStringExtra("ACHIEVEMENT_ID")

        completeButton.setOnClickListener {
            toggleCompletionStatus()
        }

        loadAchievementDetails()
        checkUserAchievementStatus()

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


    private fun toggleCompletionStatus() {
        if (isCompleted) {
            // Eliminar el logro
            val callDelete = apiService.deleteUserAchievement(achievementId!!.toLong(), userId!!.toLong())
            callDelete.enqueue(object : Callback<Void> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        isCompleted = false
                        completeButton.text = "No Completado"
                        completeButton.setBackgroundColor(getColor(android.R.color.holo_red_light))
                        Toast.makeText(this@AchievementDetailActivity, "Logro eliminado.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AchievementDetailActivity, "Error al eliminar el logro.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AchievementDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            apiService.createUserAchievement(achievementId, userId).enqueue(object : Callback<Void> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AchievementDetailActivity, "Logro creado correctamente", Toast.LENGTH_SHORT).show()
                        isCompleted = true
                        completeButton.text = "Completado"
                        completeButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
                    } else {
                        Toast.makeText(this@AchievementDetailActivity, "Error al crear el logro", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AchievementDetailActivity, "Error en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadAchievementDetails() {
        val title = intent.getStringExtra("TITLE")
        val description = intent.getStringExtra("DESCRIPTION")

        titleTextView.text = title
        descriptionTextView.text = description
    }

    private fun checkUserAchievementStatus() {
        val callUserAchievement = apiService.getUserAchievement(achievementId!!.toLong().toString(),
            userId!!.toLong().toString()
        )
        callUserAchievement.enqueue(object : Callback<UserAchievements> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<UserAchievements>, response: Response<UserAchievements>) {
                if (response.isSuccessful && response.body() != null) {
                    isCompleted = true
                    completeButton.text = "Completado"
                    completeButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
                } else {
                    isCompleted = false
                    completeButton.text = "No Completado"
                    completeButton.setBackgroundColor(getColor(android.R.color.holo_red_light))
                }
            }

            override fun onFailure(call: Call<UserAchievements>, t: Throwable) {
                Toast.makeText(this@AchievementDetailActivity, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}