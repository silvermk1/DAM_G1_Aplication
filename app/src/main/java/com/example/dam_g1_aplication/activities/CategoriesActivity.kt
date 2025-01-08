package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Categories
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesActivity : AppCompatActivity() {

    //atributos para el boton hamburguesa:
    private lateinit var panelMenu: LinearLayout
    private lateinit var menuButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_activity)

        val categoryContainer = findViewById<GridLayout>(R.id.categoryContainer)

        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        val callCategories = apiService.getCategories()
        callCategories.enqueue(object : Callback<List<Categories>> {
            override fun onResponse(
                call: Call<List<Categories>>,
                response: Response<List<Categories>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!

                    for (category in categories) {
                        // Crear el botón de categoría
                        val categoryButton = Button(this@CategoriesActivity).apply {
                            layoutParams = GridLayout.LayoutParams().apply {
                                width = resources.displayMetrics.widthPixels / 2 // Ancho para que ocupe la mitad de la pantalla
                                height = width // Alto igual al ancho para que sea cuadrado
                                marginEnd = 8.dpToPx()
                                bottomMargin = 8.dpToPx()
                            }

                            //estilos
                            text = category.name
                            textSize = 20f
                            setTextColor(Color.parseColor("#FFD700"))
                            setShadowLayer(8f, 4f, 4f, Color.BLACK)
                            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                            typeface = Typeface.DEFAULT_BOLD

                            // Construir el nombre de la imagen del fondo
                            val normalizedCategoryName = "cat_" + category.name.lowercase().replace(" ", "_")
                            val drawableId = resources.getIdentifier(normalizedCategoryName, "drawable", packageName)

                            if (drawableId != 0) {
                                background = ContextCompat.getDrawable(this@CategoriesActivity, drawableId)
                            } else {
                                // Fondo predeterminado si no se encuentra la imagen
                                background = GradientDrawable().apply {
                                    cornerRadius = 16.dpToPx().toFloat()
                                    setColor(resources.getColor(android.R.color.holo_orange_dark))
                                }
                            }

                            setOnClickListener {
                                navigateToAchievements(category)
                            }
                        }

                        // Agregar el botón directamente al `GridLayout`
                        categoryContainer.addView(categoryButton)
                    }
                } else {
                    Toast.makeText(this@CategoriesActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Categories>>, t: Throwable) {
                Toast.makeText(this@CategoriesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


//MENU INTERACTIVO HAMBURGUESA
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        menuButton = findViewById(R.id.menuButton)
        panelMenu = findViewById(R.id.panelMenu)

        // Cargar las animaciones
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        // Al presionar el ImageView (menú)
        menuButton.setOnClickListener {
            if (panelMenu.visibility == View.GONE) {
                // Mostrar el panel con animación
                panelMenu.startAnimation(slideUp)
                panelMenu.visibility = View.VISIBLE
            } else {
                // Ocultar el panel con animación
                panelMenu.startAnimation(slideDown)
                panelMenu.visibility = View.GONE
            }
        }

        // Configurar botones del panel (opcional)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button1.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            if (isLoggedIn) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }        }

    }

    private fun navigateToAchievements(category: Categories) {
        val intent = Intent(this, AchievementsActivity::class.java).apply {
            putExtra("CATEGORY_ID", category.id)
            putExtra("TITLE", category.name)
        }
        startActivity(intent)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}