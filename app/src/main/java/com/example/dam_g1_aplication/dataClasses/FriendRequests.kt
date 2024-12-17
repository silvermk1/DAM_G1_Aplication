package com.example.dam_g1_aplication.dataClasses
import com.google.gson.annotations.SerializedName

data class FriendRequests(
    @SerializedName("friendrequest") val friendrequest: Long = 0,
    @SerializedName("userSender") val userSender: Long,
    @SerializedName("userReciever") val userReciever: Long
)