package com.arm.aichat.internal

import android.content.Context
import android.util.Log
import com.arm.aichat.InferenceEngine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.IOException

internal class InferenceEngineImpl private constructor(
    private val nativeLibDir: String
) : InferenceEngine {

    companion object {
        private val TAG = "InferenceEngine"
        @Volatile private var instance: InferenceEngine? = null

        internal fun getInstance(context: Context): InferenceEngine =
            instance ?: synchronized(this) {
                // We grab the native path so C++ knows where to look for itself
                val libDir = context.applicationInfo.nativeLibraryDir
                InferenceEngineImpl(libDir).also { instance = it }
            }
    }

    // NATIVE METHODS (Do not rename these! C++ needs them exactly like this)
    private external fun init(nativeLibDir: String)
    private external fun load(modelPath: String): Int
    private external fun prepare(): Int
    private external fun systemInfo(): String
    private external fun benchModel(pp: Int, tg: Int, pl: Int, nr: Int): String
    private external fun processSystemPrompt(systemPrompt: String): Int
    private external fun processUserPrompt(userPrompt: String, predictLength: Int): Int
    private external fun generateNextToken(): String?
    private external fun unload()
    private external fun shutdown()

    private val _state = MutableStateFlow<InferenceEngine.State>(InferenceEngine.State.Uninitialized)
    override val state: StateFlow<InferenceEngine.State> = _state.asStateFlow()

    @Volatile private var _cancelGeneration = false
    private var _readyForSystemPrompt = false

    // Single thread for the brain (C++ is not thread-safe)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val llamaDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val llamaScope = CoroutineScope(llamaDispatcher + SupervisorJob())

    init {
        llamaScope.launch {
            try {
                Log.i(TAG, "Loading native library 'ai-chat'...")
                // This matches your CMakeLists.txt project name
                System.loadLibrary("ai-chat")

                init(nativeLibDir)
                _state.value = InferenceEngine.State.Initialized
                Log.i(TAG, "Engine Initialized!")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load C++ library", e)
                _state.value = InferenceEngine.State.Error(e)
            }
        }
    }

    override suspend fun loadModel(pathToModel: String) = withContext(llamaDispatcher) {
        try {
            _state.value = InferenceEngine.State.LoadingModel
            if (load(pathToModel) != 0) throw UnsupportedArchitectureException()
            if (prepare() != 0) throw IOException("Failed to prepare resources")
            _readyForSystemPrompt = true
            _state.value = InferenceEngine.State.ModelReady
        } catch (e: Exception) {
            _state.value = InferenceEngine.State.Error(e)
            throw e
        }
    }

    override suspend fun setSystemPrompt(prompt: String) = withContext(llamaDispatcher) {
        processSystemPrompt(prompt)
        _state.value = InferenceEngine.State.ModelReady
        Unit
    }

    override fun sendUserPrompt(message: String, predictLength: Int): Flow<String> = flow {
        _state.value = InferenceEngine.State.ProcessingUserPrompt
        if (processUserPrompt(message, predictLength) != 0) return@flow

        _state.value = InferenceEngine.State.Generating
        while (!_cancelGeneration) {
            val token = generateNextToken() ?: break
            if (token.isNotEmpty()) emit(token)
        }
        _state.value = InferenceEngine.State.ModelReady
    }.flowOn(llamaDispatcher)

    override suspend fun bench(pp: Int, tg: Int, pl: Int, nr: Int): String = withContext(llamaDispatcher) {
        benchModel(pp, tg, pl, nr)
    }

    override fun cleanUp() {
        _cancelGeneration = true
        runBlocking(llamaDispatcher) { unload() }
    }

    override fun destroy() {
        _cancelGeneration = true
        llamaScope.cancel()
    }
}