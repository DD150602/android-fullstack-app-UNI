package com.example.front

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.front.data.model.ProductRequest
import com.example.front.data.network.ApiClient
import com.example.front.data.storage.TokenStorage
import com.example.front.ui.theme.FrontTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductFormActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenStorage = TokenStorage(applicationContext)

        // If product_id is present → edit mode, else → create mode
        val productId = intent.getLongExtra("product_id", -1L)
        val isEditMode = productId != -1L

        setContent {
            var nombre by remember { mutableStateOf(intent.getStringExtra("product_nombre") ?: "") }
            var descripcion by remember { mutableStateOf(intent.getStringExtra("product_descripcion") ?: "") }
            var precio by remember { mutableStateOf(intent.getDoubleExtra("product_precio", 0.0).let { if (it == 0.0) "" else it.toString() }) }
            var stock by remember { mutableStateOf(intent.getIntExtra("product_stock", 0).let { if (it == 0) "" else it.toString() }) }
            var isLoading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            FrontTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isEditMode) "Editar Producto" else "Nuevo Producto",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripcion") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
                    } else {
                        Button(
                            onClick = {
                                val precioDouble = precio.toDoubleOrNull()
                                val stockInt = stock.toIntOrNull()

                                if (nombre.isBlank() || descripcion.isBlank() || precioDouble == null || stockInt == null) {
                                    showToast("Por favor completa todos los campos correctamente")
                                    return@Button
                                }

                                scope.launch {
                                    isLoading = true
                                    try {
                                        val token = tokenStorage.tokenFlow.first()
                                        if (token.isNullOrBlank()) {
                                            goToLogin()
                                            return@launch
                                        }

                                        val request = ProductRequest(
                                            nombre = nombre.trim(),
                                            descripcion = descripcion.trim(),
                                            precio = precioDouble,
                                            stock = stockInt
                                        )

                                        val response = if (isEditMode) {
                                            ApiClient.apiService.updateProduct("Bearer $token", productId, request)
                                        } else {
                                            ApiClient.apiService.createProduct("Bearer $token", request)
                                        }

                                        if (response.isSuccessful) {
                                            showToast(if (isEditMode) "Producto actualizado" else "Producto creado")
                                            finish()
                                        } else {
                                            showToast("Error al guardar producto")
                                        }
                                    } catch (e: Exception) {
                                        showToast("Sin conexion al servidor")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        ) {
                            Text(if (isEditMode) "Actualizar" else "Crear Producto")
                        }
                    }
                }
            }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}