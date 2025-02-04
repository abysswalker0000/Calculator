package com.example.androidcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.androidcalculator.ui.theme.AndroidCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calculatorViewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]
        enableEdgeToEdge()
        Thread.sleep(1000)
        installSplashScreen()
        setContent {
            AndroidCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(modifier =  Modifier.padding(innerPadding),calculatorViewModel)
                }
            }
        }
    }
}
