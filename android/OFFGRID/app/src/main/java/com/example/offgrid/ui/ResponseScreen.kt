package com.example.offgrid.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResponseScreen(
    response: String = "System idle. Waiting for input...",
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Theme Colors (matching your app)
    val CyanNeon = Color(0xFF00E5FF)
    val DarkBackground = Color(0xFF0F172A)
    val TerminalBg = Color(0xFF1E293B)
    val TextPrimary = Color(0xFFE2E8F0)
    val TextSecondary = Color(0xFF94A3B8)

    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {

        // HEADER
        Text(
            text = "HENRY // OUTPUT_STREAM",
            color = CyanNeon,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // TERMINAL BOX
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(TerminalBg, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {

                // COPY BUTTON (top right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* TODO: real copy logic later */ }) {
                        Text(
                            text = "COPY_OUTPUT",
                            color = CyanNeon,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // MAIN OUTPUT TEXT + CURSOR
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = response,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "█",
                        color = TextPrimary.copy(alpha = cursorAlpha),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SUBTLE DIVIDER
        Divider(
            color = CyanNeon.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ACKNOWLEDGE BUTTON
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "ACKNOWLEDGE_AND_RETURN",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp
            )
        }
    }
}
