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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivityFriend : AppCompatActivity() {

    private lateinit var textousuario: TextView
    private lateinit var compartir : Button
    private lateinit var listaobjetivos : ListView

//Atributos-datos--
    //nombre usuario
    private lateinit var usuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity_friend)

//Declarar atributos con sus respectivos objetos
        textousuario = findViewById(R.id.usuario)
        compartir = findViewById(R.id.compartirlogro)

        //agregar nombreusuario al text view
        var usuario = intent.getStringExtra("nombreusuario")
        textousuario.setText(usuario)

        //instanciar list view
        listaobjetivos = findViewById(R.id.listaobjetivos)
        val objetivosadapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
        listaobjetivos.adapter = objetivosadapter


//CREAR CLICKLISTENER PARA QUE AL PRESIONAR COMPARTIR OBJETIVOS SALGA UN CUADRO EMERJENTE,
//EN ESTE CUADRO EMERJENTE SALDRAN TODOS TUS OBJETIVOS, TENDRAS QUE SELECCIONAR UNO PARA COMPARTIR
//ESTA FUNCION AUN NO ESTA DISPONIBLE
        compartir.setOnClickListener(){
            val instanciaDeClaseA = LoginActivity()
            //var array = instanciaDeClaseA.retornarusuarioiniciado(this)
            //retornarobjetivosusuarioid(array[2].toLong())
        }

        retornarobjetivosusuarioid(1)

    }
//METODO PARA BUSCAR LOS OBJETIVOS DE UN DETERMINADO USUARIOID
    fun retornarobjetivosusuarioid(userId : Long){
        //preparar conexion retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        //lista para guardar los id de lo objetivos del usuario
        val ids: MutableList<Long> = mutableListOf()

    //retornar los logros segun el usuarioid
        val calluser = apiService.findUserAchievementsByUserId(1)
        calluser.enqueue(object : Callback<List<UserAchievements>> {
            override fun onResponse(
                call: Call<List<UserAchievements>>,
                response: Response<List<UserAchievements>>
            ) {
                if (response.isSuccessful) {
                    val userachievements = response.body()!!
                    println("AQUI SE MEUSTRAN!!!!(_:")
                    for (achievement in userachievements) {
                        //println("id:" + achievement.id.toString())
                        //println("id:" + achievement.achievementid.toString())

                        //guardar el id en la lista ids
                        ids.add(achievement.achievementid)

                        //enviar la lista al metodo que transforma los ids a nombre objetivos
                        transformobjetivosidanombre(ids)
                    }
                } else {
                    println("Error de respuesta: " + response.code())
                    response.errorBody()?.let { errorBody ->
                        println("Cuerpo del error: " + errorBody.string())
                    }
                    Toast.makeText(this@ProfileActivityFriend, "Error en la respuesta", Toast.LENGTH_SHORT).show()
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
            if (response.isSuccessful) {
                val objetivos = response.body()!!
                // Obtener IDs de los amigos
                for (objetivo in objetivos) {
                    for (objetivoId in objetivosid) {
                        if (objetivo.id.equals(objetivoId)){
                            nombres.add(objetivo.title)
                        }
                    }
                }
                //AGREGAR AL LIST VIEW TODOS LOS VALORES
                actualizarListView(nombres)
            } else {
                Toast.makeText(this@ProfileActivityFriend, "Error al obtener nombres", Toast.LENGTH_SHORT).show()
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


}