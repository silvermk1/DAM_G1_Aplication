package com.example.dam_g1_aplication.dataClasses

import com.google.gson.annotations.SerializedName

data class Achievements(
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("id") val id: String
)