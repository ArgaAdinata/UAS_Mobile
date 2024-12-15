package com.example.uas_mobile_argaadinata_514333.network

import retrofit2.Call
import retrofit2.http.*

data class RegisterRequest(
    val name: String,
    val address: String,
    val username: String,
    val phone: String,
    val password: String
)

data class UserResponse(
    val _id: String,
    val name: String,
    val username: String,
    val address: String,
    val phone: String,
    val password: String
)

data class NoteRequest(
    val userId: String,
    val title: String,
    val content: String
)

data class NoteResponse(
    val _id: String,
    val userId: String,
    val title: String,
    val content: String
)

data class MessageRes(
    val message: String
)

interface ApiService {
    @POST("user")
    fun register(@Body request: RegisterRequest): Call<UserResponse>

    @GET("user")
    fun getAllUsers(): Call<List<UserResponse>>

    @GET("user/{id}")
    fun getUserById(@Path("id") id: String): Call<UserResponse>

    @POST("note")
    fun addNote(@Body request: NoteRequest): Call<NoteResponse>

    @GET("note")
    fun getAllNotes(): Call<List<NoteResponse>>

    @GET("note/{id}")
    fun getNoteById(@Path("id") id: String): Call<NoteResponse>

    @POST("note/{id}")
    fun updateNote(@Path("id") id: String, @Body request: NoteRequest): Call<NoteResponse>

    @DELETE("note/{id}")
    fun deleteNote(@Path("id") id: String): Call<MessageRes>

}