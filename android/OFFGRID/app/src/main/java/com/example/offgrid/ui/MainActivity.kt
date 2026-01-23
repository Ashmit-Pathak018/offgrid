package com.example.offgrid.ui

import android.os.Bundle
import android.widget.Toast
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
// ✅ UPDATED IMPORTS: Use the new Architecture
import com.example.offgrid.logic.BackendFactory
import com.example.offgrid.logic.AIBackend
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

    // ✅ 1. NEW ENGINE: Replaced LlamaHelper with the Factory
    val aiBackend: AIBackend = remember { BackendFactory.create(context) }

    // 1. STATE: Controls the Splash Screen
    var showSplash by remember { mutableStateOf(true) }

    var screenState by remember { mutableStateOf(ScreenState.INPUT) }
    var inputText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    // ✅ 2. INIT: Use the new initialize() function
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val success = aiBackend.initialize()
            if (!success) {
                responseText = "Error: Model file missing.\nPlease put 'qwen.gguf' in Documents/OFFGRID/models/"
            }
        }
    }

    // ✅ 3. CLEANUP: Ensure threads die when app closes
    DisposableEffect(Unit) {
        onDispose {
            aiBackend.shutdown()
        }
    }

    fun runSolver(input: String, mode: String) {
        val keywords = listOf("code", "fun", "class", "error", "debug", "java", "python", "c", "stack", "list", "why", "how", "fix")
        val isTechnical = keywords.any { input.contains(it, ignoreCase = true) }

        if (!isTechnical) {
            responseText = "I am an offline coding tool. Please ask a technical question."
            screenState = ScreenState.RESPONSE
            return
        }

        // 🧠 THE UPGRADE: "Professor Mode" is preserved here
        // We pass this prompt string into the backend.
        val baseInstruction = """
            You are a strict Technical Interviewer and Computer Science Professor.
            Your goal is academic accuracy and best practices.
            
            RULES:
            1. If the user asks for a specific algorithm, use the strict textbook definition.
            2. If the user's code works but uses the wrong logic, you MUST correct it.
            3. Use clear variable names.
        """.trimIndent()

        val finalPrompt = when (mode) {
            "DEBUG" -> "$baseInstruction\n\nTASK: Find bugs or logical errors in this code:\n$input"
            "EXPLAIN" -> "$baseInstruction\n\nTASK: Explain this code step-by-step. Specify Time Complexity (Big O):\n$input"
            "ANALYZE" -> "$baseInstruction\n\nTASK: Analyze Time/Space complexity. Suggest optimizations:\n$input"
            else -> "$baseInstruction\n\nTASK: Solve this problem:\n$input"
        }

        screenState = ScreenState.PROCESSING
        responseText = ""

        scope.launch {
            isGenerating = true
            try {
                // ✅ 4. GENERATE: Call the new backend stream
                // We pass "RAW" as mode here because we manually built the prompt above
                aiBackend.generateResponse(finalPrompt, "RAW").collect { token ->
                    responseText += token
                    if (screenState != ScreenState.RESPONSE) screenState = ScreenState.RESPONSE
                }
            } catch (e: Exception) {
                responseText = "Error: ${e.message}"
            } finally {
                isGenerating = false
            }
        }
    }

    // 2. THE GATEKEEPER LOGIC
    if (showSplash) {
        SplashOverlay(onFinished = { showSplash = false })
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
                            Text("OFFGRID", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (screenState) {
                    ScreenState.INPUT -> InputScreen(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSolve = { runSolver(inputText, "SOLVE") },
                        onDebug = { runSolver(inputText, "DEBUG") },
                        onExplain = { runSolver(inputText, "EXPLAIN") },
                        onAnalyze = { runSolver(inputText, "ANALYZE") },
                        onScan = { Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show() }
                    )
                    ScreenState.PROCESSING -> ProcessingScreen(
                        onCancel = { screenState = ScreenState.INPUT },
                        onFakeComplete = {}
                    )
                    ScreenState.RESPONSE -> ResponseScreen(
                        response = responseText,
                        isGenerating = isGenerating,
                        onBack = { screenState = ScreenState.INPUT }
                    )
                }
            }
        }
    }
}