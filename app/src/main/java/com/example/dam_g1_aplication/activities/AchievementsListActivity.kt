package com.example.dam_g1_aplication.activities

import android.os.Bundle
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.lifecycleScope
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient


class AchievementsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.achiv_list_activity)

        val recyclerView = findViewById<RecyclerView>(R.id.achievementsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

       // val categoryId = intent.getStringExtra("CATEGORY_ID") ?: return
       // fetchAchievements(categoryId, recyclerView)
    }
/*
    private fun fetchAchievements(categoryId: String, recyclerView: RecyclerView) {
        // Preparar conexión de Retrofit
        val retrofit = RetrofitClient.getClient()


        val apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getAchievementsByCategory(categoryId).execute()
                }
                if (response.isSuccessful && response.body() != null) {
                    val achievements = response.body()!!
                    recyclerView.adapter = AchievementsAdapter(achievements)
                } else {
                    Toast.makeText(this@AchievementsListActivity, "No hay logros disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AchievementsListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    */

}
