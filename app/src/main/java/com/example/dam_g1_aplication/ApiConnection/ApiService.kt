package com.example.dam_g1_aplication.ApiConnection

import com.example.dam_g1_aplication.dataClasses.Achievements
import com.example.dam_g1_aplication.dataClasses.Categories
import com.example.dam_g1_aplication.dataClasses.Users
import com.example.dam_g1_aplication.dataClasses.UserAchievements
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("categories")
    fun getCategories(): Call<List<Categories>>

    @GET("achievements")
    fun getAchievements(): Call<List<Achievements>>


    @GET("achievements/category/{categoryId}")
    fun getAchievementsByCategoryId(@Path("categoryId") categoryId: Long): Call<List<Achievements>>



    @GET("users")
    fun getUsers(): Call<List<Users>>

    @DELETE("/users/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<Void>

    @POST("users")
    fun createUser(@Body user: Users): Call<Users>

    @GET("userachievement")
    fun getUserAchievements(): Call<List<UserAchievements>>

    @POST("userachievement")
    fun createUserAchievement(@Body userAchievement: UserAchievements): Call<UserAchievements>
}