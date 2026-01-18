package com.example.offgrid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun InputScreen(
    text: String,
    onTextChange: (String) -> Unit,
    onSolve: () -> Unit,
    onDebug: () -> Unit,
    onExplain: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Kotlin") }
    val languages = listOf("Kotlin", "Java", "C", "C++", "Python")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // -------- TOP ROW: LANGUAGE SELECTOR ONLY --------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedLanguage)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                selectedLanguage = lang
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -------- CODE INPUT CARD --------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                "Type or paste your code here...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -------- BUTTON GRID (2 x 2) --------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSolve,
                modifier = Modifier.weight(1f)
            ) {
                Text("Solve")
            }

            Button(
                onClick = onDebug,
                modifier = Modifier.weight(1f)
            ) {
                Text("Debug")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onExplain,
                modifier = Modifier.weight(1f)
            ) {
                Text("Explain")
            }

            Button(
                onClick = onSolve, // reuse Solve for now
                modifier = Modifier.weight(1f)
            ) {
                Text("Analyze")
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
