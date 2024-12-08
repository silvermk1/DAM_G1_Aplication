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
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivityFriend : AppCompatActivity() {

    private lateinit var textousuario: TextView
    private lateinit var compartir : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity_friend)

//recoger el nombre de usuario con getString
        val usuario = intent.getStringExtra("nombreusuario")

//Declarar atributos con sus respectivos objetos
        textousuario = findViewById(R.id.usuario)
        compartir = findViewById(R.id.compartirlogro)

        //agregar nombreusuario al list view
        textousuario.setText(usuario)

//CREAR CLICKLISTENER PARA QUE AL PRESIONAR COMPARTIR OBJETIVOS SALGA UN CUADRO EMERJENTE,
//EN ESTE CUADRO EMERJENTE SALDRAN TODOS TUS OBJETIVOS, TENDRAS QUE SELECCIONAR UNO PARA COMPARTIR
        compartir.setOnClickListener(){
            val instanciaDeClaseA = LoginActivity()
            var array = instanciaDeClaseA.retornarusuarioiniciado(this)
            //retornarobjetivosusuarioid(array[2].toLong())
            prueva2(1)
        }
    }
    fun prueva2(userId : Long){
        //preparar conexion retrofit
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        //retornar los logros segun el usuarioid
        val callUserAchievementsByUserId = apiService.findUserAchievementsByUserId(userId)
        callUserAchievementsByUserId.enqueue(object : Callback<List<UserAchievements>> {
            override fun onResponse(
                call: Call<List<UserAchievements>>,
                response: Response<List<UserAchievements>>
            ) {
                if (response.isSuccessful) {
                    val userachievements = response.body()!!
                    println("AQUI SE MEUSTRAN!!!!(:")
                    for (achievement in userachievements) {
                        println("id:" + achievement.id.toString())
                        println("id:" + achievement.achievementid)
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
    /*
    fun prueva(userId: Long) {
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Llamar al método para obtener logros por userId
        val call = apiService.getUserAchievementsByUserId(userId)
        println("MOSTRAR!------------------")
        // Ejecutar la llamada de forma asíncrona
        call.enqueue(object : Callback<List<UserAchievements>> {
            override fun onResponse(
                call: Call<List<UserAchievements>>,
                response: Response<List<UserAchievements>>
            ) {
                if (response.isSuccessful) {
                    val logros = response.body()
                    println("Logros obtenidos para el usuario $userId: $logros")
                } else {
                    // Detallar el error con el código de estado y el cuerpo de error
                    println("Error al obtener logros: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<UserAchievements>>, t: Throwable) {
                println("Error de red o servidor: ${t.message}")
            }
        })
    }
*/
/*
//METODO PARA BUSCAR TODOS LOS ID DE LOS OBJETIVOS DEL USUARIO
    //ESTE METODO TODAVIA NO FUNCIONA !!!!!! ----NO RETORNA BIEN LOS OBJETIVOS!!!!
private fun retornarobjetivosusuarioid(userid: Long) {
    println("userid:!!!-.----")
    println(userid)
    val idlogros: MutableList<String> = mutableListOf()

    // Iniciar servicio API
    val retrofit = RetrofitClient.getClient()
    val apiService = retrofit.create(ApiService::class.java)

    // Realizar la llamada asíncrona a la API
    apiService.findByUserId(userid).enqueue(object : Callback<List<UserAchievements>> {
        override fun onResponse(
            call: Call<List<UserAchievements>>,
            response: Response<List<UserAchievements>>
        ) {
            if (response.isSuccessful && response.body() != null) {
                val userAchievements = response.body()!!
                println("imprimir lista:!----")
                println(userAchievements)
                // Agregar los id a la lista
                if (userAchievements.isNotEmpty()) {
                    for (achievement in userAchievements) {
                        idlogros.add(achievement.achievementid.toString())
                    }
                } else {
                    println("no se encontraron logros de usuario en la bd")
                }
            } else {

            }

            if (idlogros.isEmpty()) {
                idlogros.add("no tienes logros")
            }
            println("imprimir lista2:!----")
            println(idlogros)
            compartirobjetivo(idlogros)
        }
        override fun onFailure(call: Call<List<UserAchievements>>, t: Throwable) {
            // En caso de error de red u otro fallo
            println("Error en la conexión")
        }
    })
}
*/


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