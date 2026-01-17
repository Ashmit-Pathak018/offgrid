package com.example.offgrid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProcessingScreen(
    onCancel: () -> Unit,
    onFakeComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        Text("HENRY is thinking… (offline)")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onCancel) {
            Text("CANCEL")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Temporary button just for demo/testing
        TextButton(onClick = onFakeComplete) {
            Text("Simulate Response (for now)")
        }
    }
}
