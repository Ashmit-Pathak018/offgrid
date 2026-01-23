#ifndef LLAMA_ANDROID_LOGGING_H
#define LLAMA_ANDROID_LOGGING_H

#include <android/log.h>

#define TAG "llama-android.cpp"

// ✅ FIX: Renamed to match usage in ai_chat.cpp (LOGi, LOGd, etc.)
#define LOGi(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)
#define LOGw(...) __android_log_print(ANDROID_LOG_WARN,  TAG, __VA_ARGS__)
#define LOGe(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGd(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGv(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

#endif //LLAMA_ANDROID_LOGGING_H