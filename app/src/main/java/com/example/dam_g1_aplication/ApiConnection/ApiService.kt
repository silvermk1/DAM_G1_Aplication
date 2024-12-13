package com.example.dam_g1_aplication.ApiConnection

import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.Categories
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

//METODOS PARA LA CONEXINO API SERVICE
interface ApiService {
    @GET("categories")
    fun getCategories(): Call<List<Categories>>

    @GET("achievements/category/{categoryId}")
    fun getAchievementsByCategoryId(@Path("categoryId") categoryId: Int): Call<List<Achievements>>

    @GET("users")
    fun getUsers(): Call<List<Users>>

    @POST("users")
    fun createUser(@Body user: Users): Call<Users>

    @GET("userachievement/{achievementId}/{userId}")
    fun getUserAchievement(
        @Path("achievementId") achievementId: String,
        @Path("userId") userId: String
    ): Call<UserAchievements>

    @FormUrlEncoded
    @POST("userachievement")
    fun createUserAchievement(
        @Field("achievementId") achievementId: String?,
        @Field("userId") userId: String?
    ): Call<Void>

    @DELETE("userachievement/{achievementId}/{userId}")
    fun deleteUserAchievement(
        @Path("achievementId") achievementId: Long,
        @Path("userId") userId: Long
    ): Call<Void>

    @GET("users")
    fun getAllUsers(): Call<List<Users>>

    @GET("/userachievements/user/{userId}")
    fun findUserAchievementsByUserId(@Path("userId") userId: Long): Call<List<UserAchievements>>

    @GET("friendships")
    fun getFriendships(): Call<List<Friendships>>

    @GET("friendrequests")
    fun getFriendRequests(): Call<List<FriendRequests>>

}