package com.example.offgrid.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    isGenerating: Boolean,
    onBack: () -> Unit
) {
    // 1. Context & Clipboard Setup
    val context = LocalContext.current

    fun copyToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("OFFGRID Output", response)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, ">> COPIED TO CLIPBOARD", Toast.LENGTH_SHORT).show()
    }

    // 2. Define Brand Colors
    val CyanNeon = Color(0xFF00E5FF)
    val DarkBackground = Color(0xFF0F172A)
    val TerminalBg = Color(0xFF1E293B)
    val OffWhite = Color(0xFFE2E8F0)
    val CodeBg = Color(0xFF000000)

    // 3. Colors Implementation
    val customColors = object : MarkdownColors {
        override val text = OffWhite
        override val codeText = CyanNeon
        override val codeBackground = CodeBg
        override val inlineCodeText = CyanNeon
        override val inlineCodeBackground = TerminalBg
        override val dividerColor = Color.Gray
        override val linkText = CyanNeon
    }

    // 4. Typography Implementation (Big Text Version)
    val monoFont = FontFamily.Monospace
    val customTypography = object : MarkdownTypography {
        override val h1 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = CyanNeon)
        override val h2 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
        override val h3 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        override val h4 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        override val h5 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        override val h6 = TextStyle(fontFamily = monoFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        override val text = TextStyle(fontFamily = monoFont, fontSize = 18.sp, color = OffWhite)
        override val paragraph = TextStyle(fontFamily = monoFont, fontSize = 18.sp, lineHeight = 28.sp, color = OffWhite)
        override val code = TextStyle(fontFamily = monoFont, fontSize = 16.sp, color = CyanNeon, background = CodeBg)
        override val inlineCode = TextStyle(fontFamily = monoFont, fontSize = 16.sp, color = CyanNeon, background = TerminalBg)
        override val quote = TextStyle(fontFamily = monoFont, fontSize = 16.sp, color = Color.Gray)
        override val list = TextStyle(fontFamily = monoFont, fontSize = 18.sp, color = OffWhite)
        override val ordered = TextStyle(fontFamily = monoFont, fontSize = 18.sp, color = OffWhite)
        override val bullet = TextStyle(fontFamily = monoFont, fontSize = 18.sp, color = OffWhite)
        override val link = TextStyle(fontFamily = monoFont, fontSize = 18.sp, color = CyanNeon)
    }

    // 5. Auto-Scroll Logic
    val scrollState = rememberScrollState()
    LaunchedEffect(response) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // --- HEADER ROW (Title + Copy Button) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HENRY // OUTPUT_LOG",
                color = CyanNeon,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            // The New Copy Button
            IconButton(
                onClick = { copyToClipboard() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Code",
                    tint = CyanNeon
                )
            }
        }

        // --- TERMINAL BOX ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(TerminalBg, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {

                Markdown(
                    content = response,
                    colors = customColors,
                    typography = customTypography
                )

                if (isGenerating) {
                    Spacer(modifier = Modifier.height(8.dp))
                    BlinkingCursor(CyanNeon)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Done Button
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
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

@Composable
fun BlinkingCursor(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Text(
        text = " █",
        color = color.copy(alpha = alpha),
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace
    )
}