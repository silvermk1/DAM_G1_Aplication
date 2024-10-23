package com.example.dam_g1_aplication.ConexionApi;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.dam_g1_aplication.ConexionApi.CallbacksUsuario.CallbackUsuario;
import com.example.dam_g1_aplication.ConexionApi.CallbacksUsuario.CallbackUsuarios;
import com.example.dam_g1_aplication.Objetos.Usuario;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//CONEXION A LA API
public class ConexionApiUsuario {

    private ApiService apiService; //OBJETO DE LA CLASSE QUE CONTIENE EL INDEX DE LOS METODOS
    Usuario usuario;

    // Constructor que inicializa Retrofit
    public ConexionApiUsuario() {
        // Inicializar Retrofit
        apiService = RetrofitClient.getClient("http://192.168.1.60:8080/") // Dirección IP y puerto del servidor API
                .create(ApiService.class);
    }

    // Método para obtener todos los usuarios con sus atributos
    //se retornara la lista con un callback des de la interfaz CallbackUsuarios
    public void getAllUsers(Context context, CallbackUsuarios callbak) { //context para editar la salida terminal
        Call<List<Usuario>> call = apiService.findAllUsers(); //almazenar usuarios en la lista call
        call.enqueue(new Callback<List<Usuario>>() { //callbac para que la aplicacion no se bloquee mientras llama
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) { //metodo que se ejecuta cuando la llamada se completa
                if (response.isSuccessful() && response.body() != null) { //condiconal = la respuesta api fue exitosa
                    List<Usuario> users = response.body(); //guardar los usuarios en la lista
                    callbak.onUsersReceived(users); //agregar el callback para retornar los usuarios
                    for (Usuario user : users) { //"ejemplo para recorrer el nombre"
                        System.out.println(user);
                    }
                } else { //respuesta de error, api no completada
                }
            }

            @Override //se activara si la llamada falla...
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("ConexionApi", "Error en la llamada: " + t.getMessage());
            }
        });

    }

    //Metodo para crear un nuevo usuario "pasar el objeto usuarionuevo con suus atributos"
    public void createUser(Context context, Usuario usuarionuevo){ //context para editar la salida y usuarionuevo con sus atributos
        Call<Usuario> call = apiService.createUser(usuarionuevo); //almazenar el usuario en una lista call
        call.enqueue(new Callback<Usuario>() { ///callbac para que la aplicacion no se bloquee mientras llama
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) { //metodo que se ejecuta cuando la llamada se completa
                if (response.isSuccessful() && response.body() != null) { //condiconal = la respuesta api fue exitosa
                    Toast.makeText(context, "Usuario creado con éxito.", Toast.LENGTH_SHORT).show();
                } else { //error
                    Toast.makeText(context, "No se pudo crear el usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override //se activa si la llamada falla...
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ConexionApi", "Error en la llamada: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo para borrar un usuario "pasar el id para saver cual"
    public void deleteUser(Context context, int id){ //context para editar la salida y id usuario para saver cual se quiere eliminar
        Call<Void> call = apiService.deleteUser(id); // Llamada para eliminar el usuario
        call.enqueue(new Callback<Void>() { //callbac para que la aplicacion no se bloquee mientras llama
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {  //metodo que se ejecuta cuando la llamada se completa
                if (response.isSuccessful()) { //condicional = la respuesta fue exitosa
                    Toast.makeText(context, "Usuario eliminado con éxito.", Toast.LENGTH_SHORT).show();
                } else { //error
                    Toast.makeText(context, "No se pudo eliminar el usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override //se activa si la llamada falla....
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ConexionApi", "Error en la llamada: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo para obtener solo un usuario a traves de su id
    //se retornara el usuario con un callback des de la interfaz CallbackUsuario
    public void findUserById(Context context, int id, CallbackUsuario callback){ //context para editar la salida y id para saver cual usuario se quiere obtener
        Call<Usuario> call = apiService.findUserById(id); //llamda para obtener el usuario
        call.enqueue(new Callback<Usuario>() { //callbac para que la aplicacion no se bloquee mientras llama
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) { //metodo que se ejecuta cuando la llamada se completa
                if (response.isSuccessful() && response.body() != null) { // condicional = la respuesta fue exitosa y el cuerpo no es nulo
                    Usuario usuario = response.body(); // obtener el usuario
                    callback.onSuccess(usuario); //agregar el callback para retornar el usuario
                } else { // error

                }
            }

            @Override //se activa si la llamda falla...
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });

    }

    //Metodo para actualizar usuario , pasar el usuario actualizado
    public void updateUser(Context context, int id, Usuario updatedUser){
        Call<Usuario> call = apiService.updateUser(id, updatedUser); //llamda para actualizar el usuario
        call.enqueue(new Callback<Usuario>() { //callback para que la aplicacion no se bloquee mientras llama
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {  //metodo que se ejecuta cuando la llamada se completa
                if (response.isSuccessful() && response.body() != null) { //condicional = la respuesta fue exitosa
                    Toast.makeText(context, "Usuario actualizado exitosamente: ", Toast.LENGTH_SHORT).show();
                } else { // error
                    Toast.makeText(context, "No se pudo actualizar el usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override //se activa si la llamada falla...
            public void onFailure(Call<Usuario> call, Throwable t) {
                String errorMessage = "Error en la llamada: " + t.getMessage();
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}