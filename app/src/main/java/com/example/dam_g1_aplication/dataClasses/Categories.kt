package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

//CATEGORIAS
data class Categories(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
)