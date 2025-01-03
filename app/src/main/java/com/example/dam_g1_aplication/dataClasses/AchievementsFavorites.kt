package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

data class AchievementsFavorites (
    @SerializedName("id")
    val id: Long = 0 ,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("achievementId")
    val achievementId: Long
)


