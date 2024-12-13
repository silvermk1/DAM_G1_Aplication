package com.example.dam_g1_aplication.dataClasses
import com.google.gson.annotations.SerializedName

data class FriendRequests(
    @SerializedName("friendrequests") val friendrequests: Long,
    @SerializedName("userSender") val userSender: Long,
    @SerializedName("userReciever") val userReciever: Long
)