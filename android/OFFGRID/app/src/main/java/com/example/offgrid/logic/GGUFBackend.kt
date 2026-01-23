package com.example.offgrid.logic

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import java.io.File

class GGUFBackend(private val context: Context) : AIBackend {

    // ✅ 1. Use our new Bridge instead of the old Engine
    private val nativeBridge = NativeBridge()
    private var isReady = false

    // Exact file name
    private val modelName = "qwen.gguf"

    override suspend fun initialize(): Boolean {
        return try {
            val path = getModelPath()
            val file = File(path)

            if (!file.exists()) {
                Log.e("OFFGRID", "❌ Model missing at: $path")
                return false
            }

            Log.d("OFFGRID", "Loading model from: $path")

            // ✅ 2. Initialize via Bridge
            nativeBridge.init("") // Initialize backend

            // Load the model
            val loadResult = nativeBridge.load(file.absolutePath)
            if (loadResult != 0) {
                Log.e("OFFGRID", "❌ Failed to load model architecture")
                return false
            }

            // Prepare context
            val prepResult = nativeBridge.prepare()
            if (prepResult != 0) {
                Log.e("OFFGRID", "❌ Failed to prepare context")
                return false
            }

            isReady = true
            Log.i("OFFGRID", "✅ Native Engine Loaded Successfully")
            true
        } catch (e: Exception) {
            Log.e("OFFGRID", "💥 Engine Initialization Failure", e)
            false
        }
    }

    override fun generateResponse(prompt: String, mode: String): Flow<String> = flow {
        if (!isReady) {
            emit("Error: Engine is not ready. Did the model load?")
            return@flow
        }

        // Format for ChatML (Qwen specific)
        val formattedPrompt = buildChatML(prompt, mode)

        try {
            // ✅ 3. Process the prompt
            val result = nativeBridge.processUserPrompt(formattedPrompt, 512) // 512 tokens max
            if (result != 0) {
                emit("[Error processing prompt]")
                return@flow
            }

            // ✅ 4. Stream tokens manually
            while (currentCoroutineContext().isActive) {
                val token = nativeBridge.generateNextToken()

                // If token is null, generation is finished
                if (token == null) break

                emit(token)

                // Tiny yield to keep UI responsive and allow cancellation
                delay(1)
            }
        } catch (e: Exception) {
            emit("\n[Error: ${e.message}]")
        }
    }.flowOn(Dispatchers.IO)

    override fun getEngineName(): String = "Native GGUF (Qwen)"

    override fun shutdown() {
        Log.d("OFFGRID", "🔌 Shutting down Native Engine...")
        try {
            if (isReady) {
                nativeBridge.unload()
                nativeBridge.shutdown()
                isReady = false
            }
        } catch (e: Exception) {
            Log.e("OFFGRID", "Error shutting down engine", e)
        }
    }

    private fun getModelPath(): String {
        // ✅ Uses Internal Private Storage (Safe)
        return File(context.filesDir, modelName).absolutePath
    }

    private fun buildChatML(userText: String, mode: String): String {
        return "<|im_start|>system\nYou are an offline coding assistant.\n<|im_end|>\n<|im_start|>user\n$userText<|im_end|>\n<|im_start|>assistant\n"
    }
}