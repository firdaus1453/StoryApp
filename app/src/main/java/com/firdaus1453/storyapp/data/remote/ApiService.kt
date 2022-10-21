package com.firdaus1453.storyapp.data.remote

import com.firdaus1453.storyapp.data.remote.response.DetailStoryResponse
import com.firdaus1453.storyapp.data.remote.response.LoginResponse
import com.firdaus1453.storyapp.data.remote.response.SignupResponse
import com.firdaus1453.storyapp.data.remote.response.StoriesResponse
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun signUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): SignupResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): StoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse
}