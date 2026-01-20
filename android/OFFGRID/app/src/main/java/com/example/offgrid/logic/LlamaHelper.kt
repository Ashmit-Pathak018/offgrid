package com.example.offgrid.logic

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

class LlamaHelper(private val context: Context) {

    private var llmInference: LlmInference? = null

    // 1. A "Bridge" to send messages from the fixed listener to the dynamic flow
    private var activeCallback: ((String, Boolean) -> Unit)? = null

    fun initModel(): Boolean {
        val modelFile = File("/data/local/tmp/model.bin")
        if (!modelFile.exists()) return false

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelFile.absolutePath)
            .setMaxTokens(1024)
            .setTemperature(0.5f) // Adds a little creativity
            .setRandomSeed(42)
            // 2. The listener is set ONCE here. We tell it to use our "Bridge".
            .setResultListener { partialResult, done ->
                activeCallback?.invoke(partialResult, done)
            }
            .build()

        return try {
            llmInference = LlmInference.createFromOptions(context, options)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun solveProblem(prompt: String): Flow<String> = callbackFlow {
        if (llmInference == null) {
            trySend("Error: Brain Offline. (Did you push model.bin?)")
            close()
            return@callbackFlow
        }

        val fullPrompt = "You are Henry, a coding assistant. Answer concisely.\nUser: $prompt\nHenry:"

        // 3. Connect the Flow to the Bridge
        activeCallback = { response, done ->
            trySend(response) // Send the chunk to the UI
            if (done) {
                close() // Stop the flow when finished
            }
        }

        // 4. Trigger the generation (No lambda passed here anymore!)
        llmInference?.generateResponseAsync(fullPrompt)

        // Cleanup when the flow is cancelled (e.g., user leaves screen)
        awaitClose {
            activeCallback = null
        }
    }
}