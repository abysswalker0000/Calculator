package com.example.androidcalculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetupPassKeyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setContentView(ComposeView(this).apply {
            setContent {
                SetupPassKeyScreen(sharedPreferences)
            }
        })
    }

    @Composable
    fun SetupPassKeyScreen(sharedPreferences: android.content.SharedPreferences) {
        var passKey by remember { mutableStateOf("") }
        var confirmPassKey by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = passKey,
                    onValueChange = { passKey = it },
                    label = { Text("Введите Pass Key") },
                    visualTransformation = PasswordVisualTransformation()
                )
                TextField(
                    value = confirmPassKey,
                    onValueChange = { confirmPassKey = it },
                    label = { Text("Подтвердите Pass Key") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    if (passKey == confirmPassKey && passKey.isNotEmpty()) {
                        savePassKey(passKey, sharedPreferences)
                    } else if (passKey.isEmpty()) {
                        errorMessage = "Pass Key не может быть пустым"
                    } else {
                        errorMessage = "Pass Key не совпадает"
                    }
                }) {
                    Text("Установить")
                }
                errorMessage?.let { Text(it, color = Color.Red) }
            }
        }
    }

    private fun savePassKey(passKey: String, sharedPreferences: android.content.SharedPreferences) {
        val credentialManager = CredentialManager.create(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                val request = CreatePasswordRequest("calculator_user", passKey)
                credentialManager.createCredential(this@SetupPassKeyActivity, request)
                sharedPreferences.edit().putString("pass_key", passKey).apply()
                runOnUiThread {
                    Toast.makeText(this@SetupPassKeyActivity, "Pass Key установлен", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SetupPassKeyActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this@SetupPassKeyActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}