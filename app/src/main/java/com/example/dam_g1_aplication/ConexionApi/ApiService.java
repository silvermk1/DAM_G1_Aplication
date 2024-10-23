package com.example.dam_g1_aplication.ConexionApi;


import com.example.dam_g1_aplication.Objetos.Usuario;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

//API CON TODOS LOS METODOS DE LA APLICACION BACKEND
public interface ApiService {
    //METODOS USUARIO!!!
    //obtener los usuarios
    @GET("users")
    Call<List<Usuario>> findAllUsers();

    //crear un nuevo usuario
    @POST("users")
    Call<Usuario> createUser(@Body Usuario newUser);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    // Obtener un usuario por ID
    @GET("users/{id}")
    Call<Usuario> findUserById(@Path("id") int id);

    // Actualizar un usuario
    @PUT("users/{id}")
    Call<Usuario> updateUser(@Path("id") int id, @Body Usuario updatedUser);

    // Actualizar la biografía de un usuario
    @PUT("users/{id}/biography")
    Call<Usuario> updateBiography(@Path("id") int id, @Body Usuario updatedUser);

//METODOS OBJETIVOS!!
    //...
//METODOS CATEGORIAS!!
    //...
}
