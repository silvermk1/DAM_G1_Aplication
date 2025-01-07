package com.example.dam_g1_aplication.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.isWideGamut
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import com.example.dam_g1_aplication.dataClasses.Users
import com.google.android.material.imageview.ShapeableImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivityFriend : AppCompatActivity() {

    private lateinit var textousuario: TextView
    private lateinit var compartir : Button
    private lateinit var listaobjetivos : ListView
    private lateinit var botonsolicitud : Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imagenusuarioamigo: ShapeableImageView

//Atributos-datos--
    //nombre usuario
    private lateinit var usuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity_friend)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

//Declarar atributos con sus respectivos objetos
        textousuario = findViewById(R.id.usuario)
        compartir = findViewById(R.id.compartirlogro2)
        botonsolicitud = findViewById(R.id.solicitud)

        //agregar nombreusuario al text view
        var usuario = intent.getStringExtra("nombreusuario")
        textousuario.setText(usuario)

        //instanciar list view
        listaobjetivos = findViewById(R.id.listaobjetivos)
        val objetivosadapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        listaobjetivos.adapter = objetivosadapter

        //declarar imagen
        imagenusuarioamigo = findViewById(R.id.imagenusuario)
        getUseridByUsername8(usuario.toString())

//AGREGAR TIPO DE BOTON SOLICITUD DEPENDE DE DONDE PROVENGA EL ACTIVITY:

        val tiposolicitud = intent.getStringExtra("tipodesolicitud") ?: "default"

        if (tiposolicitud == "solicitado") {
            botonsolicitud.setText("ACEPTAR SOLICITUD")
        //agregar amigo y borrar la solicitud al clikear el boton!
            botonsolicitud.setOnClickListener{
                getUseridByUsername4(usuario.toString())
                getUseridByUsername5(usuario.toString())
            }
        }
        if (tiposolicitud == "agregado") {
            botonsolicitud.setText("DEJAR DE SEGIR")
        //borrar amigo al presionar el boton
            botonsolicitud.setOnClickListener{
                //borrar amigo
                getUseridByUsername6(usuario.toString())
            }
        }
        if (tiposolicitud == "noagregado"){
            botonsolicitud.setText("Agregar")
        //agregar solicitud de amistad al presionar el boton
            botonsolicitud.setOnClickListener{
                //agregar solicitud de amistad
                getUseridByUsername7(usuario.toString())
            }
        }
//CREAR CLICKLISTENER PARA QUE AL PRESIONAR COMPARTIR OBJETIVOS SALGA UN CUADRO EMERJENTE,
//EN ESTE CUADRO EMERJENTE SALDRAN TODOS TUS OBJETIVOS, TENDRAS QUE SELECCIONAR UNO PARA COMPARTIR
//ESTA FUNCION AUN NO ESTA DISPONIBLE
        compartir.setOnClickListener(){
            var usuarioiniciado = intent.getStringExtra("username") //sacar usuario iniciado
            getUseridByUsername2(usuarioiniciado.toString()) //metodo para listar los objetivos del perfil en el boton objetivos
        }

        getUseridByUsername(usuario.toString()) //metodo para listar los objetivos del perfil


    }

//METODOS--------------------
//-----------------------------------
//-----------------------------------------
//METODOS PARA RETORNAR LOS OBJETIVOS DEL USUARIO DEL PERFIL SELECCIONADO ANTERIORMENTE------------
//METODO PARA RETORNAR USUARIOID POR SU USERNAME
    fun getUseridByUsername(username: String) {
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
                            if (user.username == username){
                                retornarobjetivosusuarioid(user.id.toLong())
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

//METODO PARA BUSCAR LOS OBJETIVOS DE UN DETERMINADO USUARIOID
    fun retornarobjetivosusuarioid(userId : Long){
        //preparar conexion retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        //lista para guardar los id de lo objetivos del usuario
        val ids: MutableList<Long> = mutableListOf()

    //retornar los logros segun el usuarioid
        val calluser = apiService.findUserAchievementsByUserId(userId)
        calluser.enqueue(object : Callback<List<UserAchievements>> {
            override fun onResponse(
                call: Call<List<UserAchievements>>,
                response: Response<List<UserAchievements>>
            ) {
                    val userachievements = response.body()
                    if (userachievements != null) {
                        println("AQUI SE MEUSTRAN!!!!(_:")
                        for (achievement in userachievements) {

                            //guardar el id en la lista ids
                            ids.add(achievement.achievementid)

                        }
                        //enviar la lista al metodo que transforma los ids a nombre objetivos
                        transformobjetivosidanombre(ids)

                    }

            }

            override fun onFailure(call: Call<List<UserAchievements>>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                println("error2")
            }
        })

    }

//METODO PARA TRANSFORMAR LISTA OBJETIVOSID A LISTA NOMBREOBJETIVOS
    fun transformobjetivosidanombre(objetivosid : List<Long>){
    //conectar retrofit
    val retrofit = RetrofitClient.getClient()
    val apiService = retrofit.create(ApiService::class.java)

    val nombres: MutableList<String> = mutableListOf()

    //hazer llamada = retornar ids de los amgios
    val callFriendships = apiService.getAchievements()
    callFriendships.enqueue(object : Callback<List<Achievements>> {
        override fun onResponse(call: Call<List<Achievements>>, response: Response<List<Achievements>>) {

                val objetivos = response.body()
            if (objetivos != null) {
                println("todos los objetivos:")
                println(objetivos)
                // Obtener IDs de los amigos
                for (objetivo in objetivos) {
                    for (objetivoId in objetivosid) {
                        if (objetivo.id.toLong() == objetivoId) {
                            nombres.add(objetivo.title)
                        }

                    }
                }
                //AGREGAR AL LIST VIEW TODOS LOS VALORES
                println("mostrar los nombrs:----")
                println(nombres)
                actualizarListView(nombres)
            }
        }
        override fun onFailure(call: Call<List<Achievements>>, t: Throwable) {
            Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

//METODO PARA ACTUALIZAR LIST VIEW OBJETIVOS
    fun actualizarListView(nombres: List<String>) {
        // Crear un ArrayAdapter con la lista de nombres
        val objetivosAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)

        // Actualizar el ListView
        listaobjetivos.adapter = objetivosAdapter
    }

//METODOS PARA MOSTRAR OBJETIVOS COMPARTIDOS--------------------------------------------------------
//METODO PARA ABRIR UN CUADRO EMERJENTE CON TODOS TUS OBJETIVOS
    private fun compartirobjetivo(objetivos : List<String>){

        val dialogView = layoutInflater.inflate(R.layout.cuadro_emerjente_logroscompartidos, null)
        val listView = dialogView.findViewById<ListView>(R.id.dialog_listview)

        // Infla el diseño del cuadro emergente
        val adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, objetivos)
        listView.adapter = adaptador

        // Configura el cuadro emergente
        val dialog = AlertDialog.Builder(this)
            .setTitle("Selecciona un logro:")
            .setView(dialogView)
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        // Maneja la selección de un elemento
        listView.setOnItemClickListener { parent, view, position, id ->
            val seleccionado = objetivos[position]
            Toast.makeText(this, "Seleccionaste: $seleccionado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // Muestra el cuadro
        dialog.show()
    }

    fun getUseridByUsername2(username: String) {
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
                        if (user.username == username){
                            retornarobjetivosusuarioid2(user.id.toLong())
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

    fun transformobjetivosidanombre2(objetivosid : List<Long>){
        //conectar retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        val nombres: MutableList<String> = mutableListOf()

        //hazer llamada = retornar ids de los amgios
        val callFriendships = apiService.getAchievements()
        callFriendships.enqueue(object : Callback<List<Achievements>> {
            override fun onResponse(call: Call<List<Achievements>>, response: Response<List<Achievements>>) {

                val objetivos = response.body()
                if (objetivos != null) {
                    println("todos los objetivos:")
                    println(objetivos)
                    // Obtener IDs de los amigos
                    for (objetivo in objetivos) {
                        for (objetivoId in objetivosid) {
                            if (objetivo.id.toLong() == objetivoId) {
                                nombres.add(objetivo.title)
                            }

                        }
                    }
                    //AGREGAR AL LIST VIEW TODOS LOS VALORES
                    println("mostrar los nombrs:----")
                    println(nombres)
                    compartirobjetivo(nombres)
                }
            }
            override fun onFailure(call: Call<List<Achievements>>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retornarobjetivosusuarioid2(userId : Long){
        //preparar conexion retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        //lista para guardar los id de lo objetivos del usuario
        val ids: MutableList<Long> = mutableListOf()

        //retornar los logros segun el usuarioid
        val calluser = apiService.findUserAchievementsByUserId(userId)
        calluser.enqueue(object : Callback<List<UserAchievements>> {
            override fun onResponse(
                call: Call<List<UserAchievements>>,
                response: Response<List<UserAchievements>>
            ) {
                val userachievements = response.body()
                if (userachievements != null) {
                    println("AQUI SE MEUSTRAN!!!!(_:")
                    for (achievement in userachievements) {

                        //guardar el id en la lista ids
                        ids.add(achievement.achievementid)

                    }
                    //enviar la lista al metodo que transforma los ids a nombre objetivos
                    transformobjetivosidanombre2(ids)

                }

            }

            override fun onFailure(call: Call<List<UserAchievements>>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                println("error2")
            }
        })

    }



//METODOS PARA AGREGAR UN AMIGO--------------------------------------------------------------------
//METODO PARA OBTENER EL ID DEL USERNAME
    fun getUseridByUsername7(username: String) {
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val idpropio = sharedPreferences.getString("user_id", null)?.toLongOrNull() ?: 0L

        // Hacer la llamada al servicio para obtener todos los usuarios
        val callUsers = apiService.getUsers()
        callUsers.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                val users = response.body()
                if (users != null) {

                    for (user in users) {
                        if (user.username == username){
                            getAllFriendRequests2(idpropio, user.id.toLong())

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
//COMPROBAR SI HAY OTRA SOLICITUD DE AMISTAD
    fun getAllFriendRequests2(sender: Long, reciever: Long) {
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
                var boleano = 0
                if (friendrequests != null) {
                    for (friendrequest in friendrequests) {
                        //detectar si la solicitud ya existe
                        if (friendrequest.userReciever == reciever){
                            if (friendrequest.userSender == sender){
                                boleano = 1 //la solicitud ya ha sido creada anteriormente
                                Toast.makeText(this@ProfileActivityFriend, "solicitado anteriormente", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                if  (boleano == 0){
                    addFriendRequest(sender, reciever)
                    Toast.makeText(this@ProfileActivityFriend, "solicitado", Toast.LENGTH_SHORT).show()
                }

            }


            override fun onFailure(call: Call<List<FriendRequests>>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
//AGREGAR SOLICITUD DE AMISTAD
    fun addFriendRequest(sender: Long, reciever: Long) {
        // Crear el objeto friendrequest
        val friendrequest = FriendRequests(userSender = sender, userReciever = reciever)

        // Realizar la llamada a la API
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.createFriendRequest(friendrequest)

        call.enqueue(object : Callback<FriendRequests> {
            override fun onResponse(call: Call<FriendRequests>, response: Response<FriendRequests>) {
                if (response.isSuccessful) {
                    val createfriendrequest = response.body()
                    Toast.makeText(this@ProfileActivityFriend, "Amistad creada", Toast.LENGTH_SHORT).show()
                    println("Amistad creada exitosamente: $createfriendrequest")
                } else {
                    Toast.makeText(this@ProfileActivityFriend, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FriendRequests>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

//METODOS PARA BORRAR UN AMGIO---------------------------------------------------------------------
//METODO PARA OBTENER EL ID DEL USERNAME
    fun getUseridByUsername6(username: String) {
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val idpropio = sharedPreferences.getString("user_id", null)?.toLongOrNull() ?: 0L

        // Hacer la llamada al servicio para obtener todos los usuarios
        val callUsers = apiService.getUsers()
        callUsers.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                val users = response.body()
                if (users != null) {

                    for (user in users) {
                        if (user.username == username){
                            getAllFriendShips(user.id.toLong(), idpropio.toString())

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
//FILTRAR EL USUARIO ID PARA SACAR EL ID DEL FRIENDREQUEST Y MANDAR AL METODO DELETEFRIENDREQUEST
    fun getAllFriendShips(friendA: Long, friendB: String) {
        //conectar retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val datos = mutableListOf<String>()

        //hazer llamada = retornar ids de los amgios
        val callfriendre = apiService.getFriendships()
        callfriendre.enqueue(object : Callback<List<Friendships>> {
            override fun onResponse(
                call: Call<List<Friendships>>,
                response: Response<List<Friendships>>
            ) {
                val idpropio = sharedPreferences.getString("user_id", null)

                val friendships = response.body()
                println(friendships)
                //val idpropio = sharedPreferences.getString("user_id", null)
                // Obtener IDs de los amigos
                println("MOSTRAR:-------------->(:----")
                if (friendships != null) {
                    for (friendship in friendships) {
                        //filtrar el id del usuario enviado y el del usuario recivido "logeado"
                        if (friendship.friendA.toString() == friendA.toString()) {
                            if (friendship.friendB.toString() == friendB.toString()) {
                                removeFriend(friendship.friendship)
                            }
                        }
                        if (friendship.friendA.toString() == friendB.toString()) {
                            if (friendship.friendB.toString() == friendA.toString()) {
                                removeFriend(friendship.friendship)
                            }
                        }

                    }

                }
            }

            override fun onFailure(call: Call<List<Friendships>>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
//ELIMINAR AMISTAD
    fun removeFriend(id: Long){
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Hacer la llamada al servicio para eliminar la solicitud de amistad
        val call = apiService.deleteFriendShip(id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                println("amistad borrada")

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error en la conexión
                Toast.makeText(this@ProfileActivityFriend, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//METODOS PARA BORRAR UNA SOLICITUD----------------------------------------------------------------
//METODO PARA OBTENER EL ID DEL USERNAME
    fun getUseridByUsername5(username: String) {
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val idpropio = sharedPreferences.getString("user_id", null)?.toLongOrNull() ?: 0L

        // Hacer la llamada al servicio para obtener todos los usuarios
        val callUsers = apiService.getUsers()
        callUsers.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                val users = response.body()
                if (users != null) {

                    for (user in users) {
                        if (user.username == username){
                            getAllFriendRequests(user.id.toLong(), idpropio.toString())

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
//FILTRAR EL USUARIO ID PARA SACAR EL ID DEL FRIENDREQUEST Y MANDAR AL METODO DELETEFRIENDREQUEST
    fun getAllFriendRequests(sender: Long, reciever: String) {
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
                        //filtrar el id del usuario enviado y el del usuario recivido "logeado"
                        if (friendrequest.userReciever.toString() == reciever){
                            if (friendrequest.userSender.toString() == sender.toString()){

                                //borrar solicitud metodo
                                println("numero friendship----:" + friendrequest.friendrequest)
                                deleteFriendRequestFromApi(friendrequest.friendrequest)
                            }
                        }
                    }
                }

            }


            override fun onFailure(call: Call<List<FriendRequests>>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
//METODO PARA BORRAR SOLICITUD DE AMISTAD
    fun deleteFriendRequestFromApi(friendrequestId: Long) {
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Hacer la llamada al servicio para eliminar la solicitud de amistad
        val call = apiService.deleteFriendRequest(friendrequestId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                println("borrado solicitud")

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error en la conexión
                Toast.makeText(this@ProfileActivityFriend, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


//METODOS PARA AGREGAR UN AMIGO--------------------------------------------------------------------
//OBTENER EL ID DEL USERNAME
    fun getUseridByUsername4(username: String) {
        // Preparar la conexión con Retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val idpropio = sharedPreferences.getString("user_id", null)?.toLongOrNull() ?: 0L

        // Hacer la llamada al servicio para obtener todos los usuarios
        val callUsers = apiService.getUsers()
        callUsers.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                val users = response.body()
                if (users != null) {

                    for (user in users) {
                        if (user.username == username){
                            addFriendship(user.id.toLong(), idpropio.toLong())
                            println("usuario:" + user.id.toLong())
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
//AGREGAR AMIGO
    fun addFriendship(friendAId: Long, friendBId: Long) {
        // Crear el objeto Friendship
        val friendship = Friendships(friendA = friendAId, friendB = friendBId)

        // Realizar la llamada a la API
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.createFriendship(friendship)

        call.enqueue(object : Callback<Friendships> {
            override fun onResponse(call: Call<Friendships>, response: Response<Friendships>) {
                if (response.isSuccessful) {
                    val createdFriendship = response.body()
                    Toast.makeText(this@ProfileActivityFriend, "Amistad creada", Toast.LENGTH_SHORT).show()
                    println("Amistad creada exitosamente: $createdFriendship")
                } else {
                    Toast.makeText(this@ProfileActivityFriend, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Friendships>, t: Throwable) {
                Toast.makeText(this@ProfileActivityFriend, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//METODOS PARA DESCARGAR IMAGEN PERFIL DEL USUARIO--------------------------------------------
//OBTENER EL ID DEL USERNAME
    fun getUseridByUsername8(username: String) {
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
                        if (user.username == username){
                            descargarimagenperfilbd(user.id.toLong())
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
//DESCARGAR IMAGEN DEL USUARIO
    fun descargarimagenperfilbd(userId: Long){
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        val callUser = apiService.getUserById(userId)
        callUser.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                val user = response.body()
                if (user != null) {
                    // Aquí obtienes la URL o datos de la imagen de perfil del usuario

                    val profilePhotoBase64 = user.profilephoto.toString()
                    try {
                        // Decodificar la imagen Base64 a Bitmap
                        val decodedBytes = Base64.decode(profilePhotoBase64, Base64.DEFAULT)
                        val profileBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        // Establecer la imagen en el ImageButton
                        imagenusuarioamigo.setImageBitmap(profileBitmap)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        println("Error al decodificar la imagen de perfil.")
                    }

                } else {
                    println("El usuario no tiene imagen de perfil.")
                }
            }
            override fun onFailure(call: Call<Users>, t: Throwable) {
                println("Error al obtener la imagen de perfil: ${t.message}")
            }
        })
    }


}