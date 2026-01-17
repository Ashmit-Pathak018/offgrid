package com.example.offgrid.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.offgrid.ui.theme.OFFGRIDTheme
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OFFGRIDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OffgridApp()
                }
            }
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OffgridApp() {
    var screenState by remember { mutableStateOf(ScreenState.INPUT) }
    var userInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OFFGRID") }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (screenState) {
                ScreenState.INPUT -> {
                    InputScreen(
                        text = userInput,
                        onTextChange = { userInput = it },
                        onSolve = { screenState = ScreenState.PROCESSING },
                        onDebug = { screenState = ScreenState.PROCESSING },
                        onExplain = { screenState = ScreenState.PROCESSING }
                    )
                }

                ScreenState.PROCESSING -> {
                    ProcessingScreen(
                        onCancel = { screenState = ScreenState.INPUT },
                        onFakeComplete = { screenState = ScreenState.RESPONSE }
                    )
                }

                ScreenState.RESPONSE -> {
                    ResponseScreen(
                        onBack = { screenState = ScreenState.INPUT }
                    )
                }
            }
        }
    }
}
