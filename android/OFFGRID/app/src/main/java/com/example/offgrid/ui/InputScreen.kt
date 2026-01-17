package com.example.offgrid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputScreen(
    text: String,
    onTextChange: (String) -> Unit,
    onSolve: () -> Unit,
    onDebug: () -> Unit,
    onExplain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)   // THIS makes it take all remaining space
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    if (text.isEmpty()) {
                        Text("Type or paste your code here...")
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON ROW - stays at bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onSolve, modifier = Modifier.weight(1f)) {
                Text("Solve")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDebug, modifier = Modifier.weight(1f)) {
                Text("Debug")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onExplain, modifier = Modifier.weight(1f)) {
                Text("Explain")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Coming soon */ },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan Code (Coming Soon)")
        }
    }
}
