package com.example.offgrid.logic

import android.content.Context

object BackendFactory {

    // ✅ FIX: Added 'context' parameter here so we can pass it to the backend
    fun create(context: Context): AIBackend {
        return GGUFBackend(context)
        // later: return GemmaBackend(context)
        // later: return DeepSeekBackend(context)
    }
}