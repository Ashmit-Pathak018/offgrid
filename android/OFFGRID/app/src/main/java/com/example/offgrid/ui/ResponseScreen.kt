package com.example.offgrid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResponseScreen(onBack: () -> Unit) {
    var showSolution by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("HENRY’s Response", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Understanding:")
        Text("You are trying to solve a programming problem...")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Approach:")
        Text("Break the problem into smaller steps...")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { showSolution = !showSolution }) {
            Text(if (showSolution) "Hide Solution" else "Show Solution")
        }

        if (showSolution) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Solution:")
            Text("Here would be the final code / answer.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Input")
        }
    }
}
