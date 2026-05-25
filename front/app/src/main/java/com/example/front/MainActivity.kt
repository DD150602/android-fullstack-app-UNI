package com.example.front

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.front.data.storage.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(applicationContext)

        lifecycleScope.launch {
            val token = tokenStorage.tokenFlow.first()
            val nextActivity = if (token.isNullOrBlank()) {
                LoginActivity::class.java
            } else {
                ProfileActivity::class.java
            }

            startActivity(Intent(this@MainActivity, nextActivity))
            finish()
        }
    }
}
