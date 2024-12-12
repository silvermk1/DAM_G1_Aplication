package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

//OBJETIVOS DE USUARIO
data class UserAchievements(
    @SerializedName("id") val id: String,
    @SerializedName("achievementid") val achievementid: String,
    @SerializedName("userid") val userid: String,
    @SerializedName("likes") val likes: String,
    @SerializedName("dislikes") val dislikes: String,
)