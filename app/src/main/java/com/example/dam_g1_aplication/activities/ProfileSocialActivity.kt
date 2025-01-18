package com.example.dam_g1_aplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.dam_g1_aplication.R
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.dataClasses.Users
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSocialActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService
    private lateinit var youtubeEditText: EditText
    private lateinit var youtubeSaveButton: Button
    private lateinit var twitterxEditText: EditText
    private lateinit var twitterxSaveButton: Button
    private lateinit var facebookEditText: EditText
    private lateinit var facebookSaveButton: Button
    private lateinit var twitchEditText: EditText
    private lateinit var twitchSaveButton: Button
    private lateinit var redditEditText: EditText
    private lateinit var redditSaveButton: Button
    private lateinit var steamEditText: EditText
    private lateinit var steamSaveButton: Button
    private lateinit var epicgamesEditText: EditText
    private lateinit var epicgamesSaveButton: Button
    private lateinit var nswitchEditText: EditText
    private lateinit var nswitchSaveButton: Button
    private lateinit var psnEditText: EditText
    private lateinit var psnSaveButton: Button
    private lateinit var xboxEditText: EditText
    private lateinit var xboxSaveButton: Button
    private lateinit var username: String

    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_social_activity)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        username = sharedPreferences.getString("username", "") ?: ""

        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        apiService = retrofit.create(ApiService::class.java)

        // Obtener elementos del layout
        youtubeEditText = findViewById(R.id.youtubeEditText)
        youtubeSaveButton = findViewById(R.id.youtubeSaveButton)
        twitterxEditText = findViewById(R.id.twitterxEditText)
        twitterxSaveButton = findViewById(R.id.twitterxSaveButton)
        facebookEditText = findViewById(R.id.facebookEditText)
        facebookSaveButton = findViewById(R.id.facebookSaveButton)
        twitchEditText = findViewById(R.id.twitchEditText)
        twitchSaveButton = findViewById(R.id.twitchSaveButton)
        redditEditText = findViewById(R.id.redditEditText)
        redditSaveButton = findViewById(R.id.redditSaveButton)
        steamEditText = findViewById(R.id.steamEditText)
        steamSaveButton = findViewById(R.id.steamSaveButton)
        epicgamesEditText = findViewById(R.id.epicgamesEditText)
        epicgamesSaveButton = findViewById(R.id.epicgamesSaveButton)
        nswitchEditText = findViewById(R.id.nswitchEditText)
        nswitchSaveButton = findViewById(R.id.nswitchSaveButton)
        psnEditText = findViewById(R.id.psnEditText)
        psnSaveButton = findViewById(R.id.psnSaveButton)
        xboxEditText = findViewById(R.id.xboxEditText)
        xboxSaveButton = findViewById(R.id.xboxSaveButton)

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
                    val intent = Intent(this, UserAchievementsActivity::class.java)
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
                    if (isLoggedIn) {
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
                    } else {
                        Toast.makeText(this, "Ya estava cerrada!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_contactos -> {
                    if (isLoggedIn) {
                        Toast.makeText(this, "Contactos", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, FriendsActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Inicie sesion Antes!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.nav_soporte -> {
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_compartir -> {
                    if (isLoggedIn) {
                        val intent = Intent(this, ProfileSocialActivity::class.java)
                        startActivity(intent)
                    } else  {
                        Toast.makeText(
                            this,
                            "Inicia Sesión para acceder a este menú",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            // Cierra el Drawer después de la selección
            drawerLayout.closeDrawers()
            true
        }


        // Hacer la llamada al servicio para obtener las redes del usuario
        val callUsers = apiService.getUsers()
        callUsers.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                val users = response.body()
                val user = users?.find {it.username == username}
                if (user != null) {
                    //Si el usuario tiene una red, se mostrará un botón suyo con la red puesta. Al pulsar sobre la red te dirigirá a esa
                    if (user.youtube != null) {
                        youtubeEditText.hint = "https://youtube.com/" + user.youtube
                    }
                    if (user.twitterx != null) {
                        twitterxEditText.hint = "https://x.com/" + user.twitterx
                    }
                    if (user.facebook != null) {
                        facebookEditText.hint = "https://facebook.com/" + user.facebook
                    }
                    if (user.twitch != null) {
                        twitchEditText.hint = "https://twitch.tv/" + user.twitch
                    }
                    if (user.reddit != null) {
                        redditEditText.hint = "https://reddit.com/user/" + user.reddit
                    }
                    if (user.steam != null) {
                        steamEditText.hint = "https://steamcommunity.com/id/" + user.steam
                    }
                    if (user.epicgames != null) {
                        epicgamesEditText.hint = "https://epicgames.com/id/" + user.epicgames
                    }
                    if (user.nswitch != null) {
                        nswitchEditText.hint = "https://switch.nintendo.com/" + user.nswitch
                    }
                    if (user.psn != null) {
                        psnEditText.hint = "https://psnprofiles.com/" + user.psn
                    }
                    if (user.xbox != null) {
                        xboxEditText.hint = "https://xboxgamertag.com/search/" + user.xbox
                    }
                }
            }
            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
            }
        })
    }
}