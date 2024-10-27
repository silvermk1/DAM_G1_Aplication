package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("birthday") val birthday: String,
    @SerializedName("biography") val biography: String,
    @SerializedName("mail") val mail: String
)