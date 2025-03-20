package com.example.androidcalculator

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

data class ThemeSettings(
    val buttonColor: String = "#036280",
    val backgroundColor: String = "#E8EDFC",
    val equationColor: String = "#036280",
    val resultColor: String = "#036280",
    val historyColor: String = "#444444"
)

class CalculatorViewModel(private val soundManager: SoundManager) : ViewModel() {
    val db = FirebaseFirestore.getInstance()

    private val _equationText = MutableLiveData("")
    val equationText: LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText: LiveData<String> = _resultText

    private val _historyList = MutableLiveData<List<Map<String, Any>>>()
    val historyList: LiveData<List<Map<String, Any>>> = _historyList

    private val _themeSettings = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> = _themeSettings

    private val operators = listOf("+", "-", "*", "/", "√", "sin", "cos", "tg", "ctg")

    fun onButtonClick(btn: String) {
        _equationText.value?.let { currentEquation ->
            if (btn == "C") {
                _equationText.value = ""
                _resultText.value = "0"
                return
            }
            if (btn == "del") {
                if (currentEquation.isNotEmpty()) {
                    _equationText.value = currentEquation.dropLast(1)
                }
                return
            }
            if (btn == "=") {
                try {
                    val originalEquation = currentEquation
                    val result = calculateResult(originalEquation)
                    _resultText.value = result
                    saveToFirestore(originalEquation, result)
                    _equationText.value = result
                } catch (e: Exception) {
                    Log.e("Calculator", "Error calculating result", e)
                    _resultText.value = "Error"
                    soundManager.playButtonSound()
                }
                return
            }

            if (operators.contains(btn)) {
                if (btn in listOf("sin", "cos", "tg", "ctg", "√")) {
                    if (currentEquation.isEmpty() || !operators.contains(currentEquation.last().toString())) {
                        _equationText.value = currentEquation + "$btn("
                    } else {
                        _equationText.value = currentEquation + "$btn("
                    }
                    return
                } else {
                    if (currentEquation.isNotEmpty() && operators.contains(currentEquation.last().toString())) {
                        _equationText.value = currentEquation.dropLast(1) + btn
                    } else {
                        _equationText.value = currentEquation + btn
                    }
                    return
                }
            }

            _equationText.value = currentEquation + btn
            try {
                _resultText.value = calculateResult(_equationText.value.toString())
            } catch (e: Exception) {
                Log.e("Calculator", "Error calculating intermediate result", e)
            }
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
        if (finalResult.endsWith(".0")) {
            finalResult = finalResult.replace(".0", "")
        }
        return finalResult
    }

    private fun saveToFirestore(equation: String, result: String) {
        val historyEntry = hashMapOf(
            "equation" to equation,
            "result" to result,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("history")
            .add(historyEntry)
            .addOnSuccessListener { documentReference ->
                Log.d("Calculator", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Calculator", "Error adding document", e)
            }
    }

    fun startListeningToHistory() {
        db.collection("history")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Calculator", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val history = snapshot.documents.map { it.data ?: emptyMap() }
                    _historyList.value = history
                } else {
                    Log.d("Calculator", "Current data: null")
                }
            }
    }

    fun fetchThemeSettings() {
        db.collection("settings").document("theme")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val buttonColor = document.getString("buttonColor") ?: "#036280"
                    val backgroundColor = document.getString("backgroundColor") ?: "#E8EDFC"
                    val equationColor = document.getString("equationColor") ?: "#036280"
                    val resultColor = document.getString("resultColor") ?: "#036280"
                    val historyColor = document.getString("historyColor") ?: "#444444"
                    _themeSettings.value = ThemeSettings(buttonColor, backgroundColor, equationColor, resultColor, historyColor)
                } else {
                    _themeSettings.value = ThemeSettings()
                }
            }
            .addOnFailureListener { e ->
                Log.w("Calculator", "Ошибка получения настроек цвета", e)
                _themeSettings.value = ThemeSettings()
            }
    }
}