/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.basics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import com.codelab.basics.ui.theme.BasicsCodelabTheme
import java.text.DecimalFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

val DarkGray = Color(0xFF2E2E2E)
val MediumGray = Color(0xFFA5A5A5)
val LightGray = Color(0xFFD4D4D2)
val Orange = Color(0xFFFF9500)
val White = Color.White
val Black = Color.Black

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicsCodelabTheme {
//                MyApp()
                CalculatorApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

val factorial = object : Function("fact", 1) {
    override fun apply(vararg args: Double): Double {
        val n = args[0].toInt()
        if (n < 0 || n != args[0].toInt()) {
            throw IllegalArgumentException("Argument for factorial must be a non-negative integer")
        }
        var result = 1.0
        for (i in 2..n) {
            result *= i
        }
        return result
    }
}

@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    var display by rememberSaveable { mutableStateOf("0") }
    var expression by rememberSaveable { mutableStateOf("") }
    var isInverse by rememberSaveable { mutableStateOf(false) }

    fun evaluateExpression(exp: String): String {
        return try {
            val sanitizedExp = exp
                .replace("×", "*")
                .replace("÷", "/")
                .replace("√", "sqrt")
                .replace("π", Math.PI.toString())

            val expressionBuilder = ExpressionBuilder(sanitizedExp)
                .function(factorial)
                .build()

            val result = expressionBuilder.evaluate()

            val df = DecimalFormat("#.#######")
            df.format(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    val onButtonClick: (String) -> Unit = { buttonText ->
        var currentText = buttonText
        if (isInverse) {
            currentText = when (buttonText) {
                "sin" -> "asin"
                "cos" -> "acos"
                "tan" -> "atan"
                else -> buttonText
            }
        }

        when (currentText) {
            "AC" -> {
                expression = ""
                display = "0"
            }
            "⌫" -> { // Backspace
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    display = if (expression.isEmpty()) "0" else expression
                }
            }
            "=" -> {
                if (expression.isNotEmpty()) {
                    val result = evaluateExpression(expression)
                    display = result
                    expression = if (result != "Error") result else ""
                }
            }
            "inv" -> {
                isInverse = !isInverse
            }
            "sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "√" -> {
                if (expression == "0" || expression == "Error") {
                    expression = "$currentText("
                } else {
                    expression += "$currentText("
                }
                display = expression
            }
            "x!" -> {
                expression += "fact("
                display = expression
            }
            "1/x" -> {
                if (expression == "0" || expression == "Error") {
                    expression = "1/"
                } else {
                    expression = "1/($expression)"
                }
                display = expression
            }
            "xʸ" -> {
                expression += "^("
                display = expression
            }
            else -> {
                if (display == "0" || expression == "Error") {
                    expression = currentText
                } else {
                    expression += currentText
                }
                display = expression
            }
        }
    }

    Box(
        modifier = modifier
            .background(Black)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = display,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                fontSize = if (display.length > 9) 48.sp else 72.sp,
                fontWeight = FontWeight.Light,
                color = White,
                textAlign = TextAlign.End,
                maxLines = 2
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val buttonRows = listOf(
                    listOf("inv", "sin", "ln", "cos", "log", "tan"),
                    listOf("√", "xʸ", "x!", "(", ")", "π"),
                    listOf("AC", "⌫", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttonRows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { buttonText ->
                            val weight = if (buttonText == "0") 2.1f else 1f
                            val modifier = Modifier.weight(weight).aspectRatio(if(buttonText == "0") 2f else 1f)

                            val label = when {
                                buttonText == "inv" && isInverse -> "inv"
                                buttonText == "sin" -> if (isInverse) "sin⁻¹" else "sin"
                                buttonText == "cos" -> if (isInverse) "cos⁻¹" else "cos"
                                buttonText == "tan" -> if (isInverse) "tan⁻¹" else "tan"
                                else -> buttonText
                            }

                            CalculatorButton(
                                text = label,
                                modifier = modifier,
                                onClick = { onButtonClick(buttonText) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (backgroundColor, textColor) = when (text) {
        "AC", "⌫", "%" -> MediumGray to Black
        "÷", "×", "-", "+", "=" -> Orange to White
        "inv" -> if (text == "inv") Orange to White else MediumGray to Black
        else -> DarkGray to White
    }

    ElevatedButton(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = CircleShape,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, fontSize = 28.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalculatorAppPreview() {
    BasicsCodelabTheme {
        CalculatorApp(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            Greetings()
        }
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun Greetings(modifier: Modifier = Modifier, names: List<String> = List(1000) { "$it" }) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting(name = name)
        }
    }
}

@Composable
private fun Greeting(name: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        CardContent(name)
    }
}

@Composable
private fun CardContent(name: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = "Hello, ")
            Text(
                text = name, style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) {
                Text(
                    text = ("Composem ipsum color sit lazy, " +
                            "padding theme elit, sed do bouncy. ").repeat(4),
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Filled.ExpandLess else Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    BasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "GreetingPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    BasicsCodelabTheme {
        Greetings()
    }
}

@Preview
@Composable
fun MyAppPreview() {
    BasicsCodelabTheme {
        MyApp(Modifier.fillMaxSize())
    }
}