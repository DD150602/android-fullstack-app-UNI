package com.example.front.data.model

data class ProfileResponse(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val cedula: String,
    val celular: String?
)
