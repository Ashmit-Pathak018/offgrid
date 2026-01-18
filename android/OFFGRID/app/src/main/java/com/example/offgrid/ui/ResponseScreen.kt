package com.example.offgrid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography

@Composable
fun ResponseScreen(
    response: String = "Waiting for input...",
    onBack: () -> Unit
) {
    // 1. Define Brand Colors
    val CyanNeon = Color(0xFF00E5FF)
    val DarkBackground = Color(0xFF0F172A)
    val TerminalBg = Color(0xFF1E293B)
    val OffWhite = Color(0xFFE2E8F0)
    val CodeBg = Color(0xFF000000)

    // 2. Colors Implementation
    val customColors = object : MarkdownColors {
        override val text = OffWhite
        override val codeText = CyanNeon
        override val codeBackground = CodeBg
        override val inlineCodeText = CyanNeon
        override val inlineCodeBackground = TerminalBg
        override val dividerColor = Color.Gray
        override val linkText = CyanNeon
    }

    // 3. Typography Implementation
    val monoFont = FontFamily.Monospace
    val customTypography = object : MarkdownTypography {
        // Headers
        override val h1 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = CyanNeon)
        override val h2 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        override val h3 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        override val h4 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        override val h5 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        override val h6 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)

        // Base Text
        override val text = TextStyle(fontFamily = monoFont, fontSize = 15.sp, color = OffWhite)
        override val paragraph = TextStyle(fontFamily = monoFont, fontSize = 15.sp, lineHeight = 24.sp, color = OffWhite)

        // Code Blocks
        override val code = TextStyle(fontFamily = monoFont, fontSize = 14.sp, color = CyanNeon, background = CodeBg)
        override val inlineCode = TextStyle(fontFamily = monoFont, fontSize = 14.sp, color = CyanNeon, background = TerminalBg)
        override val quote = TextStyle(fontFamily = monoFont, fontSize = 14.sp, color = Color.Gray)

        // Lists
        override val list = TextStyle(fontFamily = monoFont, fontSize = 15.sp, color = OffWhite)
        override val ordered = TextStyle(fontFamily = monoFont, fontSize = 15.sp, color = OffWhite)
        override val bullet = TextStyle(fontFamily = monoFont, fontSize = 15.sp, color = OffWhite)

        // Extras
        override val link = TextStyle(fontFamily = monoFont, fontSize = 15.sp, color = CyanNeon)

        // REMOVED: override val image = ... (This caused the error!)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "HENRY // OUTPUT_LOG",
            color = CyanNeon,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Terminal Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(TerminalBg, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(modifier = Modifier.verticalScroll(scrollState)) {

                Markdown(
                    content = response,
                    colors = customColors,
                    typography = customTypography
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Done Button
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
                fontFamily = FontFamily.Monospace
            )
        }
    }
}