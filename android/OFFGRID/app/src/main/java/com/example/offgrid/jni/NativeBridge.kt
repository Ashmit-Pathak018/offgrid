package com.example.offgrid.jni

object NativeBridge {

    init {
        // Later we will load:
        // System.loadLibrary("offgrid")
    }

    external fun runModel(input: String): String
}
