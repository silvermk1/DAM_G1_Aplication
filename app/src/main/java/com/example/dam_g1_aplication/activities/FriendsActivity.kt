package com.example.dam_g1_aplication.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsList: RecyclerView
    private lateinit var friendRequestsList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_activity)

//INSTANCIAR ATRIBUTOS
        //instanciar divisor y Guideline
        val dividerView = findViewById<View>(R.id.dividerView)
        val dividerGuideline = findViewById<Guideline>(R.id.dividerGuideline)

        //instanciar recylceview
        friendsList = findViewById(R.id.friendsRecyclerView)
        friendRequestsList = findViewById(R.id.pendingRequestsRecyclerView)


//Ajustar linea del medio movible
        // Configurar el divisor para arrastrarlo
        dividerView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    adjustDivider(event.rawY, dividerGuideline)
                }
            }
            true
        }

//INSTANCIAR ADAPTADORES Y AGREGAR DATOS AL RECYCLEVIEW

        getAllFriendships()
        getAllFriendRequests()
    }
//METODO PARA OBTENER TODOS LOS AMIGOS
    fun getAllFriendships(){

    // Preparar la conexión Retrofit
    val retrofit = RetrofitClient.getClient()
    val apiService = retrofit.create(ApiService::class.java)

    // Realizar la llamada para obtener las amistades
    val callFriendships = apiService.getFriendships()
    callFriendships.enqueue(object : Callback<List<Friendships>> {
        override fun onResponse(
            call: Call<List<Friendships>>,
            response: Response<List<Friendships>>
        ) {
            if (response.isSuccessful) {
                val friendships = response.body()!!
                // Mostrar las amistades obtenidas
                println("AMISTADES OBTENIDAS:")
                for (friendship in friendships) {
                    println("ID de amistad: " + friendship.friendship)
                    println("Amigo A: " + friendship.friendA)
                    println("Amigo B: " + friendship.friendB)
                }
            } else {
                // Manejar error en la respuesta
                println("Error en la respuesta: " + response.code())
                response.errorBody()?.let { errorBody ->
                    println("Cuerpo del error: " + errorBody.string())
                }
                Toast.makeText(this@FriendsActivity, "Error al obtener las amistades", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<Friendships>>, t: Throwable) {
            // Manejar fallo de la llamada
            Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            println("Error al obtener amistades: " + t.message)
        }
    })
}

//METODO PARA OBTENER TODAS LAS SOLICITUDES
    fun getAllFriendRequests() {
        // Preparar la conexión Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Realizar la llamada para obtener las solicitudes de amistad
        val callFriendRequests = apiService.getFriendRequests()
        callFriendRequests.enqueue(object : Callback<List<FriendRequests>> {
            override fun onResponse(
                call: Call<List<FriendRequests>>,
                response: Response<List<FriendRequests>>
            ) {
                if (response.isSuccessful) {
                    val friendRequests = response.body()!!
                    // Mostrar las solicitudes de amistad obtenidas
                    println("SOLICITUDES DE AMISTAD OBTENIDAS:")
                    for (request in friendRequests) {
                        println("ID de solicitud: " + request.friendrequests)
                        println("Remitente: " + request.userSender)
                        println("Receptor: " + request.userReciever)
                    }
                } else {
                    // Manejar error en la respuesta
                    println("Error en la respuesta: " + response.code())
                    response.errorBody()?.let { errorBody ->
                        println("Cuerpo del error: " + errorBody.string())
                    }
                    Toast.makeText(this@FriendsActivity, "Error al obtener solicitudes de amistad", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FriendRequests>>, t: Throwable) {
                // Manejar fallo de la llamada
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                println("Error al obtener solicitudes de amistad: " + t.message)
            }
        })
    }

//metodo para mover la linea del medio y agrandar o menguar las listas
//con alluda del chatgpt
    private fun adjustDivider(rawY: Float, guideline: Guideline) {
        // Obtener la altura del ConstraintLayout principal
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



}