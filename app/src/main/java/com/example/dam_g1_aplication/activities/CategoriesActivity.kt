package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Categories
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

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
                                topMargin = 8.dpToPx()
                                bottomMargin = 8.dpToPx()
                                leftMargin = 8.dpToPx()
                                rightMargin = 8.dpToPx()
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

        // MENU HAMBURGUESA
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.nav_view)

        // Establecer el comportamiento del botón hamburguesa
        menuButton.setOnClickListener {
            // Abrir o cerrar el menú lateral (DrawerLayout)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        escuchadebotonesmenu()

    }

    //METODOS MENU HAMBURGUESA
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun escuchadebotonesmenu() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_perfil -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    if (isLoggedIn) {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_logros -> {
                    Toast.makeText(this, "Logros", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AchievementDetailActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_categorias -> {
                    Toast.makeText(this, "Categorías", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CategoriesActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_iniciar -> {
                    if (isLoggedIn) {
                        Toast.makeText(this, "Cierra la sesion!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_cerrar -> {
                    sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    sharedPreferences.getString("user_id", null)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        remove("username")
                        remove("user_id")
                        remove("mail")
                        apply()

                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Sesion cerrada, Adios!", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_contactos -> {
                    if (isLoggedIn) {
                        Toast.makeText(this, "Contactos", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, FriendsActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Inicie sesion Antes!", Toast.LENGTH_SHORT).show()
                    }
                }

                R.id.nav_soporte -> {
                    Toast.makeText(this, "Soporte", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_compartir -> {
                    Toast.makeText(this, "Gracias por comparitr (:", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileSocialActivity::class.java)
                    startActivity(intent)
                } else -> {
                    Toast.makeText(this, "Opción desconocida", Toast.LENGTH_SHORT).show()
                }
            }

            // Cierra el Drawer después de la selección
            drawerLayout.closeDrawers()
            true
        }
    }

    //otros metodos
    private fun navigateToAchievements(category: Categories) {
        val intent = Intent(this, AchievementsActivity::class.java).apply {
            putExtra("CATEGORY_ID", category.id)
            putExtra("TITLE", category.name)
        }
        startActivity(intent)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}