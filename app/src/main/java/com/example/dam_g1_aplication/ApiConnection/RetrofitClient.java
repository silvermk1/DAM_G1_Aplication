package com.example.dam_g1_aplication.ApiConnection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//CLASE PARA CONFIGURAR RETROFIT
public class RetrofitClient {
    private static Retrofit retrofit = null;

    //ingresar la ip de el framework y api "puerto"
    private static String baseUrl = "http://192.168.1.38:8080/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}