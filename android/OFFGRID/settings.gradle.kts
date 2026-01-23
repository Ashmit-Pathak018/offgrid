pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "OFFGRID"
include(":app") // Your Main App

// --- THE FIX ---
// 1. Give names to the two parts of the engine
include(":llama-wrapper")
include(":llama-lib")

// 2. Point them to the correct folders inside 'llama-engine'
project(":llama-wrapper").projectDir = file("llama-engine/app")
project(":llama-lib").projectDir = file("llama-engine/lib")