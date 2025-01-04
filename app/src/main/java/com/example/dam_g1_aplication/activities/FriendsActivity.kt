package com.example.dam_g1_aplication.activities


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsList: ListView
    private lateinit var friendRequestsList: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dragButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_activity)

        //INSTANCIAR ATRIBUTOS
        //val dividerView = findViewById<View>(R.id.dividerView)
        val dividerGuideline = findViewById<Guideline>(R.id.dividerGuideline)

        friendsList = findViewById(R.id.friendsRecyclerView)
        friendRequestsList = findViewById(R.id.pendingRequestsRecyclerView)

        val friendsAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        friendsList.adapter = friendsAdapter

        val friendsRequestsAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        friendRequestsList.adapter = friendsRequestsAdapter

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Botón para mover la línea divisoria
        dragButton = findViewById(R.id.dragButton)

// Cuando el usuario presione y mantenga el botón, moverá la línea
        dragButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Aquí se gestiona el inicio del movimiento
                    Toast.makeText(this, "Presionado para mover", Toast.LENGTH_SHORT).show()
                }

                MotionEvent.ACTION_MOVE -> {
                    // Cuando se mueve el dedo, ajustamos el divisor
                    adjustDivider(event.rawY, dividerGuideline)
                }
            }
            true
        }


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
    }


    //metodo para mover la linea del medio y agrandar o menguar las listas-------
//con alluda del chatgpt
    private fun adjustDivider(rawY: Float, guideline: Guideline) {
        val parentHeight = findViewById<ConstraintLayout>(R.id.mainLayout).height

        // Calcular el nuevo porcentaje para el Guideline
        val newPercent = rawY / parentHeight.toFloat()

        // Validar que el porcentaje esté dentro de los límites permitidos (20% a 80%)
        if (newPercent in 0.2..0.8) {
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = newPercent
            guideline.layoutParams = params
        } else {
            Toast.makeText(
                this,
                "No puedes mover el divisor más allá de los límites.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //METODOS PARA AGREGAR AMIGOS AL LISTVIEW------------------------------------
//METODO PARA OBTENER TODOS LOS IDS DE LOS AMIGOS
    fun getAllFriendships() {
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

//METODO PARA TRADUCIR IDS AMIGOS A NOMBRES AMIGOS
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
        // Este método actualizará el ListView con los nombres de todos los amigos
        runOnUiThread {
            val adapter = friendsList.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.addAll(friendNames)
            adapter.notifyDataSetChanged()
        }
    }

    //METODOS PARA AGREGAR SOLICITUDES AL LISTVIEW------------------------------------
//METODO PARA OBTENER TODOS LOS IDS DE LAS SOLICITUDES
    fun getAllFriendRequests() {
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
        // Este método actualizará el ListView con los nombres de todos los amigos
        runOnUiThread {
            val adapter = friendRequestsList.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.addAll(friendNames)
            adapter.notifyDataSetChanged()
        }
    }
}

