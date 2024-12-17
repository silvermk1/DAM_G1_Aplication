package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var usernameTextView: TextView
    private lateinit var mailTextView: TextView
    private lateinit var biographyTextMultiLine: EditText
    private lateinit var saveButton: Button
    private lateinit var username: String
    private lateinit var mail: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bottoncerrarsesion : Button
    private lateinit var botonamigos : Button
    private lateinit var friendSearcher: EditText
    private lateinit var friendsSearchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.getString("user_id", null)
        println("idpreferences debug" +         sharedPreferences.getString("user_id", null)
        )
        username = sharedPreferences.getString("username", null).toString()
        mail = sharedPreferences.getString("mail", null).toString()
        usernameTextView = findViewById(R.id.usernameTextView)
        mailTextView = findViewById(R.id.mailTextView)
        biographyTextMultiLine = findViewById(R.id.biographyTextMultiLine)
        bottoncerrarsesion = findViewById(R.id.cerrarsesion)
        saveButton = findViewById(R.id.saveButton)
        botonamigos = findViewById(R.id.friendListButton)
        friendSearcher = findViewById(R.id.FriendSearcher)
        friendsSearchButton = findViewById(R.id.friendsSearch)
        usernameTextView.text = username
        mailTextView.text = mail



        // FOOTER
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val profileButton: Button = findViewById(R.id.profileButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val homeButton: Button = findViewById(R.id.homeButton)


        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
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
        //CERRAR SESION AL PRESIONAR EL BOTON
        bottoncerrarsesion.setOnClickListener{
            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", false)
                remove("username")
                remove("user_id")
                remove("mail")
                apply()

            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        //Mandar al perfil buscado
        friendsSearchButton.setOnClickListener {
            // Obtener el texto ingresado en el EditText
            val nombreAmigo = friendSearcher.text.toString().trim()

            // Verificar que el campo no este vacio
            if (nombreAmigo.isNotEmpty()) {
                getUseridByUsername3(nombreAmigo)
            }

        }
        //MANDAR AL ACTIVITY FRIENDS
        botonamigos.setOnClickListener{
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }

        // FOOTER
    }

//METODO PARA COMPROVAR QUE EL USUARIO EXISTE
    fun getUseridByUsername3(username: String) {
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Hacer la llamada al servicio para obtener todos los usuarios
        val callUsers = apiService.getUsers()
        callUsers.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                val users = response.body()
                println("usuarios:")
                println(users)
                if (users != null) {
                    println("nulo?")
                    println("Usuarios obtenidos: ${users.size}")
                    for (user in users) {
                        println("Username: ${user.username}, ID: ${user.id}")
                        if (user.username == username){//el usuario existe
                            comprovarAmistad(user.id.toLong(), username)
                        }
                    }
                } else {
                    println("No se encontraron usuarios.")
                }

            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                println("Error al realizar la solicitud: ${t.message}")
            }
        })
    }
//METODO PARA COMPROVAR LA AMISTAD Y MANDAR AL ACTIVITY DEL PERFIL
    fun comprovarAmistad(idamigo : Long, username: String){
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val friendIds = mutableListOf<String>()
        var numero = 0
        val callFriendships = apiService.getFriendships()
        callFriendships.enqueue(object : Callback<List<Friendships>> {
            override fun onResponse(call: Call<List<Friendships>>, response: Response<List<Friendships>>) {
                val friendships = response.body()!!
                val idpropio = sharedPreferences.getString("user_id", null)

                // Filtrar los IDs de los amigos que corresponden al usuario actual
                for (friendship in friendships) {
                    //comprovar que no es tu amigo
                        if (friendship.friendA.toString() == idpropio) {
                            if (friendship.friendB.toString() == idamigo.toString()){
                                numero = 1
                                Toast.makeText(this@ProfileActivity, "ya es tu amigo", Toast.LENGTH_SHORT).show()
                            }
                        }
                        if (friendship.friendB.toString() == idpropio) {
                            if (friendship.friendA.toString() == idamigo.toString()){
                                numero = 1
                                Toast.makeText(this@ProfileActivity, "ya es tu amigo", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                //no esa tu amigo, mandar al activity...
                if (numero == 0){
                    val intent = Intent(this@ProfileActivity, ProfileActivityFriend::class.java)
                    intent.putExtra("tipodesolicitud", "noagregado")
                    intent.putExtra("nombreusuario", username)
                    startActivity(intent)
                }
                }

            override fun onFailure(call: Call<List<Friendships>>, t: Throwable) {
                TODO("Not yet implemented")
            }





        })

}
}