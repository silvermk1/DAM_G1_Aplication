package com.example.dam_g1_aplication.ProgramacionAsincronica;
import android.content.Context;


import android.util.Log;

import com.example.dam_g1_aplication.ConexionApi.CallbacksUsuario.CallbackUsuario;
import com.example.dam_g1_aplication.ConexionApi.CallbacksUsuario.CallbackUsuarios;
import com.example.dam_g1_aplication.ConexionApi.ConexionApiUsuario;
import com.example.dam_g1_aplication.Objetos.Usuario;

import java.util.List;

//AQUI TODOS LOS CALLBACKS CREADOS
//CUANDO QUIERES RETORNAR UN DATO DE LA API SE HACE CON CALLBACKS

public class CallbacksUsuario {
    //CALLBACKS PARA SELECCIONAR TODOS LOS USUARIOS
    //CALBACK DE EJEMPLO,  TAMBIEN PUEDES PASAR METODOS POR PARAMETROS PARA EJECUTARLOS DENTRO...
    public void recibirtodoslosusuarios(Context context){

        ConexionApiUsuario conexionApi = new ConexionApiUsuario();
        conexionApi.getAllUsers(context, new CallbackUsuarios() {
            @Override
            public void onUsersReceived(List<Usuario> users) {
                //USUARIOS RETORNADOS
                for (Usuario user : users) {

                    System.out.println(user.getUsername() + user.getId());
                }
            }
        });

    }
    //CALLBACKS PARA SELECCIONAR UN USUARIO EN CONCRETO
    //CALLBACK DE EJEMPLO, TAMBIEN PUEDES PASAR METODOS POR PARAMETROS PARA EJECUTARLOS DENTRO...
    public void recibirUsuarioPorId(Context context, int id) {
        ConexionApiUsuario conexionApi = new ConexionApiUsuario();

        conexionApi.findUserById(context, id, new CallbackUsuario() {
            @Override
            public void onSuccess(Usuario usuario) {
                //USUARIO RETORNADO
                //usuario.metododeprueva();
            }
        });
    }

}
