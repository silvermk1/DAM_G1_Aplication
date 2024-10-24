package com.example.dam_g1_aplication.Mainclasses

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.R

class CategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val categories = mutableListOf("Category 1", "Category 2", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3", "Category 3" ) // Lista de categorías
    private val favorites = mutableListOf<String>() // Lista de favoritos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)


        val backButton = findViewById<Button>(R.id.bBack)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val adapter = CategoryAdapter(categories) { category ->

            if (!favorites.contains(category)) {
                favorites.add(category)
                println("$category añadido a favoritos")
            }
        }
        recyclerView.adapter = adapter
    }
}
