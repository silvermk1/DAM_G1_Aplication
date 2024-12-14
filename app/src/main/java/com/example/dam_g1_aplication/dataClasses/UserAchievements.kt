package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

//OBJETIVOS DE USUARIO
data class UserAchievements(
    @SerializedName("id") val id: Long,
    @SerializedName("achievementid") val achievementid: Long,
    @SerializedName("userid") val userid: Long,
    @SerializedName("likes") val likes: Long,
    @SerializedName("dislikes") val dislikes: Int,
)