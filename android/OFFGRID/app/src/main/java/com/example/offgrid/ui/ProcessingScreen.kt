package com.example.offgrid.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.offgrid.R
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    onCancel: () -> Unit,
    onFakeComplete: () -> Unit
) {

    val CyanNeon = Color(0xFF00E5FF)
    val DarkBg = Color(0xFF0F172A)
    val TrackBg = Color(0xFF1E293B)

    // Pulse animation for logo
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    var progress by remember { mutableStateOf(0.0f) }
    var statusText by remember { mutableStateOf("INITIALIZING UPLINK...") }

    LaunchedEffect(Unit) {
        val steps = listOf(
            0.2f to "PARSING INPUT STREAM...",
            0.45f to "ANALYZING SYNTAX NODES...",
            0.7f to "OPTIMIZING LOGIC GATES...",
            0.9f to "GENERATING OUTPUT...",
            1.0f to "COMPLETE."
        )

        steps.forEach { (prog, text) ->
            delay(600)
            progress = prog
            statusText = text
        }

        delay(500)
        onFakeComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // LOGO (pulsing)
        Image(
            painter = painterResource(id = R.drawable.logo_flatv2),
            contentDescription = "Processing",
            modifier = Modifier
                .size(96.dp)
                .scale(scale)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "> $statusText",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .border(1.dp, CyanNeon, RoundedCornerShape(4.dp)),
            color = CyanNeon,
            trackColor = TrackBg,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onCancel,
            border = null
        ) {
            Text(
                "CANCEL PROCESS",
                color = Color.Gray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
