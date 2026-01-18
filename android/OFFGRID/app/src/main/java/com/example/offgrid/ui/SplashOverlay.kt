package com.example.offgrid.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush // Required for Gradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.offgrid.R

@Composable
fun SplashOverlay(onFinished: () -> Unit) {

    var visible by remember { mutableStateOf(true) }

    // 1. ANIMATION CONTROLS
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.95f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        delay(1500)   // Hold for 1.5 seconds
        visible = false
        delay(1000)   // Wait for fade to finish
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ========================================================
        // LAYER 1: THE BACKGROUND (Dark Blue -> Black Gradient)
        // ========================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A), // Deep Cyberpunk Blue (Top)
                            Color(0xFF000000)  // Pure Black (Bottom)
                        )
                    )
                )
        )

        // ========================================================
        // LAYER 2: THE ANIMATED LOGO
        // ========================================================
        Box(
            modifier = Modifier
                .alpha(alpha)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_flatv2),
                contentDescription = "OFFGRID Logo",
                modifier = Modifier.size(150.dp)
            )
        }
    }
}