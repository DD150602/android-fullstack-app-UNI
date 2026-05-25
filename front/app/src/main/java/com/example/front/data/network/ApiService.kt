package com.example.front.data.network

import com.example.front.data.model.AuthResponse
import com.example.front.data.model.LoginRequest
import com.example.front.data.model.Product
import com.example.front.data.model.ProductRequest
import com.example.front.data.model.ProfileResponse
import com.example.front.data.model.RegisterRequest
import com.example.front.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    // ── Auth ──────────────────────────────────────────────
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // ── User ──────────────────────────────────────────────
    @GET("api/user/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): Response<ProfileResponse>

    // ── Products ──────────────────────────────────────────
    @GET("api/products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String
    ): Response<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Response<Product>

    @POST("api/products")
    suspend fun createProduct(
        @Header("Authorization") authorization: String,
        @Body request: ProductRequest
    ): Response<Product>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body request: ProductRequest
    ): Response<Product>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Response<Unit>
}