package com.example.offgrid.logic

/**
 * This class is the ONLY place where Kotlin talks to C++.
 * The function names here MUST match the C++ names exactly.
 */
class NativeBridge {

    // 1. Setup
    external fun init(libPath: String)
    external fun load(modelPath: String): Int
    external fun prepare(): Int

    // 2. Chat
    external fun processUserPrompt(prompt: String, nPredict: Int): Int
    external fun generateNextToken(): String?

    // 3. Cleanup
    external fun unload()
    external fun shutdown()

    // 4. Stubs (Required to match the C++ file fully)
    // ✅ Your friend helped fix these, so we include them here!
    external fun processSystemPrompt(prompt: String): Int
    external fun systemInfo(): String
    external fun benchModel(pp: Int, tg: Int, pl: Int, nr: Int): String

    companion object {
        init {
            // Load the C++ library ("ai_chat" matches CMakeLists.txt)
            System.loadLibrary("ai_chat")
        }
    }
}