# Technical Architecture Document — HENRY Offline Coding Assistant

**Architecture Version:** 1.0
**Status:** Active

This document translates the Product Requirements (Root Document v1.1) and UX Flow (v1.1) into a concrete, implementable technical architecture. It is written for Android engineers, ML engineers, and reviewers.

---

## 1. System Overview (High-Level)

The system follows a **local-first, edge-inference architecture**:

```
+--------------------+        +-----------------------+
|   Android UI Layer | <----> |  App Core / Orchestrator |
| (Compose + State)  |        | (Business Logic)       |
+--------------------+        +-----------------------+
           |                               |
           v                               v
+--------------------+        +-----------------------+
|  Persistence Layer |        |   Inference Engine    |
| (Room + Files)     |        | (llama.cpp / NDK)     |
+--------------------+        +-----------------------+
           |                               |
           v                               v
   Local App Storage                 Local GGUF Model
```

### Core Design Principles

* **Offline-first:** No runtime internet dependency.
* **Decoupled model:** App binary ≠ model weights.
* **Fail-safe:** Explicit handling of process death, network loss, and runaway inference.
* **Interruptible:** Inference can always be cancelled.

---

## 2. Component Breakdown

### 2.1 UI Layer (Android)

* **Framework:** Kotlin + Jetpack Compose.
* **State management:** ViewModel + SavedStateHandle.
* **Responsibilities:**

  * Render input, processing state, and response screens.
  * Expose explicit **Cancel** during inference.
  * Display OCR affordance (disabled/"Coming Soon" in v1).
  * Restore last input/response on process recreation.

### 2.2 App Core / Orchestrator

* Acts as the **single coordinator** between UI, persistence, and inference.
* Responsibilities:

  * Route user actions (Solve/Debug/Explain) to prompt templates.
  * Manage inference lifecycle (start, cancel, cleanup).
  * Enforce output limits (max tokens, timeouts).
  * Handle model availability and versioning.

### 2.3 Persistence Layer

Two-tier persistence is required:

#### (A) Lightweight State — Room Database

Stores small, structured state:

* last_input: TEXT
* last_response: TEXT
* last_mode: ENUM (SOLVE/DEBUG/EXPLAIN)
* model_version: TEXT
* download_state: ENUM (NONE/IN_PROGRESS/PAUSED/COMPLETE)

Room ensures survival across:

* App backgrounding
* Process death
* Device restarts (optional, but recommended)

#### (B) Heavy Artifacts — File Storage

Stores large binary assets:

* GGUF model file (private app storage)
* Partial download chunks (if applicable)
* Optional cached prompts/responses (LRU cleanup)

---

## 3. Model Management

### 3.1 Model Format & Location

* **Format:** GGUF (Qwen2.5-Coder-1.5B-Instruct, Q4 quantized).
* **Storage:** Private app directory (`/data/data/<pkg>/files/models/`).
* **Integrity:** SHA-256 checksum stored in Room and verified post-download.

### 3.2 First-Run Download (Reliability-Critical)

A dedicated **Download Manager** component must implement:

* **Resumable downloads** using HTTP range requests.
* Persistent progress tracking (bytes downloaded).
* Pause/resume support.
* Corruption detection via checksum validation.
* Atomic swap: only replace old model after full validation.

Failure handling:

* Network interruption must not reset progress to 0%.
* If corruption is detected, only the affected range is redownloaded when possible.

### 3.3 Model Updates

* Updates are optional and user-initiated.
* Old model may be retained as fallback until new model is validated.
* Version metadata stored in Room.

---

## 4. Inference Engine (NDK + llama.cpp)

### 4.1 Native Layer

* **Engine:** llama.cpp compiled for Android (ARM64-v8a).
* Exposed via JNI bindings to Kotlin.
* Responsibilities:

  * Load GGUF model into memory.
  * Stream tokens incrementally.
  * Support hard interruption via a cancellation flag.

### 4.2 Threading Model

* **UI Thread:** Never runs inference.
* **Inference Thread (Background):** Dedicated single-thread executor.
* **I/O Thread:** Handles model loading, file reads, and downloads.

### 4.3 Cancellation Semantics (Hard Requirement)

* Cancel must be immediate and deterministic.
* Implementation:

  * A volatile cancellation flag polled between token generations.
  * On cancel: stop generation loop, free buffers, notify Kotlin layer.

---

## 5. Prompting & Output Control

### 5.1 Prompt Templates

Three templates map to UX modes:

* **Solve:** Problem → explain → approach → solution.
* **Debug:** Identify error → explain → fix code.
* **Explain:** Break down code logically.

All prompts prepend HENRY’s system prompt (v1.1).

### 5.2 Token & Time Limits

* **Max output tokens:** 1024 (configurable).
* **Hard timeout:** 30 seconds (configurable).
* If exceeded, auto-cancel with user-friendly message.

### 5.3 Streaming Output

* Tokens streamed to UI as they arrive.
* UI renders sections incrementally but locks final structure (Understanding/Approach/Solution).

---

## 6. State Persistence & Process Death Tolerance

### 6.1 Critical State to Persist

* last_input
* last_response
* current_mode
* in_progress flag

### 6.2 Lifecycle Handling

* On `onStop()`: persist state to Room.
* On `onStart()`: restore state and UI.
* If process was killed mid-inference:

  * UI restores last completed response.
  * User may re-run inference manually.

---

## 7. OCR Integration (Future Phase)

### 7.1 Placeholder Architecture (v1)

* UI button present but disabled or marked "Coming Soon".
* No backend dependency in v1.

### 7.2 Phase 2 Plan

* Integrate on-device OCR (ML Kit or Tesseract).
* Convert image → text → pass to prompt pipeline.
* Preserve same persistence and cancellation semantics.

---

## 8. Error Handling & Degradation

### 8.1 Inference Failures

* If model fails to load: show clear error + offer redownload.
* If generation times out: allow retry with shorter prompt.
* If memory pressure occurs: gracefully cancel and notify user.

### 8.2 Download Failures

* Resume where left off.
* If repeatedly failing, suggest Wi-Fi.
* Never force restart from 0%.

---

## 9. Security & Privacy

* All inference and storage are **local-only**.
* No telemetry required for v1.
* Model and user data stored in app-private directories.

---

## 10. Performance Targets

* Model load time: < 5 seconds (typical mid-range device).
* First token latency: < 3 seconds.
* Sustained generation: 5–15 tokens/sec (device-dependent).
* Cancel latency: < 200 ms.

---

## 11. Testing & Validation Checklist

* Cancel works mid-generation 100% of the time.
* State survives app backgrounding and process death.
* Download resumes after network drop.
* Checksum validation catches corruption.
* UI correctly restores last response.
* No thermal runaway during long runs.

---

## 12. Risks & Mitigations

| Risk                 | Mitigation                         |
| -------------------- | ---------------------------------- |
| OOM during inference | Limit context + output tokens      |
| Slow devices         | Allow smaller fallback model later |
| Corrupt download     | Checksum + resumable ranges        |
| Process death        | Room persistence                   |

---

## 13. Versioning

* Architecture Version: 1.0
* Tied to Root Document v1.1 and UX v1.1
