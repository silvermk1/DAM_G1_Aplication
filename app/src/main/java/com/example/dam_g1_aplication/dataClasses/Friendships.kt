package com.example.dam_g1_aplication.dataClasses
import com.google.gson.annotations.SerializedName

data class Friendships(
    @SerializedName("friendship") val friendship: Long,
    @SerializedName("friendA") val friendA: Long,
    @SerializedName("friendB") val friendB: Long
)