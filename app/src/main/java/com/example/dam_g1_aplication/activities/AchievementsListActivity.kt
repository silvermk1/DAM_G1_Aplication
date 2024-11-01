package com.example.dam_g1_aplication.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.adapters.AchievementsAdapter
import com.example.dam_g1_aplication.dataClasses.Achievements
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import retrofit2.Retrofit

class AchievementsListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.achiv_list_activity)

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.achievementsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Preparar conexión de Retrofit
        val retrofit = RetrofitClient.getClient()
        apiService = retrofit.create(ApiService::class.java)

        // Obtener el ID de la categoría de la intención
        val categoryId = intent.getStringExtra("CATEGORY_ID")?.toLongOrNull()
        if (categoryId != null) {
            fetchAchievements(categoryId)
        } else {
            Toast.makeText(this, "ID de categoría no válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAchievements(categoryId: Long) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getAchievementsByCategoryId(categoryId).execute()

                }
                if (response.isSuccessful && response.body() != null) {
                    val achievements = response.body()!!
                    Log.d("AchievementsListActivity", "Number of achievements received: ${achievements.size}")
                    recyclerView.adapter = AchievementsAdapter(achievements)
                } else {
                    Log.d("AchievementsListActivity", "No achievements available, assigning empty adapter")
                    recyclerView.adapter = AchievementsAdapter(emptyList())
                    Toast.makeText(this@AchievementsListActivity, "No hay logros disponibles", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@AchievementsListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
