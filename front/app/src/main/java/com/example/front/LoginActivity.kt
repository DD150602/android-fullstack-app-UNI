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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.front.data.model.ErrorResponse
import com.example.front.data.model.LoginRequest
import com.example.front.data.network.ApiClient
import com.example.front.data.storage.TokenStorage
import com.example.front.ui.theme.FrontTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(applicationContext)

        setContent {
            FrontTheme {
                LoginScreen(
                    onLogin = { correo, password, onComplete ->
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.login(
                                    LoginRequest(correo = correo, password = password)
                                )

                                if (response.isSuccessful) {
                                    val body = response.body()
                                    if (body != null) {
                                        tokenStorage.saveToken(body.token)
                                        startActivity(
                                            Intent(this@LoginActivity, ProfileActivity::class.java)
                                        )
                                        finish()
                                    } else {
                                        showToast("Respuesta vacia del servidor")
                                    }
                                } else {
                                    val errorText = response.errorBody()?.string()
                                    val errorMessage = errorText
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { Gson().fromJson(it, ErrorResponse::class.java)?.error }
                                        ?: "Login fallido"
                                    showToast(errorMessage)
                                }
                            } catch (_: Exception) {
                                showToast("No se pudo conectar con API")
                            } finally {
                                onComplete()
                            }
                        }
                    },
                    onOpenRegister = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                    }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun LoginScreen(
    onLogin: (String, String, () -> Unit) -> Unit,
    onOpenRegister: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesion",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contrasena") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        Button(
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    onLogin(correo.trim(), password) {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp))
            } else {
                Text("Entrar")
            }
        }

        TextButton(
            onClick = onOpenRegister,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Crear cuenta")
        }
    }
}
