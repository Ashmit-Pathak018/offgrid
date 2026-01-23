package com.example.offgrid.logic

import kotlinx.coroutines.flow.Flow

/**
 * The Universal Contract.
 * The UI talks to this interface, so it doesn't care if we use Qwen, Llama, or DeepSeek.
 */
interface AIBackend {

    /**
     * Initializes the model (loads file from storage to RAM).
     * @return true if successful, false if failed.
     */
    suspend fun initialize(): Boolean

    /**
     * Streaming response generator.
     * @param prompt User input.
     * @param mode The mode (DEBUG, SOLVE, etc.) to format the system prompt.
     */
    fun generateResponse(prompt: String, mode: String): Flow<String>

    /**
     * Returns the engine name for debugging (e.g., "GGUF/Qwen").
     */
    fun getEngineName(): String

    /**
     * Cleanup native / ML resources.
     */
    fun shutdown()
}
