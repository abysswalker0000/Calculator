package com.example.androidcalculator

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }
        enableEdgeToEdge()
        if (resources.configuration.orientation == 2) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }

        val sharedPreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val isAuthenticated = sharedPreferences.getBoolean("is_authenticated", false)

        // Проверка аутентификации
        if (!isAuthenticated) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val soundManager = SoundManager(this)
        val calculatorViewModel = ViewModelProvider(this, CalculatorViewModelFactory(soundManager))
            .get(CalculatorViewModel::class.java)

        calculatorViewModel.startListeningToHistory()
        calculatorViewModel.fetchThemeSettings()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM Token", task.result)
            } else {
                Log.w("FCM Token", "Ошибка получения FCM-токена", task.exception)
            }
        }

        setContent {
            val themeSettings by calculatorViewModel.themeSettings.observeAsState(ThemeSettings())
            val buttonColor = Color(android.graphics.Color.parseColor(themeSettings.buttonColor))
            val backgroundColor = Color(android.graphics.Color.parseColor(themeSettings.backgroundColor))
            val equationColor = Color(android.graphics.Color.parseColor(themeSettings.equationColor))
            val resultColor = Color(android.graphics.Color.parseColor(themeSettings.resultColor))
            val historyColor = Color(android.graphics.Color.parseColor(themeSettings.historyColor))
            var showThemeDialog by remember { mutableStateOf(false) }

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Calculator(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = calculatorViewModel,
                    buttonColor = buttonColor,
                    backgroundColor = backgroundColor,
                    equationColor = equationColor,
                    resultColor = resultColor,
                    historyColor = historyColor,
                    onThemeClick = { showThemeDialog = true }
                )

                if (showThemeDialog) {
                    ThemeCustomizationDialog(
                        currentButtonColor = themeSettings.buttonColor,
                        currentBackgroundColor = themeSettings.backgroundColor,
                        currentEquationColor = themeSettings.equationColor,
                        currentResultColor = themeSettings.resultColor,
                        currentHistoryColor = themeSettings.historyColor,
                        onDismiss = { showThemeDialog = false },
                        onSave = { buttonCol, backCol, eqCol, resCol, histCol ->
                            calculatorViewModel.db.collection("settings").document("theme")
                                .set(
                                    mapOf(
                                        "buttonColor" to buttonCol,
                                        "backgroundColor" to backCol,
                                        "equationColor" to eqCol,
                                        "resultColor" to resCol,
                                        "historyColor" to histCol
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d("Calculator", "Цвета обновлены")
                                    calculatorViewModel.fetchThemeSettings()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Calculator", "Ошибка обновления цветов", e)
                                }
                            showThemeDialog = false
                        },
                        onReset = {
                            calculatorViewModel.db.collection("settings").document("theme")
                                .set(
                                    mapOf(
                                        "buttonColor" to "#036280",
                                        "backgroundColor" to "#E8EDFC",
                                        "equationColor" to "#036280",
                                        "resultColor" to "#036280",
                                        "historyColor" to "#444444"
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d("Calculator", "Тема сброшена")
                                    calculatorViewModel.fetchThemeSettings()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Calculator", "Ошибка сброса темы", e)
                                }
                            showThemeDialog = false
                        }
                    )
                }
            }
            LaunchedEffect(themeSettings) {
                window.statusBarColor = android.graphics.Color.parseColor(themeSettings.buttonColor)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferences.edit().putBoolean("is_authenticated", false).apply()
    }

    // Добавляем ThemeCustomizationDialog
    @Composable
    fun ThemeCustomizationDialog(
        currentButtonColor: String,
        currentBackgroundColor: String,
        currentEquationColor: String,
        currentResultColor: String,
        currentHistoryColor: String,
        onDismiss: () -> Unit,
        onSave: (String, String, String, String, String) -> Unit,
        onReset: () -> Unit
    ) {
        var buttonColorText by remember { mutableStateOf(currentButtonColor) }
        var backgroundColorText by remember { mutableStateOf(currentBackgroundColor) }
        var equationColorText by remember { mutableStateOf(currentEquationColor) }
        var resultColorText by remember { mutableStateOf(currentResultColor) }
        var historyColorText by remember { mutableStateOf(currentHistoryColor) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Настройка цветов") },
            text = {
                Column {
                    Text("Цвет кнопок (HEX код, например #036280):")
                    TextField(value = buttonColorText, onValueChange = { buttonColorText = it })
                    Text("Цвет фона (HEX код, например #E8EDFC):")
                    TextField(value = backgroundColorText, onValueChange = { backgroundColorText = it })
                    Text("Цвет уравнения (HEX код):")
                    TextField(value = equationColorText, onValueChange = { equationColorText = it })
                    Text("Цвет результата (HEX код):")
                    TextField(value = resultColorText, onValueChange = { resultColorText = it })
                    Text("Цвет истории (HEX код):")
                    TextField(value = historyColorText, onValueChange = { historyColorText = it })
                    errorMessage?.let { Text(it, color = Color.Red) }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (isValidHexColor(buttonColorText) && isValidHexColor(backgroundColorText) &&
                        isValidHexColor(equationColorText) && isValidHexColor(resultColorText) &&
                        isValidHexColor(historyColorText)
                    ) {
                        onSave(buttonColorText, backgroundColorText, equationColorText, resultColorText, historyColorText)
                    } else {
                        errorMessage = "Некорректный HEX-код. Используйте формат #RRGGBB."
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(onClick = onReset) {
                    Text("Сбросить на изначальную")
                }
            }
        )
    }

    private fun isValidHexColor(color: String): Boolean {
        return color.matches(Regex("^#[0-9A-Fa-f]{6}$"))
    }
}