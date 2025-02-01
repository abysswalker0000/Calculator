package com.example.androidcalculator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextIndent

val openSans = FontFamily(
    Font(R.font.opsmed, FontWeight.W400),
    Font(R.font.opsbld, FontWeight.W900),
)



val buttonList = listOf(
    "C","del","(",")","sin",
    "1","2","3","+","cos",
    "4","5","6","-","tg",
    "7","8","9","*","ctg",
    ".","0","=","/","√"

)
@Composable
fun Calculator(modifier: Modifier = Modifier, viewModel: CalculatorViewModel) {
    val equationText = viewModel.eqationText.observeAsState()
    val resultText = viewModel.resultText.observeAsState()

    Box(
        modifier = modifier
            .background(color = Color(0xFF262626))
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = equationText.value ?: "",
                modifier = Modifier.padding(start = 16.dp, top = 30.dp, bottom = 200.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color(0xFFD0BC2C),
                    textAlign = TextAlign.Start,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W900,
                fontFamily = openSans,
            )
            Text(
                text = resultText.value ?: "0",
                modifier = Modifier.padding(start = 16.dp),
                style = TextStyle(
                    fontSize = 25.sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFFD0BC2C),
                    fontWeight = FontWeight.W900,
                    fontFamily = openSans,
                ),
                maxLines = 2,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
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
fun CalculatorButton(btn : String, onClick : () -> Unit) {
    Box(modifier = Modifier.padding(3.dp)){
        FloatingActionButton(onClick = onClick,
            modifier = Modifier.size(65.dp),
            contentColor = getContentColor(btn),
            containerColor= getButtonColor(btn),
            shape = CircleShape
        )
        {
            Text(text=btn, fontSize = 20.sp, fontWeight = FontWeight.W400, fontFamily = openSans)
        }
    }
}

fun getButtonColor(btn : String) : Color{
    if (btn == "1" || btn == "2" || btn == "3" || btn == "4" ||btn == "5" ||btn == "6" ||btn == "7" ||btn == "8" ||btn == "9" ||btn == "0")
        return Color(0xFF13120F)
    if( btn=="=" || btn =="C")
        return Color(0xFFD0BC2C)
    else return Color(0xFF00000F)
}
fun getContentColor(btn : String) : Color{
    if (btn=="=" || btn=="C")
        return Color(0xFF00000F)
    else return Color(0xFFD0BC2C)
}