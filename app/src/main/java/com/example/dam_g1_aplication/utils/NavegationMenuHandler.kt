package com.example.dam_g1_aplication.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.activities.CategoriesActivity
import com.example.dam_g1_aplication.activities.FriendsActivity
import com.example.dam_g1_aplication.activities.HomeActivity
import com.example.dam_g1_aplication.activities.LoginActivity
import com.example.dam_g1_aplication.activities.ProfileActivity
import com.example.dam_g1_aplication.activities.ProfileSocialActivity
import com.example.dam_g1_aplication.activities.SupportActivity
import com.example.dam_g1_aplication.activities.UserAchievementsActivity
import com.google.android.material.navigation.NavigationView

class NavigationMenuHandler(
    private val context: Context,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    private var isLoggedIn: Boolean
) {

    // Asociamos cada botón a un activity
    fun setupMenu() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navigateTo(HomeActivity::class.java, "Home")
                R.id.nav_perfil -> navigateTo(
                    if (isLoggedIn) ProfileActivity::class.java else LoginActivity::class.java,
                    if (isLoggedIn) "Perfil" else "Inicia sesión para acceder a tu perfil"
                )
                R.id.nav_logros -> navigateTo(
                    if (isLoggedIn) UserAchievementsActivity::class.java else LoginActivity::class.java,
                    if (isLoggedIn) "Logros" else "Inicia sesión para ver tus logros"
                )
                R.id.nav_categorias -> navigateTo(CategoriesActivity::class.java, "Categorías")
                R.id.nav_iniciar -> handleLoginLogout()
                R.id.nav_cerrar -> handleLogout()
                R.id.nav_contactos -> navigateTo(
                    if (isLoggedIn) FriendsActivity::class.java else LoginActivity::class.java,
                    if (isLoggedIn) "Contactos" else "Inicia sesión para ver tus contactos"
                )
                R.id.nav_soporte -> navigateTo(SupportActivity::class.java, "Soporte")
                R.id.nav_compartir -> navigateTo(
                    if (isLoggedIn) ProfileSocialActivity::class.java else LoginActivity::class.java,
                    if (isLoggedIn) "Compartir" else "Inicia sesión para compartir tu perfil"
                )
            }
            drawerLayout.closeDrawers()
            true
        }
    }


    //Lógica para navegar a otra actividad
    private fun navigateTo(activityClass: Class<*>?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (activityClass != null) {
            context.startActivity(Intent(context, activityClass))
        }
    }

    //Verifica si la sesión está iniciada
    private fun handleLoginLogout() {
        if (isLoggedIn) {
            Toast.makeText(context, "Cierra la sesión!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    //Cierra la sesión
    private fun handleLogout() {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (isLoggedIn) {
            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", false)
                remove("username")
                remove("user_id")
                remove("mail")
                apply()
            }
            updateLoginStatus(false)
            Toast.makeText(context, "Sesión cerrada, ¡Adiós!", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, LoginActivity::class.java))
        } else {
            Toast.makeText(context, "¡Ya estaba cerrada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateLoginStatus(isLoggedIn: Boolean) {
        this.isLoggedIn = isLoggedIn
    }
}
