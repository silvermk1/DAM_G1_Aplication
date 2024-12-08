package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

//OBJETIVOS DE USUARIO
data class UserAchievements(
    @SerializedName("id") val id: Long,
    @SerializedName("achievementid") val achievementid: Long,
    @SerializedName("userid") val userid: Long,
    @SerializedName("likes") val likes: Int,
    @SerializedName("dislikes") val dislikes: Int,
    @SerializedName("compliment") val compliment: Char
//he sacado la columna completationdate, ya que no esta en la tabla!!!!

)