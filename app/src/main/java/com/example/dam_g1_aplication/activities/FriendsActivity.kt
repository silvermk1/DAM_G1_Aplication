package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsContainer: LinearLayout
    private lateinit var requestsContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_activity)

        friendsContainer = findViewById(R.id.friendsContainer)
        requestsContainer = findViewById(R.id.requestsContainer)
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        getAllFriendships()
        getAllFriendRequests()
    }

    private fun addButtonToContainer(container: LinearLayout, name: String, isFriend: Boolean) {
        val button = Button(this).apply {
            text = name

            // Fondo con esquinas redondeadas y color dinámico
            val backgroundDrawable = GradientDrawable().apply {
                cornerRadius = 8 * resources.displayMetrics.density // Conversión de dp a píxeles
                setColor(
                    if (isFriend) resources.getColor(android.R.color.holo_green_dark)
                    else resources.getColor(android.R.color.holo_red_dark)
                )
            }
            background = backgroundDrawable

            setTextColor(resources.getColor(android.R.color.white))

            setOnClickListener {
                val intent = Intent(this@FriendsActivity, ProfileActivityFriend::class.java).apply {
                    putExtra("tipodesolicitud", if (isFriend) "agregado" else "solicitado")
                    putExtra("nombreusuario", name)
                }
                startActivity(intent)
            }
        }

        // Configurar los márgenes
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 8, 8, 8) // Márgenes en dp
        }
        button.layoutParams = layoutParams

        container.addView(button)
    }


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
                val idpropio = sharedPreferences.getString("user_id", null)
                val idpropioLong = idpropio?.toLongOrNull()

                if (idpropioLong != null && friendships != null) {
                    friendships.forEach { friendship ->
                        if (friendship.friendA == idpropioLong) {
                            friendIds.add(friendship.friendB.toString())
                        } else if (friendship.friendB == idpropioLong) {
                            friendIds.add(friendship.friendA.toString())
                        }
                    }

                    friendIds.forEach { userId ->
                        getUserById(userId.toLong()) { userName ->
                            addButtonToContainer(friendsContainer, userName, true)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Friendships>>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAllFriendRequests() {
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val requestIds = mutableListOf<String>()

        val callfriendre = apiService.getFriendRequests()
        callfriendre.enqueue(object : Callback<List<FriendRequests>> {
            override fun onResponse(
                call: Call<List<FriendRequests>>,
                response: Response<List<FriendRequests>>
            ) {
                val idpropio = sharedPreferences.getString("user_id", null)
                val idpropioLong = idpropio?.toLongOrNull()

                if (idpropioLong != null) {
                    response.body()?.filter { it.userReciever == idpropioLong }
                        ?.forEach { request ->
                            requestIds.add(request.userSender.toString())
                        }

                    requestIds.forEach { userId ->
                        getUserById(userId.toLong()) { userName ->
                            addButtonToContainer(requestsContainer, userName, false)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<FriendRequests>>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserById(userId: Long, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        apiService.getUserById(userId).enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                response.body()?.let { callback(it.username) }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                println("Error al obtener el usuario: ${t.message}")
            }
        })
    }
}
