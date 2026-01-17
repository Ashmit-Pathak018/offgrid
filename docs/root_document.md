# Project Root Document

**Version:** 1.1  
**Status:** Active

---

## Project Name (Working Title)
**OFFGRID** (temporary)  
Offline AI Coding Assistant for Android

---

## 1. Core Idea / Vision

Build an **offline-first Android application** that acts as a local AI coding assistant capable of explaining, debugging, and solving programming problems **without internet access**.

The app is designed for real-world scenarios where connectivity is unreliable or unavailable (college labs, hostels, restricted networks, remote areas).

This is a **tool**, not a policing system. The goal is to reduce friction in learning and problem-solving, not to enforce artificial limitations on how users learn.

---

## 2. Problem Statement

Students and developers often get stuck on programming problems in environments with poor or no internet access. Existing AI tools (ChatGPT, StackOverflow, online IDEs) are inaccessible in these moments, causing:
- Wasted time  
- Frustration  
- Broken learning flow  

The problem is not lack of intelligence or effort, but lack of **access**.

---

## 3. Target Users

**Primary:**
- Computer Science students  
- Engineering students in labs  
- Beginners learning programming  

**Secondary:**
- Developers working offline  
- Users in low-connectivity regions  

---

## 4. Product Philosophy

- Tools are neutral; intent is user-controlled  
- Learning does not stop at seeing solutions  
- Reducing frustration improves retention  
- Do not fight user behavior; support it  

The app can provide **full solutions**, presented in a structured, explain-first manner.

---

## 5. Scope Definition (v1)

### In Scope
- Offline operation  
- Programming problem solving  
- Debugging and code explanation  
- Core languages (C, C++, Python, Java, JavaScript)  
- Structured responses (explain → approach → solution)  

### Out of Scope
- Exam-specific features  
- Online dependency  
- Frameworks and large projects  
- Cloud sync or accounts  

---

## 6. AI Persona

**HENRY** — a calm, precise, slightly dry AI coding assistant.

HENRY’s behavior, tone, and response structure are defined in a separate **Prompt & Behavior Specification** document and must remain stable across model updates.

---

## 7. Technical Strategy (High-Level)

- On-device LLM inference  
- Small, coding-specialized model  
- CPU-only execution  
- Model decoupled from app binary  

---

## 8. Model Strategy (Finalized)

### Default Model (v1)
- **Model:** Qwen/Qwen2.5-Coder-1.5B-Instruct  
- **Format:** GGUF  
- **Quantization:** 4-bit (Q4)  
- **Inference:** CPU-only, on-device  

### Update Policy
- Model downloaded post-install  
- Model updates are optional  
- App must remain functional without updating the model  

---

## 8.1 First-Run Model Download Reliability (Critical)

The initial model download is a **high-risk bottleneck** in low-connectivity environments and must be treated as a reliability-critical system.

**Requirements:**
- Model downloads must be **resumable** (HTTP range requests or equivalent).  
- Partial downloads must be persisted and resumed, not restarted.  
- The app must never discard a partially downloaded model unless corruption is detected.  
- Clear progress indication must be shown (percentage and downloaded size).  
- Users must be able to pause and resume the download safely.  

**Failure Handling:**
- Network interruption must not require restarting from 0%.  
- A failed first-run download that restarts from scratch is considered a **critical product failure**.  

This requirement exists to prevent user drop-off in unreliable network conditions.

---

## 9. UX Principles

- Offline-first mindset  
- Minimal cognitive load  
- Fast access to answers  
- User-controlled depth  
- Tool-like, not social  

---

## 10. Mobile Reality Constraints (v1.1)

This section defines **non-negotiable constraints** derived from real-world Android behavior and offline AI inference. These constraints apply across UX, architecture, and implementation.

### 10.1 Input Reality (Mobile Constraint)

- Prolonged manual typing of code on mobile devices is discouraged.  
- The product must support **non-typing input paths** as first-class citizens.  
- A visible **Scan Code (OCR)** affordance must exist in the primary input UI from v1.  
- OCR functionality may ship in a later phase, but the UI affordance must be present from Day 1 to avoid future layout disruption and user retraining.  

This is a **design-time requirement**, not a post-launch enhancement.

---

### 10.2 Inference Control & Resource Safety

- On-device LLM inference is slow, CPU-intensive, and thermally constrained.  
- The user must retain **explicit, immediate control** over inference execution.  
- A clearly visible **Cancel** action is mandatory during all inference operations.  
- Cancel must:
  - Immediately halt generation  
  - Release allocated resources  
  - Return the system to a safe idle state  

Unbounded or runaway inference is considered a **critical failure mode** due to battery drain and thermal risk.

---

### 10.3 State Persistence & Process Death Tolerance

- Android may terminate background or memory-intensive apps without warning.  
- Loss of generated output is unacceptable in real usage environments (labs, restricted networks).  
- The application must persist:
  - The most recent user input  
  - The most recent generated response  

This state must survive:
- App backgrounding  
- OS-initiated process death  
- App relaunch after memory reclaim  

Failure to restore the last response is considered a **critical UX and product failure**.

---

## 11. Risks & Constraints

- Device RAM limitations  
- Thermal throttling  
- Model hallucinations  

Mitigation via:
- Small model size  
- Explicit cancel controls  
- Structured prompts  

---

## 12. Success Criteria (MVP)

- Fully offline operation  
- Solves common student problems  
- Acceptable response time  
- No loss of responses due to app lifecycle  

---

## 13. Living Document Note

This document is a **living system specification**. Updates reflect real constraints discovered through design and testing and should be treated as system hardening, not scope creep.

---

## 14. Change Log

**v1.1**
- Added Mobile Reality Constraints section  
- Formalized OCR affordance requirement  
- Formalized inference cancel requirement  
- Formalized state persistence as critical  
