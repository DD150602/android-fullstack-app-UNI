package com.example.front

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.front.data.model.RegisterRequest
import com.example.front.data.network.ApiClient
import com.example.front.ui.theme.FrontTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontTheme {
                RegisterScreen(
                    onRegister = { request, onComplete ->
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.register(request)

                                if (response.isSuccessful) {
                                    val message = response.body()?.mensaje ?: "Registro exitoso"
                                    showToast(message)
                                    finish()
                                } else {
                                    val errorText = response.errorBody()?.string()
                                    val errorMessage = errorText
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { Gson().fromJson(it, ErrorResponse::class.java)?.error }
                                        ?: "Registro fallido"
                                    showToast(errorMessage)
                                }
                            } catch (_: Exception) {
                                showToast("No se pudo conectar con API")
                            } finally {
                                onComplete()
                            }
                        }
                    },
                    onBack = { finish() }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun RegisterScreen(
    onRegister: (RegisterRequest, () -> Unit) -> Unit,
    onBack: () -> Unit
) {
    var cedula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        SimpleField(
            value = cedula,
            onValueChange = { cedula = it },
            label = "Cedula",
            topPadding = 24
        )
        SimpleField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre"
        )
        SimpleField(
            value = apellido,
            onValueChange = { apellido = it },
            label = "Apellido"
        )
        SimpleField(
            value = celular,
            onValueChange = { celular = it },
            label = "Celular"
        )
        SimpleField(
            value = correo,
            onValueChange = { correo = it },
            label = "Correo"
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
                    onRegister(
                        RegisterRequest(
                            cedula = cedula.trim(),
                            nombre = nombre.trim(),
                            apellido = apellido.trim(),
                            celular = celular.trim(),
                            correo = correo.trim(),
                            password = password
                        )
                    ) {
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
                Text("Registrarse")
            }
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Volver al login")
        }
    }
}

@Composable
private fun SimpleField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    topPadding: Int = 12
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding.dp)
    )
}
