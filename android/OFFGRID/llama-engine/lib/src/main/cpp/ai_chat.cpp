#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <cstring>
#include <sstream>

#include "llama.h"
#include "common.h"

#define TAG "OFFGRID_NATIVE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)

static llama_model * g_model = nullptr;
static llama_context * g_context = nullptr;
static llama_batch g_batch;
static struct llama_sampler * g_sampler = nullptr;
static int g_pos = 0;

static void android_log_callback(ggml_log_level level, const char * text, void * user_data) {
    (void)user_data;
    if (level == GGML_LOG_LEVEL_ERROR) LOGE("%s", text);
    else LOGI("%s", text);
}

// -----------------------------------------------------------------------
// INTERNAL HELPER
// -----------------------------------------------------------------------
static int process_prompt_internal(JNIEnv *env, jstring promptStr) {
    if (!g_context) return 1;

    const char *prompt = env->GetStringUTFChars(promptStr, nullptr);
    std::vector<llama_token> tokens = common_tokenize(g_context, prompt, true, true);
    env->ReleaseStringUTFChars(promptStr, prompt);

    for (size_t i = 0; i < tokens.size(); i++) {
        // ✅ CRITICAL FIX: Enable logits only for the LAST token
        bool logits = (i == tokens.size() - 1);

        common_batch_add(g_batch, tokens[i], g_pos, {0}, logits);
        g_pos++;

        if (g_batch.n_tokens >= 512) {
            if (llama_decode(g_context, g_batch) != 0) return 1;
            common_batch_clear(g_batch);
        }
    }

    if (g_batch.n_tokens > 0) {
        if (llama_decode(g_context, g_batch) != 0) return 1;
        common_batch_clear(g_batch);
    }
    return 0;
}

// -----------------------------------------------------------------------
// JNI EXPORTS
// -----------------------------------------------------------------------
extern "C" {

JNIEXPORT void JNICALL
Java_com_example_offgrid_logic_NativeBridge_init(JNIEnv *env, jobject, jstring libPath) {
    llama_log_set(android_log_callback, nullptr);
    llama_backend_init();
    LOGI("Native Engine Initialized");
}

JNIEXPORT jint JNICALL
Java_com_example_offgrid_logic_NativeBridge_load(JNIEnv *env, jobject, jstring modelPathStr) {
    const char *modelPath = env->GetStringUTFChars(modelPathStr, nullptr);
    auto mparams = llama_model_default_params();
    g_model = llama_model_load_from_file(modelPath, mparams);
    env->ReleaseStringUTFChars(modelPathStr, modelPath);

    if (!g_model) {
        LOGE("Failed to load model!");
        return 1;
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_offgrid_logic_NativeBridge_prepare(JNIEnv *env, jobject) {
    if (!g_model) return 1;

    // Params specifically tuned for Android Memory
    auto cparams = llama_context_default_params();
    cparams.n_ctx = 2048;
    cparams.n_batch = 512;

    g_context = llama_init_from_model(g_model, cparams);
    if (!g_context) return 1;

    g_batch = llama_batch_init(512, 0, 1);

    // Sampler Setup
    g_sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(g_sampler, llama_sampler_init_temp(0.7f));
    llama_sampler_chain_add(g_sampler, llama_sampler_init_dist(1234));

    g_pos = 0;
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_offgrid_logic_NativeBridge_processUserPrompt(JNIEnv *env, jobject, jstring promptStr, jint nPredict) {
    return process_prompt_internal(env, promptStr);
}

JNIEXPORT jint JNICALL
Java_com_example_offgrid_logic_NativeBridge_processSystemPrompt(JNIEnv *env, jobject, jstring promptStr) {
    return process_prompt_internal(env, promptStr);
}

JNIEXPORT jstring JNICALL
Java_com_example_offgrid_logic_NativeBridge_generateNextToken(JNIEnv *env, jobject) {
    if (!g_context || !g_sampler) return nullptr;

    // 1. Sample
    llama_token id = llama_sampler_sample(g_sampler, g_context, -1);

    // 2. Accept
    llama_sampler_accept(g_sampler, id);

    // 3. Decode Next (Logits = true because we always need to sample the next word)
    common_batch_add(g_batch, id, g_pos, {0}, true);
    g_pos++;

    if (llama_decode(g_context, g_batch) != 0) return nullptr;
    common_batch_clear(g_batch);

    // 4. Check EOS
    if (llama_vocab_is_eog(llama_model_get_vocab(g_model), id)) {
        return nullptr;
    }

    // 5. Return String
    std::string piece = common_token_to_piece(g_context, id);
    return env->NewStringUTF(piece.c_str());
}

JNIEXPORT void JNICALL
Java_com_example_offgrid_logic_NativeBridge_unload(JNIEnv *, jobject) {
    if (g_sampler) llama_sampler_free(g_sampler);
    if (g_context) llama_free(g_context);
    if (g_model) llama_model_free(g_model);
    llama_batch_free(g_batch);
}

JNIEXPORT void JNICALL
Java_com_example_offgrid_logic_NativeBridge_shutdown(JNIEnv *, jobject) {
    llama_backend_free();
}

// Stubs
JNIEXPORT jstring JNICALL
Java_com_example_offgrid_logic_NativeBridge_systemInfo(JNIEnv *e, jobject) { return e->NewStringUTF("Ready"); }

JNIEXPORT jstring JNICALL
Java_com_example_offgrid_logic_NativeBridge_benchModel(JNIEnv *e, jobject, jint, jint, jint, jint) { return e->NewStringUTF("Skipped"); }

} // End extern "C"