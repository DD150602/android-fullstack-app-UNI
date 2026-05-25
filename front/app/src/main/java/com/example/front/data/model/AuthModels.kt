package com.example.front.data.model

data class LoginRequest(
    val correo: String,
    val password: String
)

data class RegisterRequest(
    val cedula: String,
    val nombre: String,
    val apellido: String,
    val celular: String,
    val correo: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val nombre: String,
    val correo: String,
    val mensaje: String
)

data class RegisterResponse(
    val mensaje: String,
    val correo: String
)

data class ErrorResponse(
    val error: String? = null
)
