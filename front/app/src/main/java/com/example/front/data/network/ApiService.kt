package com.example.front.data.network

import com.example.front.data.model.AuthResponse
import com.example.front.data.model.LoginRequest
import com.example.front.data.model.ProfileResponse
import com.example.front.data.model.RegisterRequest
import com.example.front.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/user/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): Response<ProfileResponse>
}
