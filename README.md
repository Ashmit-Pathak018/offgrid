# OFFGRID — Offline Android Coding Assistant (HENRY)

OFFGRID is an offline-first Android application that runs a local Large Language Model (LLM) on your device to help you understand, debug, and solve programming problems without internet access.

It is designed for students and developers who often work in environments with poor or no connectivity (college labs, hostels, restricted networks, or remote areas).

---

## Vision

To provide a reliable, privacy-preserving, and practical on-device AI coding assistant that works even when the internet doesn’t.

OFFGRID is a tool, not a policing system — it helps users think clearly, understand code, and make progress, rather than blocking or judging them.

---

## Meet HENRY (The AI)

HENRY is the app’s built-in AI assistant:

- Calm, precise, and slightly dry in tone  
- Structured in responses (Understanding → Approach → Solution)  
- Optimized for programming logic and debugging  
- Runs fully offline on-device  

His exact behavior is defined in:
docs/henry_prompt_spec.md

---

## Key Features (Planned for v1)

### Offline First
- No internet required after initial model download  
- All inference runs locally on your device  

### Coding-Focused AI
- Supports: C, C++, Python, Java, JavaScript  
- Can:
  - Explain code  
  - Debug errors  
  - Provide structured solutions  

### Mobile-Friendly UX
- Paste-based input  
- Visible Scan Code (OCR – Coming Soon) button from Day 1  
- Expandable sections for:
  - Understanding  
  - Approach  
  - Solution  

### Safe & Reliable Inference
- Prominent Cancel button to stop runaway generation  
- Protection against battery drain and overheating  

### Process Death Tolerance
- Your last input and last response are saved  
- Works correctly even if Android kills the app in the background  

### Resilient First-Run Download
- Model download is resumable  
- Progress is saved — it never restarts from 0% after a network drop  

---

## Technical Overview

- Model: Qwen/Qwen2.5-Coder-1.5B-Instruct (GGUF, Q4)  
- Inference Engine: llama.cpp via Android NDK  
- Architecture:
  - Kotlin + Jetpack Compose UI  
  - Room for persistence  
  - Native inference via JNI  
  - Dedicated background thread for model execution  

For full details, see:
docs/technical_architecture.md

---

## Repository Structure

henry-offline-coding-assistant/
├── android/              # Android app (to be built)
├── native/               # llama.cpp + JNI bindings
└── docs/
   ├── root_document.md
   ├── ux_flow.md
   ├── henry_prompt_spec.md
   ├── technical_architecture.md
   └── roadmap.md

---

## Roadmap (High Level)

1. Local model runs on Android  
2. Resilient, resumable model downloader  
3. Interruptible inference with Cancel  
4. Process-death safe persistence  
5. Core UI with OCR affordance  
6. Performance tuning  
7. Testing & validation  

See full plan:
docs/roadmap.md

---

## Status

Current Version: v1.1 (PRD + UX + Architecture Complete)  
Next Step: Implement Android app and on-device inference.

---

## License

TBD — will be decided in a later phase.
