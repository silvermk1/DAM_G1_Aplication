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

        val friendsAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        friendsList.adapter = friendsAdapter

        val friendsRequestsAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
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


//CLICKLISTENER
        //mandar al perfil del amigo clickeado
        friendsList.setOnItemClickListener { parent, view, position, id ->
            // Obtener el nombre del amigo que se ha clicado
            val nombreusuario = friendsList.getItemAtPosition(position) as String
            //mandar al intent del amigo clickeado
            val intent = Intent(this, ProfileActivityFriend::class.java)
            intent.putExtra("nombreusuario", nombreusuario)
            startActivity(intent)

        }
    }


//metodo para mover la linea del medio y agrandar o menguar las listas
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
            Toast.makeText(this, "No puedes mover el divisor más allá de los límites.", Toast.LENGTH_SHORT).show()
        }
    }


//metodo para actualizar list view de nombres de amigos
    fun updateListView(userName: String) {
        // Este es el método donde agregarías el nombre al ListView
        // Por ejemplo, puedes usar un ArrayAdapter o algún otro adaptador
        // Asegúrate de actualizar el ListView en el hilo principal

        runOnUiThread {
            val adapter = friendsList.adapter as ArrayAdapter<String>
            adapter.add(userName)
        }
    }

//METOD PARA RETORNAR UN USUARIO POR SU ID
    fun getUserById(userId: Long, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Realizar la llamada para obtener el usuario por su ID
        val callUser = apiService.getUserById(userId)
        callUser.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        callback(user.username) // Devuelves el nombre del usuario a través del callback
                    }
                } else {
                    println("Error en la respuesta: " + response.code())
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                println("Error al obtener el usuario: " + t.message)
            }
        })
    }

//METODO PARA OBTENER TODOS LOS AMIGOS
    fun getAllFriendships() {
        //conectar retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val datos = mutableListOf<String>()

        //hazer llamada = retornar ids de los amgios
        val callFriendships = apiService.getFriendships()
        callFriendships.enqueue(object : Callback<List<Friendships>> {
            override fun onResponse(call: Call<List<Friendships>>, response: Response<List<Friendships>>) {
                if (response.isSuccessful) {
                    val friendships = response.body()!!

                    val idpropio = sharedPreferences.getString("user_id", null)
                    // Obtener IDs de los amigos
                    for (friendship in friendships) {
                        //filtrar los amigos que corresponden al usuario iniciado
                        if (friendship.friendA.toString() == idpropio || friendship.friendB.toString() == idpropio) {
                            // filtrar los id de los amigos y apartar el del usuario iniciado
                            if (friendship.friendA.toString() != idpropio) {
                                datos.add(friendship.friendA.toString())
                            }
                            // filtrar los id de los amigos y apartar el del usuario iniciado
                            if (friendship.friendB.toString() != idpropio) {
                                datos.add(friendship.friendB.toString())

                            }
                        }
                    }
                //TRADUCIR ID A NOMBRE
                    // Usar una lista para recolectar los nombres
                    val friendNames = mutableListOf<String>()
                    val pendingRequests = datos.size
                    //recorrer la lista de ids creada anteriormente
                    for (userId in datos) {
                        //con el metodo getUserById convertir id a username y agregarlo a la lista
                        getUserById(userId.toLong()) { userName ->
                            synchronized(friendNames) {
                                friendNames.add(userName)
                                if (friendNames.size == pendingRequests) {
                                    // Actualizar el ListView con todos los nombres al final
                                    runOnUiThread {
                                        val adapter = friendsList.adapter as ArrayAdapter<String>
                                        adapter.clear()
                                        adapter.addAll(friendNames)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@FriendsActivity, "Error al obtener amistades", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Friendships>>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//METODO PARA OBTENER TODAS LAS SOLICITUDES //NOSE PORQUE NO FUNCIONA!!!!
    fun getAllFriendRequests() {
        //conectar retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val datos = mutableListOf<String>()

        //hazer llamada = retornar ids de los amgios
        val callfriendre = apiService.getFriendRequests()
        callfriendre.enqueue(object : Callback<List<FriendRequests>> {
            override fun onResponse(call: Call<List<FriendRequests>>, response: Response<List<FriendRequests>>) {
                if (response.isSuccessful) {
                    val friendships = response.body()!!

                    //val idpropio = sharedPreferences.getString("user_id", null)
                    // Obtener IDs de los amigos
                    println("MOSTRAR:(:--------------")
                    for (friendship in friendships) {
                        println("SOLICITUD:" + friendship.userSender)
                    }

                } else {
                    // Mostrar más detalles sobre el error
                    println("Error en la respuesta: ${response.code()}")
                    response.errorBody()?.let {
                        // Muestra el cuerpo del error si existe
                        println("Cuerpo del error: ${it.string()}")
                    }
                    Toast.makeText(this@FriendsActivity, "Error al obtener amistades", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FriendRequests>>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}