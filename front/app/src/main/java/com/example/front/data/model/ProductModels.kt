package com.example.front.data.model

data class Product(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int
)

data class ProductRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int
)