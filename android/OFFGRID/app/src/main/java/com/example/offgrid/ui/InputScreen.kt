package com.example.offgrid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputScreen(
    text: String,
    onTextChange: (String) -> Unit,
    onSolve: () -> Unit,
    onDebug: () -> Unit,
    onExplain: () -> Unit,
    onAnalyze: () -> Unit,
    onScan: () -> Unit // Keeps connection to Main
) {
    val CyanNeon = Color(0xFF00E5FF)
    val DarkBg = Color(0xFF0F172A)
    val InputBg = Color(0xFF1E293B)
    val TextColor = Color(0xFFE2E8F0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // 1. Status Header (Restored)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYSTEM: ONLINE",
                color = CyanNeon,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            // The Cool Badge
            Surface(
                color = CyanNeon.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon)
            ) {
                Text(
                    text = "TEXT / CODE",
                    color = CyanNeon,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. The "IDE" Input Area (Restored Line Numbers)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(InputBg, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                // Line Numbers Column
                Column(
                    modifier = Modifier.width(30.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    val lineCount = text.lines().count().coerceAtLeast(1)
                    (1..lineCount).forEach { number ->
                        Text(
                            text = "$number",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // The Actual Input Field
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = TextStyle(
                        color = TextColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(CyanNeon),
                    modifier = Modifier.fillMaxSize()
                ) { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "// Paste your code here...",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Tactical Buttons
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TacticalButton("SOLVE", onClick = onSolve, modifier = Modifier.weight(1f), color = CyanNeon)
                TacticalButton("DEBUG", onClick = onDebug, modifier = Modifier.weight(1f), color = Color(0xFFFF0055))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TacticalButton("EXPLAIN", onClick = onExplain, modifier = Modifier.weight(1f), color = Color(0xFF00FFAA))
                TacticalButton("ANALYZE", onClick = onAnalyze, modifier = Modifier.weight(1f), color = Color(0xFFFFD700))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Scan Button
        Button(
            onClick = onScan,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
            Text("SCAN FROM CAMERA", color = CyanNeon, fontFamily = FontFamily.Monospace)
        }
    }
}

// Custom "Cyberpunk" Button Style
@Composable
fun TacticalButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}