package com.example.bmicalculator

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bmicalculator.ui.theme.BMICalculatorTheme
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMICalculatorTheme {
                BMICalculatorApp()
            }
        }
    }
}

// Display of the BMI calculation. It sets a message according to the result of the BMI.
@Composable
fun BMIDisplay(bmi: Float) {
    var msg: String
    var color: Color
    if (bmi < 18.5) {
        msg = "underweight"
        color = Color.Yellow
    }
    else if (bmi in 18.5..24.9) {
        msg = "normal weight"
        color = Color.Green
    }
    else if (bmi in 25.0..29.9) {
        msg = "overweight"
        color = Color.Yellow
    }
    else {
        msg = "obesity"
        color = Color.Red
    }
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Your body-mass index",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$bmi",
            style = MaterialTheme.typography.headlineSmall
        )
        Row (
            modifier = Modifier.width(140.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier.size(25.dp).background(color = color, shape = CircleShape)
            )
            Text(text = msg, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// Warning message in case of a non-valid value inserted into the calculator.
@Composable
fun NonValidInput() {
    Row (
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Can't calculate. Not valid values.", style = MaterialTheme.typography.titleMedium)
    }
}

// App's user interface display. Contains all the elements can interact with. It uses two
// other composables to give feedback to the user.
@Composable
fun BMICalculatorScreen(
    weight: String,
    height: String,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    bmi: Float,
    validInput: Boolean,
    showMessage: Boolean,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BMI Calculator",
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            label = { Text(text="Weight (kilograms)") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = height,
            onValueChange = onHeightChange,
            label = { Text(text="Height (meters)") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            singleLine = true
        )
        Button(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            onClick = { onCalculate() }
        ) {
            Text("Calculate")
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp)
        ) {
            // Display of the calculation result or message error, depending on the 'onCalculate' function.
            if (showMessage) {
                if (validInput) {
                    BMIDisplay(bmi)
                } else {
                    NonValidInput()
                }
            }
        }
    }
}
// App's main component. It applies state hoisting with the input and the output values.
@Composable
fun BMICalculatorApp() {
    var weight by remember { mutableStateOf("") } // weight input
    var height by remember { mutableStateOf("") } // height input
    var bmi by remember { mutableFloatStateOf(0f) } // value obtained from the calculation
    var validInput by remember { mutableStateOf(true) } // display of the result or error message
    var showMessage by remember { mutableStateOf(false) } // State to hold the message until the first calculation is made.

    BMICalculatorScreen(
        weight = weight,
        height = height,
        onWeightChange = {weight = it},
        onHeightChange = {height = it},
        bmi = bmi,
        validInput = validInput,
        showMessage = showMessage,
        // Body-mass index calculation once the 'calculate' button is clicked.
        onCalculate = {
            if (weight.isNotBlank() && height.isNotBlank()) {
                showMessage = true
                var w: Float? = weight.toFloatOrNull()
                var h: Float? = height.toFloatOrNull()
                // Validation for appropriate data type inserted, as well as reasonable weight and height.
                if (w == null || h == null || w !in 1.0..300.0 || h !in 0.2..2.5) {
                    validInput = false
                } else {
                    bmi = round(w / (h * h))
                    validInput = true
                }
            } else {
                showMessage = false
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BMICalculatorPreview() {
    BMICalculatorTheme {
        BMICalculatorScreen("", "", {}, {}, 0f, true, false, {})
    }
}