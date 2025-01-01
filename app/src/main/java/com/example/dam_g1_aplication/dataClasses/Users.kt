package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

//USUARIO
data class Users(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("birthday") val birthday: String? = null,
    @SerializedName("biography") val biography: String? = null,
    @SerializedName("mail") val mail: String? = null,
    @SerializedName("profilephoto") val profilephoto: String? = null,

)
