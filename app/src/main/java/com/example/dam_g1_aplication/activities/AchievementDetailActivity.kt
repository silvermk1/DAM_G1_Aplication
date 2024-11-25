package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievement_detail_activity)

        // Inicializar ApiService usando RetrofitClient
        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        completeButton = findViewById(R.id.completeButton)

        // FOOTER
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        supportButton.setOnClickListener {
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

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        // FOOTER

        userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(this, "Error al cargar el logro", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        achievementId = intent.getStringExtra("ACHIEVEMENT_ID")

        completeButton.setOnClickListener {
            toggleCompletionStatus()
        }

        loadAchievementDetails()
        checkUserAchievementStatus()
    }

    private fun toggleCompletionStatus() {
        if (isCompleted) {
            // Eliminar el logro
            val callDelete = apiService.deleteUserAchievement(achievementId!!.toLong(), userId!!.toLong())
            callDelete.enqueue(object : Callback<Void> {
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
            // Crear el logro
            val callCreate = apiService.createUserAchievement(achievementId, userId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AchievementDetailActivity, "Logro creado correctamente", Toast.LENGTH_SHORT).show()
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