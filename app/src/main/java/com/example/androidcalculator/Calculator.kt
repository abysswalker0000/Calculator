package com.example.androidcalculator

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

val openSans = FontFamily(
    Font(R.font.opsmed, FontWeight.W400),
    Font(R.font.opsbld, FontWeight.W900),
)

val buttonList = listOf(
    "C", "del", "(", ")", "sin",
    "1", "2", "3", "+", "cos",
    "4", "5", "6", "-", "tg",
    "7", "8", "9", "*", "ctg",
    ".", "0", "=", "/", "√"
)

@Composable
fun Calculator(modifier: Modifier = Modifier, viewModel: CalculatorViewModel) {
    val equationText = viewModel.eqationText.observeAsState()
    val resultText = viewModel.resultText.observeAsState()

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == 1

    if (isPortrait) {
        VerticalCalculator(equationText = equationText.value, resultText = resultText.value, viewModel = viewModel)
    } else {
        HorizontalCalculator(equationText = equationText.value, resultText = resultText.value, viewModel = viewModel)
    }
}

@Composable
fun VerticalCalculator(equationText: String?, resultText: String?, viewModel: CalculatorViewModel) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFE8EDFC))
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = equationText ?: "",
                modifier = Modifier.padding(start = 16.dp, top = 60.dp, bottom = 40.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color(0xFF036280),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W900,
                    fontFamily = openSans,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = resultText ?: "0",
                modifier = Modifier.padding(start = 16.dp),
                style = TextStyle(
                    fontSize = 25.sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF036280),
                    fontWeight = FontWeight.W900,
                    fontFamily = openSans,
                ),
                maxLines = 2
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                buttonList.chunked(5).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        row.forEach { button ->
                            CalculatorButton(btn = button, onClick = {
                                viewModel.onButtonClick(button)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalCalculator(equationText: String?, resultText: String?, viewModel: CalculatorViewModel) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFE8EDFC))
            .fillMaxSize()
            .padding(bottom = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = equationText ?: "",
                    modifier = Modifier.padding(top = 30.dp, bottom = 10.dp),
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color(0xFF036280),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.W900,
                        fontFamily = openSans,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resultText ?: "0",
                    modifier = Modifier.padding(bottom = 10.dp),
                    style = TextStyle(
                        fontSize = 40.sp,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF036280),
                        fontWeight = FontWeight.W900,
                        fontFamily = openSans,
                    ),
                    maxLines = 2
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                buttonList.chunked(5).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        row.forEach { button ->
                            CalculatorButton(
                                btn = button,
                                onClick = {
                                    viewModel.onButtonClick(button)
                                },
                                size = 58
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(btn: String, onClick: () -> Unit, size: Int = 65) {
    val context = LocalContext.current // Получаем контекст с помощью LocalContext

    Box(modifier = Modifier.padding(3.dp)) {
        FloatingActionButton(
            onClick = {
                onClick()
                vibrate(context) // Добавление вибрации
            },
            modifier = Modifier.size(size.dp),
            contentColor = getContentColor(btn),
            containerColor = getButtonColor(btn),
            shape = CircleShape
        ) {
            Text(text = btn, fontSize = 20.sp, fontWeight = FontWeight.W400, fontFamily = openSans)
        }
    }
}


fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(50)
    }
}

fun getButtonColor(btn: String): Color {
    return when {
        btn in listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0") -> Color(0xFF81BECE)
        btn == "=" || btn == "C" -> Color(0xFF012E4A)
        else -> Color(0xFF378BA4)
    }
}

fun getContentColor(btn: String): Color {
    return if (btn == "=" || btn == "C") Color(0xFFCBD0CA) else Color(0xFFDDE0DD)
}
