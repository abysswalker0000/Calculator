package com.example.androidcalculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
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

        val passKey = sharedPreferences.getString("pass_key", null)
        if (passKey == null) {
            startActivity(Intent(this, SetupPassKeyActivity::class.java))
            finish()
            return
        }

        setContentView(ComposeView(this).apply {
            setContent {
                LoginScreen(sharedPreferences)
            }
        })

        // Пробуем автологин
        autoLogin(sharedPreferences)
    }

    @Composable
    fun LoginScreen(sharedPreferences: android.content.SharedPreferences) {
        var passKeyInput by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Column {
            TextField(
                value = passKeyInput,
                onValueChange = { passKeyInput = it },
                label = { Text("Введите Pass Key") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = {
                if (validatePassKey(passKeyInput, sharedPreferences)) {
                    grantAccess(sharedPreferences)
                } else {
                    errorMessage = "Неверный Pass Key"
                }
            }) {
                Text("Войти")
            }
            TextButton(onClick = {
                setupBiometricAuthentication(sharedPreferences)
            }) {
                Text("Сбросить Pass Key")
            }
            errorMessage?.let { Text(it, color = Color.Red) }
        }
    }

    private fun validatePassKey(enteredKey: String, sharedPreferences: android.content.SharedPreferences): Boolean {
        val storedKey = sharedPreferences.getString("pass_key", null)
        return enteredKey == storedKey
    }

    private fun grantAccess(sharedPreferences: android.content.SharedPreferences) {
        sharedPreferences.edit().putBoolean("is_authenticated", true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun autoLogin(sharedPreferences: android.content.SharedPreferences) {
        val credentialManager = CredentialManager.create(this)
        val request = GetCredentialRequest(listOf(GetPasswordOption()))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                val credential = result.credential
                if (credential is PasswordCredential) {
                    val storedPassKey = credential.password
                    if (validatePassKey(storedPassKey, sharedPreferences)) {
                        runOnUiThread { grantAccess(sharedPreferences) }
                    } else {
                        runOnUiThread { Toast.makeText(this@LoginActivity, "Неверный Pass Key", Toast.LENGTH_SHORT).show() }
                    }
                }
            } catch (e: Exception) {
                Log.e("AutoLogin", "Ошибка автологина: ${e.message}")
            }
        }
    }

    private fun setupBiometricAuthentication(sharedPreferences: android.content.SharedPreferences) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(this@LoginActivity, "Ошибка биометрии: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                resetPassKey(sharedPreferences)
                startActivity(Intent(this@LoginActivity, SetupPassKeyActivity::class.java))
                finish()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@LoginActivity, "Биометрия не удалась", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Подтверждение личности")
            .setSubtitle("Используйте биометрию для сброса Pass Key")
            .setNegativeButtonText("Отмена")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun resetPassKey(sharedPreferences: android.content.SharedPreferences) {
        sharedPreferences.edit().remove("pass_key").apply()
    }
}