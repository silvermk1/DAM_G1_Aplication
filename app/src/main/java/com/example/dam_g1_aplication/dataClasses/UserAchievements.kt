package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

data class UserAchievements(
    @SerializedName("id") val id: String,
    @SerializedName("achievementid") val achievementid: String,
    @SerializedName("userid") val userid: String,
    @SerializedName("completationdate") val completationdate: String
)