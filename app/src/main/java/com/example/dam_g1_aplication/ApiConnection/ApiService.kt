package com.example.dam_g1_aplication.ApiConnection

import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.Categories
import com.example.dam_g1_aplication.dataClasses.FriendRequests
import com.example.dam_g1_aplication.dataClasses.Friendships
import com.example.dam_g1_aplication.dataClasses.Users
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("/userachievement/{userId}")   //FUNCIONA?
    fun findUserAchievementsByUserId(@Path("userId") userId: Long): Call<List<UserAchievements>>

    @GET("/friendships")
    fun getFriendships(): Call<List<Friendships>>

    @GET("/friendrequests")
    fun getFriendRequests(): Call<List<FriendRequests>>

    @GET("/Achievements")
    fun getAchievements(): Call<List<Achievements>>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Long): Call<Users>

    @POST("/friendships")
    fun createFriendship(@Body friendship: Friendships): Call<Friendships>

    @POST("/friendrequests")
    fun createFriendRequest(@Body friendrequest: FriendRequests): Call<FriendRequests>

    @DELETE("friendrequests/{friendrequestId}")
    fun deleteFriendRequest(@Path("friendrequestId") friendrequestId: Long): Call<Void>

    @DELETE("/friendships/{friendshipId}")
    fun deleteFriendShip(@Path("friendshipId") friendshipId: Long): Call<Void>

    @PUT("users/{id}")
    fun updateUser(@Path("id") userId: Long, @Body updatedUser: Users): Call<Users>
}