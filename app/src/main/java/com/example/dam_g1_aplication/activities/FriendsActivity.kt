package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import com.example.dam_g1_aplication.utils.NavigationMenuHandler
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoggedIn: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var friendsList: ListView
    private lateinit var friendRequestsList: ListView
    private lateinit var navigationMenuHandler: NavigationMenuHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_activity)

        //INSTANCIAR ATRIBUTOS

        friendsList = findViewById(R.id.amistadeslista)
        friendRequestsList = findViewById(R.id.solicitudeslista)

        val friendsAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        friendsList.adapter = friendsAdapter

        val friendsRequestsAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        friendRequestsList.adapter = friendsRequestsAdapter

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Obtener datos para los ListViews
        getAllFriendships()
        getAllFriendRequests()
        println("ahora:")
        //getUserById2(2)

        //CLICKLISTENER
        //mandar al perfil del amigo clickeado
        friendsList.setOnItemClickListener { parent, view, position, id ->
            // Obtener el nombre del amigo que se ha clicado
            val nombreusuario = friendsList.getItemAtPosition(position) as String
            //mandar al intent del amigo clickeado
            val intent = Intent(this, ProfileActivityFriend::class.java)
            intent.putExtra("tipodesolicitud", "agregado")
            intent.putExtra("nombreusuario", nombreusuario)
            startActivity(intent)

        }
        //mandar al perfil de la solicitud clickeada
        friendRequestsList.setOnItemClickListener { parent, view, position, id ->
            // Obtener el nombre del amigo que se ha clicado
            val nombreusuario = friendRequestsList.getItemAtPosition(position) as String
            //mandar al intent del amigo clickeado
            val intent = Intent(this, ProfileActivityFriend::class.java)
            intent.putExtra("tipodesolicitud", "solicitado")
            intent.putExtra("nombreusuario", nombreusuario)
            startActivity(intent)

        }
        // MENU HAMBURGUESA
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.nav_view)

        //Acciones de los botones del menú
        navigationMenuHandler = NavigationMenuHandler(this, drawerLayout, navigationView, isLoggedIn)
        navigationMenuHandler.setupMenu()

        // Establecer el comportamiento del botón hamburguesa
        menuButton.setOnClickListener {
            // Abrir o cerrar el menú lateral (DrawerLayout)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    // AGREGAR AMIGOS AL LISTVIEW
    private fun getAllFriendships() {
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val friendIds = mutableListOf<String>()

        val callFriendships = apiService.getFriendships()
        callFriendships.enqueue(object : Callback<List<Friendships>> {
            override fun onResponse(
                call: Call<List<Friendships>>,
                response: Response<List<Friendships>>
            ) {
                val friendships = response.body()
                if (friendships == null) {
                    Toast.makeText(this@FriendsActivity, "No se pudieron obtener las amistades.", Toast.LENGTH_SHORT).show()
                    return
                }
                val idpropio = sharedPreferences.getString("user_id", null)

                // Filtrar los IDs de los amigos que corresponden al usuario actual
                for (friendship in friendships) {
                    if (friendship.friendA.toString() == idpropio || friendship.friendB.toString() == idpropio) {
                        if (friendship.friendA.toString() != idpropio) {
                            friendIds.add(friendship.friendA.toString())
                        }
                        if (friendship.friendB.toString() != idpropio) {
                            friendIds.add(friendship.friendB.toString())
                        }
                    }
                }

                val totalFriends = friendIds.size
                val friendNames = mutableListOf<String>()
                var counter = 0

                // Obtener los nombres de todos los amigos
                for (userId in friendIds) {
                    getUserById2(userId.toLong()) { userName ->
                        friendNames.add(userName)
                        counter++

                        if (counter == totalFriends) {
                            updateListViewWithFriends(friendNames)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Friendships>>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // TRADUCIR IDS AMIGOS A NOMBRES AMIGOS
    fun getUserById2(userId: Long, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        val callUser = apiService.getUserById(userId)
        callUser.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                val user = response.body()
                if (user != null) {
                    // Llamamos al callback con el nombre del usuario
                    callback(user.username.toString())
                }
            }
            override fun onFailure(call: Call<Users>, t: Throwable) {
                println("Error al obtener el usuario: ${t.message}")
            }
        })
    }

    //METODO PARA INSERTAR LOS NOMBRES DE LOS AMIGOS EN EL LISTVIEW
    fun updateListViewWithFriends(friendNames: List<String>) {
        // Este metodo actualizará el ListView con los nombres de todos los amigos
        runOnUiThread {
            val adapter = friendsList.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.addAll(friendNames)
            adapter.notifyDataSetChanged()
        }
    }

    // AGREGAR SOLICITUDES AL LISTVIEW
    private fun getAllFriendRequests() {
        //conectar retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val datos = mutableListOf<String>()

        //hazer llamada = retornar ids de los amgios
        val callfriendre = apiService.getFriendRequests()
        callfriendre.enqueue(object : Callback<List<FriendRequests>> {
            override fun onResponse(
                call: Call<List<FriendRequests>>,
                response: Response<List<FriendRequests>>
            ) {
                val idpropio = sharedPreferences.getString("user_id", null)

                val friendrequests = response.body()
                println(friendrequests)
                //val idpropio = sharedPreferences.getString("user_id", null)
                // Obtener IDs de los amigos
                println("MOSTRAR:-------------->(:----")
                if (friendrequests != null) {
                    for (friendrequest in friendrequests) {
                        if (friendrequest.userReciever.toString() == idpropio) {
                            datos.add(friendrequest.userSender.toString())
                        }
                    }
                }
                //agregar al listview:
                val totalFriends = datos.size
                val friendNames = mutableListOf<String>()
                var counter = 0

                for (userId in datos) {
                    getUserById2(userId.toLong()) { userName ->
                        friendNames.add(userName)
                        counter++

                        // Si hemos obtenido los nombres de todos los amigos, actualizar el ListView
                        if (counter == totalFriends) {
                            updateListViewWithFriendsRequests(friendNames)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<FriendRequests>>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    //METODO PARA INSERTAR LOS NOMBRES DE LOS AMIGOS EN EL LISTVIEW
    fun updateListViewWithFriendsRequests(friendNames: List<String>) {
        // Este metodo actualizará el ListView con los nombres de todos los amigos
        runOnUiThread {
            val adapter = friendRequestsList.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.addAll(friendNames)
            adapter.notifyDataSetChanged()
        }
    }
}




