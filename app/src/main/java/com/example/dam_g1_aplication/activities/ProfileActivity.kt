package com.example.dam_g1_aplication.activities

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.dam_g1_aplication.ApiConnection.ApiService
import com.example.dam_g1_aplication.ApiConnection.RetrofitClient
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

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
    private lateinit var imagenusuario: ImageButton

    //cojer permisos de almazenamiento
    companion object {
        const val REQUEST_IMAGE_PICK = 2
        const val REQUEST_PERMISSION_READ_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.getString("user_id", null)
        println("idpreferences debug" + sharedPreferences.getString("user_id", null))
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
        imagenusuario = findViewById(R.id.profileImageView)
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

        //HAZER FOTO
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no, pedir el permiso
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_STORAGE)
        }

        //cargar imagen perfil
        val userId = sharedPreferences.getString("user_id", null)?.toLongOrNull()
        if (userId != null) {
            descargarimagenperfilbd(userId)
        }
//AGREGAR IMAGEN USUARIO AL PRESIONAR IMAGEN Y AGREGAR A LA BD
        imagenusuario.setOnClickListener {
            actualizarimagenperfil()

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
        } }

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

//METODOS PARA OBTENER PERMISOS
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido, ahora puedes abrir la galería
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a las fotos", Toast.LENGTH_SHORT).show()
            }
        }
    }

//METODO PARA SELECCIONAR IMAGEN GALERIA PARA FOTOPERFIL
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Obtener la URI de la imagen seleccionada
            val selectedImageUri: Uri = data.data ?: return

            // Mostrar la imagen en el ImageButton
            imagenusuario.setImageURI(selectedImageUri)
        }
    }

//METODO PARA SUVIR LA IMAGEN DE PERFIL NUEVA A LA BD
    fun updateUser(userId: Long, updatedUser: Users) {
        // Crear una instancia de Retrofit y el ApiService
        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiService::class.java)

        // Realizar la llamada a la API para actualizar el usuario
        val call = apiService.updateUser(userId, updatedUser)

        // Enviar la llamada de manera asincrónica
        call.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful) {
                    // Usuario actualizado correctamente
                    val updatedUserResponse = response.body()
                    updatedUserResponse?.let {
                        Toast.makeText(this@ProfileActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        println("Usuario actualizado exitosamente: $updatedUserResponse")
                    }
                } else {
                    // Manejo de error
                    Toast.makeText(this@ProfileActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                // Manejo de fallo en la solicitud
                Toast.makeText(this@ProfileActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//METODO PARA ACTUALIZAR LA IMAGEN DE PERFIL DES DE LA BD
    fun actualizarimagenperfil(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*" // Filtrar solo imágenes
        startActivityForResult(intent, REQUEST_IMAGE_PICK)

        //convertir la imagen agregada a string:
        val drawable = imagenusuario.drawable
        //provar con imagen de prueva:
        //val drawable = ResourcesCompat.getDrawable(resources, R.drawable.fotoperfil_ejemplo, null)

        val bitmap = (drawable as BitmapDrawable).bitmap
        val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        compressedBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream) // Reducir calidad
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        //actualizar imagen bd
        val updatedUser = Users(
            id = "1",
            username = "Marc",
            password = null,
            birthday = null,
            biography = null,
            mail = null,
            profilephoto = base64String,
        )

        updateUser(updatedUser.id.toLong(), updatedUser)    }

//METODO PARA PONER IMAGEN PERFIL DE LA BD AL PERFIL
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
                        imagenusuario.setImageBitmap(profileBitmap)
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