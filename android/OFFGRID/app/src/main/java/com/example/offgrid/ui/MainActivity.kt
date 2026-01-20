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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.offgrid.R
import com.example.offgrid.logic.LlamaHelper // Import the Brain
import com.example.offgrid.ui.theme.OFFGRIDTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            OFFGRIDTheme {
                OFFGRIDApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OFFGRIDApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Initialize the Brain (Pass 'context' here!)
    val llamaHelper = remember { LlamaHelper(context) }

    // 2. State Management
    var screenState by remember { mutableStateOf(ScreenState.INPUT) }
    var inputText by remember { mutableStateOf("") }
    var responseContent by remember { mutableStateOf("") } // Stores the live answer
    var showSplash by remember { mutableStateOf(true) }

    // 3. Load Model in Background on App Start
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val success = llamaHelper.initModel() // Looks for /data/local/tmp/model.bin
            if (!success) {
                // If it fails, we prepopulate the response so the user knows immediately
                responseContent = "Error: Model file missing.\nDid you run the ADB Push command?"
            }
        }
    }

    // 4. The "Bouncer" Logic (Blocks non-coding questions)
    fun isTechnical(input: String): Boolean {
        // Simple keywords for now
        val keywords = listOf("code", "fun", "class", "error", "debug", "java", "python", "c", "stack", "list", "why", "how", "fix")
        return keywords.any { input.contains(it, ignoreCase = true) }
    }

    // 5. The Solver Logic
    fun runSolver(prompt: String) {
        if (!isTechnical(prompt)) {
            responseContent = "I am an offline coding tool. Please ask a technical question."
            screenState = ScreenState.RESPONSE
            return
        }

        screenState = ScreenState.PROCESSING
        responseContent = "" // Clear old text

        scope.launch {
            // Start the stream
            llamaHelper.solveProblem(prompt).collect { token ->
                responseContent += token

                // As soon as first text arrives, show the Response Screen
                if (screenState != ScreenState.RESPONSE) {
                    screenState = ScreenState.RESPONSE
                }
            }
        }
    }

    if (showSplash) {
        SplashOverlay { showSplash = false }
    } else {
        Scaffold(
            containerColor = Color(0xFF0F172A),
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_flatv2),
                                contentDescription = "OFFGRID",
                                modifier = Modifier.size(26.dp)
                            )
                            Text(
                                text = "OFFGRID",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp,
                                color = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0F172A),
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (screenState) {
                    ScreenState.INPUT -> InputScreen(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSolve = { runSolver(inputText) },
                        onDebug = { runSolver("Find the bug in this code: $inputText") },
                        onExplain = { runSolver("Explain this code logic: $inputText") }
                    )

                    ScreenState.PROCESSING -> ProcessingScreen(
                        onCancel = { screenState = ScreenState.INPUT },
                        onFakeComplete = { /* No-op: The stream handles the transition now */ }
                    )

                    ScreenState.RESPONSE -> ResponseScreen(
                        response = responseContent, // Shows live text
                        onBack = { screenState = ScreenState.INPUT }
                    )
                }
            }
        }
    }
}