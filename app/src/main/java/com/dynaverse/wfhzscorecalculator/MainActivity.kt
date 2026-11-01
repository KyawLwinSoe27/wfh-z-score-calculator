package com.dynaverse.wfhzscorecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dynaverse.wfhzscorecalculator.ui.WfhHomeScreen
import com.dynaverse.wfhzscorecalculator.ui.theme.WFHZScoreCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WFHZScoreCalculatorTheme {
                WfhHomeScreen()
            }
        }
    }
}
