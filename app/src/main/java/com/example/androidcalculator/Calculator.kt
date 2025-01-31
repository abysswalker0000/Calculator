package com.example.androidcalculator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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



val buttonList = listOf(
    "C","del","(",")",
    "1","2","3","+",
    "4","5","6","-",
    "7","8","9","*",
    ".","0","=","/"

)
@Composable
fun Calculator(modifier: Modifier = Modifier)
{
    Box(modifier = modifier) {
        Column(modifier=modifier.fillMaxSize(),
            horizontalAlignment =Alignment.Start)
            {
            Text(text="12341+1312",
                style = TextStyle(
                fontSize = 30.sp,
                textAlign = TextAlign.Start,
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier=Modifier.weight(1f))

                Text(text="246",
                    style = TextStyle(
                        fontSize = 50.sp,
                        textAlign = TextAlign.Start
                    ),
                    maxLines = 2,)

                Spacer(modifier=Modifier.height(10.dp))


                LazyVerticalGrid(columns = GridCells.Fixed(4))
                {
                    items(buttonList){
                        CalculatorButton(btn=it)
                    }
            }
        }
    }
}

@Composable
fun CalculatorButton(btn : String) {
    Box(modifier = Modifier.padding(10.dp)){
        FloatingActionButton(onClick = {},
            modifier = Modifier.size(60.dp),
            contentColor = Color.Yellow,
            containerColor= getButtonColor(btn),
        )
        {
            Text(text=btn, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun getButtonColor(btn : String) : Color{
    if (btn == "1" || btn == "2" || btn == "3" || btn == "4" ||btn == "5" ||btn == "6" ||btn == "7" ||btn == "8" ||btn == "9" ||btn == "0")
        return Color(0xFF595959)
    else return Color.DarkGray
}