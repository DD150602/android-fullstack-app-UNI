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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.front.data.model.ProfileResponse
import com.example.front.data.network.ApiClient
import com.example.front.data.storage.TokenStorage
import com.example.front.ui.theme.FrontTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(applicationContext)

        setContent {
            var profile by remember { mutableStateOf<ProfileResponse?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            FrontTheme {
                ProfileScreen(
                    profile = profile,
                    isLoading = isLoading,
                    onNavigateToProducts = {
                        startActivity(Intent(this@ProfileActivity, ProductsActivity::class.java))
                    },
                    onLogout = {
                        lifecycleScope.launch {
                            tokenStorage.clearToken()
                            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                )
            }

            LaunchedEffect(Unit) {
                try {
                    val token = tokenStorage.tokenFlow.first()
                    if (token.isNullOrBlank()) {
                        goToLogin()
                        return@LaunchedEffect
                    }

                    val response = ApiClient.apiService.getProfile("Bearer $token")
                    if (response.isSuccessful) {
                        profile = response.body()
                    } else {
                        showToast("Sesion invalida")
                        tokenStorage.clearToken()
                        goToLogin()
                    }
                } catch (_: Exception) {
                    showToast("No se pudo cargar perfil")
                } finally {
                    isLoading = false
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

@Composable
private fun ProfileScreen(
    profile: ProfileResponse?,
    isLoading: Boolean,
    onNavigateToProducts: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            profile != null -> {
                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.headlineMedium
                )
                ProfileLine("Nombre", "${profile.nombre} ${profile.apellido}")
                ProfileLine("Correo", profile.correo)
                ProfileLine("Cedula", profile.cedula)
                ProfileLine("Celular", profile.celular ?: "")

                Button(
                    onClick = onNavigateToProducts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Ver Productos")
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Text("Cerrar sesion")
                }
            }

            else -> {
                Text(
                    text = "No se pudo cargar perfil",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}
