# UX Flow Document – HENRY Offline Coding Assistant

**UX Spec Version:** 1.1
**Status:** Active

This document defines the **user experience flow**, screen-by-screen behavior, and interaction logic for the offline Android application powered by HENRY.

The UX is designed for **clarity under constraint**: small screens, offline usage, limited compute, and time pressure (labs, practice sessions).

---

## 1. UX Design Principles

* Offline-first mindset
* Minimal cognitive load
* Fast access to answers
* No unnecessary steps
* User always in control of depth (explanation vs solution)

The UI must feel like a **tool**, not a social app.

---

## 2. Primary User Scenarios

### Scenario A: Stuck on a Problem

* User pastes a problem statement
* Wants explanation + solution

### Scenario B: Debugging Code

* User pastes code + error
* Wants to know what went wrong

### Scenario C: Understanding Code

* User pastes unfamiliar code
* Wants a breakdown

These three scenarios cover >90% of real usage.

---

## 3. App Launch Flow

### First Launch (Only Once)

1. Splash screen (brief)
2. Short explanation:

   * "Works fully offline"
   * "Model downloads once"
3. Model download screen

   * Show model name
   * Show size
   * Wi-Fi only toggle
4. Download progress
5. Ready state

### Subsequent Launches

* Directly opens to **Main Input Screen**

---

## 4. Main Input Screen (Core Screen)

### Elements

* Title bar: "HENRY – Offline Coding Assistant"
* Offline indicator (always visible)
* Large text input area
* Optional language selector
* **Input Assist Buttons:**

  * Paste
  * **Scan Code (OCR – Coming Soon)**
* Action buttons:

  * Solve
  * Debug
  * Explain

### Behavior

* Input supports:

  * Problem statements
  * Code
  * Errors
* The **Scan Code (OCR)** button is visible from Day 1 but may be disabled or marked "Coming Soon" in v1.
* This establishes a clear mental model for non-typing input and avoids future UI clutter.
* No formatting tools initially (keep simple)

---

## 5. Interaction Flow

### Step 1: User Input

* User pastes or types content
* Optionally selects language

### Step 2: Action Selection

* **Solve** → Problem-solving mode
* **Debug** → Debugging mode
* **Explain** → Code explanation mode

Each mode maps to a specific prompt template.

---

## 6. Processing State (Inference Control)

### While HENRY Is Working

* Disable text input
* Show spinner + text:

  * "HENRY is thinking… (offline)"
* Show a **prominent Cancel button** (always visible)

### Cancel Behavior (Hard Requirement)

* Immediately stop inference
* Release CPU / memory resources
* Return user to input state

The Cancel action exists to:

* Prevent runaway inference loops
* Avoid battery drain and thermal spikes
* Give the user explicit control during slow on-device inference

Avoid fake typing animations.

---

## 7. Response Screen Layout

Responses are displayed in **expandable sections**:

### Section 1: Understanding

* Visible by default
* Short and concise

### Section 2: Approach

* Visible by default
* Explains logic

### Section 3: Solution

* Collapsed by default
* Expand on user tap

### Section 4: Notes (if present)

* Collapsed

---

## 7.1 State Persistence (Critical)

Due to Android memory management during heavy on-device inference:

* The app must persist:

  * Last user input
  * Last generated response
* State must survive:

  * App backgrounding
  * OS-initiated app kills
  * App resume after memory reclaim

On resume, the app must restore the last state automatically.

This prevents user frustration and data loss in lab environments.

---

## 8. Code Display Behavior

* Monospaced font
* Syntax highlighting (basic)
* Copy button
* Scrollable horizontally and vertically

No in-app execution in v1.

---

## 9. Error Handling UX

If HENRY fails or times out:

* Show clear message:

  * "Unable to generate a response. Try simplifying the input."
* Do not blame the user
* Do not expose model internals

---

## 10. Model Update UX

### When Update Is Available

* Passive notification:

  * "Improved offline model available"

### Update Screen

* Show:

  * What improved (brief)
  * Model size
* User chooses:

  * Update now
  * Later

Updates are never forced.

---

## 11. Settings Screen (Minimal)

Settings include:

* Current model info
* Delete / replace model
* Download over Wi-Fi only
* Reset app data

No excessive toggles.

---

## 12. Accessibility & Performance

* Large tap targets
* Dark mode support
* Avoid heavy animations
* Keep screens responsive during inference

---

## 13. Out-of-Scope UX (v1)

* Account system
* Cloud sync
* Code execution
* Project/file management
* Social features

---

## 14. UX Success Criteria

* User can get help in < 3 taps
* No confusion about offline state
* Answers persist across app restarts
* Users can safely cancel inference at any time
* UI stays out of the way

---

## 15. Change Log

**v1.1**

* Added visible OCR input affordance (Coming Soon)
* Added explicit inference Cancel control
* Added mandatory state persistence across backgrounding and OS kills
