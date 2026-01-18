package com.example.offgrid.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.offgrid.R
import com.example.offgrid.ui.theme.OFFGRIDTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OFFGRIDTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp)
                                ) {

                                    Image(
                                        painter = painterResource(id = R.drawable.logo_version2),
                                        contentDescription = "OFFGRID Logo",
                                        modifier = Modifier.size(30.dp)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = "OFFGRID",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        OffgridApp()
                    }
                }
            }
        }
    }
}

@Composable
fun OffgridApp() {

    var screenState by remember { mutableStateOf(ScreenState.INPUT) }
    var inputText by remember { mutableStateOf("") }

    when (screenState) {

        ScreenState.INPUT -> {
            InputScreen(
                text = inputText,
                onTextChange = { inputText = it },
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
