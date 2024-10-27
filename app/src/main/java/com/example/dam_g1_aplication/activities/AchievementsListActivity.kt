package com.example.dam_g1_aplication.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.R

class AchievementsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.achiv_list_activity)
        val achivments = listOf("Achivment 1","Achivment 2","AQchivment 3")
        val rvAchiv = findViewById<RecyclerView>(R.id.rvAchivList)
        rvAchiv.layoutManager = LinearLayoutManager(this)

        val adapter = AchievementAdapter(achivments)
        rvAchiv.adapter = adapter

        findViewById<Button>(R.id.bBack).setOnClickListener {
            finish()
        }
    }
}