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
    @SerializedName("youtube") var youtube: String? = null,
    @SerializedName("twitterx") var twitterx: String? = null,
    @SerializedName("facebook") var facebook: String? = null,
    @SerializedName("twitch") var twitch: String? = null,
    @SerializedName("reddit") var reddit: String? = null,
    @SerializedName("steam") var steam: String? = null,
    @SerializedName("epicgames") var epicgames: String? = null,
    @SerializedName("nswitch") var nswitch: String? = null,
    @SerializedName("psn") var psn: String? = null,
    @SerializedName("xbox") var xbox: String? = null,
)
