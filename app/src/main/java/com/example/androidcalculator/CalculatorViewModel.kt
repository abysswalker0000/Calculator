package com.example.androidcalculator

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class CalculatorViewModel : ViewModel() {

    private val _equationText = MutableLiveData("")
    val eqationText : LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText : LiveData<String> = _resultText

    private val operators = listOf("+", "-", "*", "/", "√", "sin", "cos", "tg", "ctg")

    fun onButtonClick(btn: String) {
        _equationText.value?.let {
            if (btn == "C") {
                _equationText.value = ""
                _resultText.value = "0"
                return
            }

            if (btn == "del") {
                if (it.isNotEmpty()) {
                    _equationText.value = it.substring(0, it.length - 1)
                }
                return
            }

            if (btn == "=") {
                _equationText.value = _resultText.value
                return
            }

            if (operators.contains(btn)) {
                if (it.isNotEmpty() && operators.contains(it.last().toString())) {
                    _equationText.value = it.dropLast(1) + btn
                    return
                }
                if (btn in listOf("sin", "cos", "tg", "ctg", "√")) {
                    _equationText.value = it + "$btn("
                    return
                }
            }

            _equationText.value = it + btn
            try {
                _resultText.value = calculateResult(_equationText.value.toString())
            } catch (_: Exception) {}
        }
    }

    fun calculateResult(equation: String): String {
        var updatedEquation = equation

        updatedEquation = updatedEquation.replace(Regex("([0-9)])(\\(|sin|cos|tg|ctg|√)"), "$1*$2")
        updatedEquation = updatedEquation.replace("√", "Math.sqrt")
        updatedEquation = updatedEquation.replace("sin", "Math.sin")
        updatedEquation = updatedEquation.replace("cos", "Math.cos")
        updatedEquation = updatedEquation.replace("ctg", "1/Math.tan")
        updatedEquation = updatedEquation.replace("tg", "Math.tan")

        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scriptable: Scriptable = context.initStandardObjects()

        var finalResult = context.evaluateString(scriptable, updatedEquation, "Javascript", 1, null).toString()
        if (finalResult.endsWith("0")) {
            finalResult = finalResult.replace(".0", "")
        }
        return finalResult
    }
}
