package com.example.front

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.front.data.model.Product
import com.example.front.data.network.ApiClient
import com.example.front.data.storage.TokenStorage
import com.example.front.ui.theme.FrontTheme
import kotlinx.coroutines.flow.first

class ProductDetailActivity : ComponentActivity() {

    private var loadTrigger = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenStorage = TokenStorage(applicationContext)
        val productId = intent.getLongExtra("product_id", -1L)

        setContent {
            var product by remember { mutableStateOf<Product?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var isDeleting by remember { mutableStateOf(false) }
            val trigger by loadTrigger

            FrontTheme {
                LaunchedEffect(trigger) {
                    isLoading = true
                    try {
                        val token = tokenStorage.tokenFlow.first()
                        if (token.isNullOrBlank()) { goToLogin(); return@LaunchedEffect }
                        val response = ApiClient.apiService.getProduct("Bearer $token", productId)
                        if (response.isSuccessful) {
                            product = response.body()
                        } else {
                            showToast("Error al cargar producto")
                            finish()
                        }
                    } catch (e: Exception) {
                        showToast("Sin conexion al servidor")
                        finish()
                    } finally {
                        isLoading = false
                    }
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Eliminar producto") },
                        text = { Text("¿Estas seguro que deseas eliminar este producto?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false
                                isDeleting = true
                                // delete handled in LaunchedEffect below
                            }) { Text("Eliminar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                        }
                    )
                }

                if (isDeleting) {
                    LaunchedEffect(Unit) {
                        try {
                            val token = tokenStorage.tokenFlow.first()
                            if (token.isNullOrBlank()) { goToLogin(); return@LaunchedEffect }
                            val response = ApiClient.apiService.deleteProduct("Bearer $token", productId)
                            if (response.isSuccessful) {
                                showToast("Producto eliminado")
                                finish()
                            } else {
                                showToast("Error al eliminar")
                            }
                        } catch (e: Exception) {
                            showToast("Sin conexion al servidor")
                        } finally {
                            isDeleting = false
                        }
                    }
                }

                ProductDetailScreen(
                    product = product,
                    isLoading = isLoading,
                    onEdit = {
                        val intent = Intent(this, ProductFormActivity::class.java)
                        intent.putExtra("product_id", productId)
                        intent.putExtra("product_nombre", product?.nombre)
                        intent.putExtra("product_descripcion", product?.descripcion)
                        intent.putExtra("product_precio", product?.precio)
                        intent.putExtra("product_stock", product?.stock)
                        startActivity(intent)
                    },
                    onDelete = { showDeleteDialog = true }
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadTrigger.value++
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun ProductDetailScreen(
    product: Product?,
    isLoading: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> CircularProgressIndicator()

            product != null -> {
                Text(
                    text = "Detalle del Producto",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                ProductDetailLine("Nombre", product.nombre)
                ProductDetailLine("Descripcion", product.descripcion)
                ProductDetailLine("Precio", "$${product.precio}")
                ProductDetailLine("Stock", product.stock.toString())

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar")
                    }
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }

            else -> {
                Text(
                    text = "Producto no encontrado",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ProductDetailLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}