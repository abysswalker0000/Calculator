package com.example.androidcalculator

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.getValue

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
fun Calculator(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel,
    buttonColor: Color,
    backgroundColor: Color,
    equationColor: Color,
    resultColor: Color,
    historyColor: Color,
    onThemeClick: () -> Unit
) {
    val equationText by viewModel.equationText.observeAsState("")
    val resultText by viewModel.resultText.observeAsState("0")

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == 1

    if (isPortrait) {
        VerticalCalculator(
            equationText = equationText,
            resultText = resultText,
            viewModel = viewModel,
            buttonColor = buttonColor,
            backgroundColor = backgroundColor,
            equationColor = equationColor,
            resultColor = resultColor,
            historyColor = historyColor,
            onThemeClick = onThemeClick
        )
    } else {
        HorizontalCalculator(
            equationText = equationText,
            resultText = resultText,
            viewModel = viewModel,
            buttonColor = buttonColor,
            backgroundColor = backgroundColor,
            equationColor = equationColor,
            resultColor = resultColor,
            historyColor = historyColor,
            onThemeClick = onThemeClick
        )
    }
}

@Composable
fun VerticalCalculator(
    equationText: String,
    resultText: String,
    viewModel: CalculatorViewModel,
    buttonColor: Color,
    backgroundColor: Color,
    equationColor: Color,
    resultColor: Color,
    historyColor: Color,
    onThemeClick: () -> Unit
) {
    val history by viewModel.historyList.observeAsState(emptyList())
    val sortedHistory = history.sortedByDescending { it["timestamp"] as Long }
    val lastFourEntries = sortedHistory.take(4)

    Box(
        modifier = Modifier
            .background(color = backgroundColor)
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "История:",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = historyColor,
                        fontWeight = FontWeight.W600,
                        fontFamily = openSans
                    )
                )
                lastFourEntries.forEach { entry ->
                    Text(
                        text = "${entry["equation"]} = ${entry["result"]}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = historyColor,
                            fontFamily = openSans
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = equationText,
                modifier = Modifier.padding(start = 16.dp, top = 60.dp, bottom = 40.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    color = equationColor,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W900,
                    fontFamily = openSans
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = resultText,
                modifier = Modifier.padding(start = 16.dp),
                style = TextStyle(
                    fontSize = 25.sp,
                    textAlign = TextAlign.Start,
                    color = resultColor,
                    fontWeight = FontWeight.W900,
                    fontFamily = openSans
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
                            CalculatorButton(
                                btn = button,
                                onClick = { viewModel.onButtonClick(button) },
                                buttonColor = buttonColor,
                                size = 64
                            )
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = onThemeClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Тема")
        }
    }
}

@Composable
fun HorizontalCalculator(
    equationText: String,
    resultText: String,
    viewModel: CalculatorViewModel,
    buttonColor: Color,
    backgroundColor: Color,
    equationColor: Color,
    resultColor: Color,
    historyColor: Color,
    onThemeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(color = backgroundColor)
            .fillMaxSize()
            .padding(top = 5.dp)
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
                    text = equationText,
                    modifier = Modifier.padding(top = 30.dp, bottom = 10.dp, start = 28.dp),
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = equationColor,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.W900,
                        fontFamily = openSans
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resultText,
                    modifier = Modifier.padding(top = 10.dp, start = 28.dp),
                    style = TextStyle(
                        fontSize = 40.sp,
                        textAlign = TextAlign.Start,
                        color = resultColor,
                        fontWeight = FontWeight.W900,
                        fontFamily = openSans
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
                                onClick = { viewModel.onButtonClick(button) },
                                buttonColor = buttonColor,
                                size = 60
                            )
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = onThemeClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Тема")
        }
    }
}

@Composable
fun CalculatorButton(
    btn: String,
    onClick: () -> Unit,
    buttonColor: Color,
    size: Int = 60
) {
    val context = LocalContext.current
    val elevation = if (btn in listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "=", "C")) 0.dp else 6.dp

    Box(modifier = Modifier.padding(3.dp)) {
        FloatingActionButton(
            onClick = {
                onClick()
                vibrate(context)
            },
            modifier = Modifier.size(size.dp),
            contentColor = getContentColor(btn),
            containerColor = getButtonColor(btn, buttonColor),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(elevation)
        ) {
            Text(text = btn, fontSize = 18.sp, fontWeight = FontWeight.W400, fontFamily = openSans)
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

fun getButtonColor(btn: String, baseColor: Color): Color {
    return when {
        btn in listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0") -> baseColor.copy(alpha = 0.7f)
        btn == "=" || btn == "C" -> baseColor.copy(alpha = 0.5f)
        else -> baseColor
    }
}

fun getContentColor(btn: String): Color {
    return if (btn == "=" || btn == "C") Color(0xFFCBD0CA) else Color(0xFFDDE0DD)
}