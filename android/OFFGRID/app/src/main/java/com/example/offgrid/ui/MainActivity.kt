package com.example.offgrid.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // <--- Added missing import
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily // <--- Added missing import
import androidx.compose.ui.text.font.FontWeight // <--- Added missing import
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // <--- Added missing import
import com.example.offgrid.R
import com.example.offgrid.ui.theme.OFFGRIDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // 1. Handle System Splash
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
    var screenState by remember { mutableStateOf(ScreenState.INPUT) }
    var inputText by remember { mutableStateOf("") }

    // 2. Control Custom Splash
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashOverlay {
            showSplash = false
        }
    } else {
        Scaffold(
            containerColor = Color(0xFF0F172A), // Matches App Background
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
                                color = Color.White // Explicit text color
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0F172A), // Seamless blend
                        scrolledContainerColor = Color(0xFF0F172A),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (screenState) {
                    ScreenState.INPUT -> InputScreen(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSolve = { screenState = ScreenState.PROCESSING },
                        onDebug = { screenState = ScreenState.PROCESSING },
                        onExplain = { screenState = ScreenState.PROCESSING }
                    )

                    ScreenState.PROCESSING -> ProcessingScreen(
                        onCancel = { screenState = ScreenState.INPUT },
                        onFakeComplete = { screenState = ScreenState.RESPONSE }
                    )

                    ScreenState.RESPONSE -> ResponseScreen(
                        response = """
        # Analysis Complete
        Found optimization opportunity in loop structure.
        
        ### Issue Detected
        The time complexity is currently **O(n^2)**. We can reduce this to **O(n)** using a Hash Map.
        
        ### Suggested Fix
        Here is the optimized code:
        
        ```kotlin
        fun findPairs(nums: IntArray, target: Int): List<Pair<Int, Int>> {
            val map = HashMap<Int, Int>()
            val result = mutableListOf<Pair<Int, Int>>()
            
            for (num in nums) {
                val complement = target - num
                if (map.containsKey(complement)) {
                    result.add(Pair(complement, num))
                }
                map[num] = 1
            }
            return result
        }
        ```
        
        > Note: This approach uses more memory but is significantly faster for large datasets.
    """.trimIndent(),
                        onBack = { screenState = ScreenState.INPUT }
                    )
                }
            }
        }
    }
}