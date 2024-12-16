package com.example.dam_g1_aplication.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import com.example.dam_g1_aplication.dataClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivityFriend : AppCompatActivity() {

    private lateinit var textousuario: TextView
    private lateinit var compartir : Button
    private lateinit var listaobjetivos : ListView
    private lateinit var botonsolicitud : Button


//Atributos-datos--
    //nombre usuario
    private lateinit var usuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity_friend)

        var sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

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

//AGREGAR TIPO DE BOTON SOLICITUD DEPENDE DE DONDE PROVENGA EL ACTIVITY:

        intent.putExtra("tipodesolicitud", "solicitado")
        val tiposolicitud = sharedPreferences.getString("tipodesolicitud", null)

        if (tiposolicitud == "solicitado") {
            botonsolicitud.setText("ACEPTAR SOLICITUD")
        }
        if (tiposolicitud == "agregado") {
            botonsolicitud.setText("DEJAR DE SEGIR")
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

//METODO PARA TRANSFORMAR LISTA OBJETIVOSID A LISTA NOMBREOBJETIVOS //NO FUNCIONA!!!
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

//MOSTRAR OBJETIVOS COMPARTIDOS-------
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

}