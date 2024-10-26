package com.example.dam_g1_aplication.Mainclasses

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.R

class AchivListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_achiv_list)
        val achivments = listOf("Achivment 1","Achivment 2","AQchivment 3")
        val rvAchiv = findViewById<RecyclerView>(R.id.rvAchivList)
        rvAchiv.layoutManager = LinearLayoutManager(this);

        val adapter = AchievementAdapter(achivments)
        rvAchiv.adapter = adapter

        findViewById<Button>(R.id.bBack).setOnClickListener {
            finish()
        }
    }
}