# Implementation Plan & Roadmap — HENRY Offline Coding Assistant

**Plan Version:** 1.0
**Status:** Active

This roadmap translates the PRD (Root v1.1), UX (v1.1), and Technical Architecture (v1.0) into a **practical build plan** with clear milestones, dependencies, and exit criteria. It is structured for a small team (1–3 developers) but works for solo execution as well.

---

## 0. Overall Strategy

**Build order (non-negotiable):**

1. Local model runs on a device (technical feasibility)
2. Download reliability works
3. Inference + cancel works
4. State persistence works
5. UI is built last

This minimizes risk and avoids building pretty screens on top of an unstable core.

---

## Phase 1 — Local Inference Feasibility (Week 1)

**Goal:** Prove that Qwen2.5-Coder-1.5B-Instruct can run acceptably on Android.

### Tasks

1. Set up Android project (Kotlin + Compose).
2. Compile **llama.cpp for Android (ARM64-v8a)** via NDK.
3. Convert/download model to **GGUF (Q4)**.
4. Write minimal JNI wrapper to:

   * Load model from file
   * Generate text for a fixed prompt
5. Run on a real mid-range phone.

### Exit Criteria

* Model loads successfully on device
* First token latency < 5s
* Sustained generation works for ~200 tokens

**Deliverable:** “Hello world” inference demo.

---

## Phase 2 — Inference Engine + Cancellation (Week 2)

**Goal:** Make inference safe, controllable, and interruptible.

### Tasks

1. Move inference to a dedicated background thread.
2. Implement **volatile cancellation flag** polled between tokens.
3. Expose JNI methods:

   * startInference(prompt)
   * cancelInference()
4. Add hard timeout (30s) as backup.
5. Measure CPU, temperature, and battery impact.

### Exit Criteria

* Cancel stops generation within ~200ms
* No memory leaks after cancel
* App remains responsive during inference

**Deliverable:** Reliable, cancel-safe inference engine.

---

## Phase 3 — First-Run Model Download (Week 3)

**Goal:** Make the 1GB download resilient in bad networks.

### Tasks

1. Build a **custom Download Manager** using HTTP range requests.
2. Persist download progress (bytes downloaded) in Room.
3. Implement:

   * Pause / resume
   * Retry with backoff
   * Checksum validation (SHA-256)
4. Only swap in new model after successful validation.
5. Add Wi-Fi-only toggle.

### Exit Criteria

* Download resumes after network drop
* Partial files are preserved
* Corrupt downloads are detected

**Deliverable:** Production-grade, resumable model downloader.

---

## Phase 4 — Persistence & Process Death Tolerance (Week 4)

**Goal:** Never lose user input or last response.

### Tasks

1. Define Room schema:

   * last_input (TEXT)
   * last_response (TEXT)
   * last_mode (ENUM)
   * model_version (TEXT)
   * download_state (ENUM)
2. Persist state in `onStop()`.
3. Restore state in `onStart()`.
4. Handle mid-inference process death:

   * Restore last completed response
   * Allow user to rerun inference
5. Add local caching for large responses if needed.

### Exit Criteria

* Backgrounding + return restores state
* OS kill + relaunch restores state

**Deliverable:** Crash/kill-tolerant app core.

---

## Phase 5 — Prompt Pipeline + HENRY System Prompt (Week 5)

**Goal:** Make outputs consistent and structured.

### Tasks

1. Embed **HENRY v1.1 system prompt** into all requests.
2. Create three prompt templates:

   * SOLVE
   * DEBUG
   * EXPLAIN
3. Enforce output limits (max tokens, timeout).
4. Stream tokens to UI in sections:

   * Understanding
   * Approach
   * Solution (collapsed by default)
5. Basic post-processing to ensure Markdown fences are present.

### Exit Criteria

* Outputs follow structure reliably
* No rambling or runaway text

**Deliverable:** Stable, predictable AI behavior.

---

## Phase 6 — Core UI (Week 6)

**Goal:** Build the minimal but correct UX.

### Screens to implement

1. **First Launch:**

   * Model download screen
   * Progress + pause/resume
2. **Main Input Screen:**

   * Text input
   * Paste button
   * **OCR (Coming Soon) button visible**
   * Solve / Debug / Explain buttons
3. **Processing Screen:**

   * Spinner + "HENRY is thinking… (offline)"
   * Prominent Cancel button
4. **Response Screen:**

   * Expandable sections
   * Monospace code blocks
   * Copy button

### Exit Criteria

* Users can get help in < 3 taps
* Cancel button is always visible during inference

**Deliverable:** Usable MVP UI.

---

## Phase 7 — Error Handling & Edge Cases (Week 7)

**Goal:** Make the app feel robust, not brittle.

### Tasks

1. Handle:

   * Model load failure
   * Download failure
   * Timeout during generation
   * Memory pressure
2. Show clear, non-blaming messages.
3. Add graceful degradation:

   * Suggest shorter prompts
   * Offer redownload if model is corrupted

### Exit Criteria

* No hard crashes in common failure cases
* Errors are understandable to students

**Deliverable:** Resilient UX.

---

## Phase 8 — Performance Tuning (Week 8)

**Goal:** Make it fast enough for real use.

### Tasks

1. Tune:

   * Thread count
   * Context window
   * Max output tokens
2. Optimize model load time (lazy loading, caching).
3. Reduce first-token latency where possible.
4. Test on low-end vs mid-range devices.

### Exit Criteria

* First token < 3s on typical device
* Sustained 5–15 tokens/sec

**Deliverable:** Acceptable real-world performance.

---

## Phase 9 — Testing & Validation (Week 9)

**Goal:** Treat this like a real product, not a demo.

### Tests to run

* Cancel mid-generation (multiple times)
* Background app during inference → restore state
* Kill app mid-download → resume
* Corrupt model file → detect + recover
* Large input → graceful handling

### Acceptance Criteria

* No data loss
* No runaway battery drain
* No unexplained crashes

**Deliverable:** Tested, trustworthy MVP.

---

## Phase 10 — Optional Phase 2 Features (Post-MVP)

### OCR Integration

* Add on-device OCR (ML Kit / Tesseract)
* Pipe scanned text into prompt pipeline
* Reuse same persistence & cancel logic

### Advanced Model Mode

* Offer optional larger model for high-end devices
* Keep default Qwen for stability

### Desktop Companion

* Lightweight desktop version for power users

---

## Rough Timeline Summary

| Phase | Duration | Milestone               |
| ----- | -------- | ----------------------- |
| 1     | Week 1   | Model runs locally      |
| 2     | Week 2   | Cancel-safe inference   |
| 3     | Week 3   | Resumable download      |
| 4     | Week 4   | Process-death tolerance |
| 5     | Week 5   | HENRY prompt pipeline   |
| 6     | Week 6   | Core UI                 |
| 7     | Week 7   | Error handling          |
| 8     | Week 8   | Performance tuning      |
| 9     | Week 9   | Testing & validation    |

---

## What You’ll Have at the End

* A real, offline, on-device coding assistant
* Clear product boundaries
* Resilient architecture
* Strong mobile UX
* Zero dependence on internet at runtime
