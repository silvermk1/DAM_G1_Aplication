package com.example.dam_g1_aplication.ConexionApi.CallbacksUsuario;



import com.example.dam_g1_aplication.Objetos.Usuario;

import java.util.List;

public interface CallbackUsuarios {
    void onUsersReceived(List<Usuario> users); // Método para recibir la lista de usuarios
}

