# DrinkWise

**Empowering Youth, Managing Habits, Accessing Support.**

DrinkWise is an privacy-centric, non-judgmental Android application developed in partnership with the **Canadian Alcohol Use Disorder Society (CAUDS)**. It is specifically designed to help youth aged 19–24 identify early signs of Alcohol Use Disorder (AUD) and provide them with the tools and resources needed for proactive self-management.

---

## Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Declarative & Modern)
*   **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
*   **Backend:** Firebase Firestore (Cloud Sync) & Firebase Authentication
*   **Concurrency:** Kotlin Coroutines (for non-blocking UI and data sync)
*   **Security:** Per-user data siloing via strictly enforced Firestore Security Rules

---

## Key Features

### Drink Tracker (Core)
*   **High-Speed Logging:** Swiftly record daily consumption by beverage type, container size, and quantity.
*   **Financial Tracking:** Monitor exactly how much you spend on drinks to see the economic impact.
*   **Data Auditing:** Easily edit, duplicate, or batch-delete historical entries for accurate tracking.

### Insights & Calendar
*   **Heatmap Visualization:** A monthly/weekly view using color-coded dots to represent drinking intensity and spending trends.
*   **Pattern Recognition:** Identify triggers or reward-driven behaviors over time.

### Reflective Journal
*   **Emotional Context:** Record your mood, social settings, and experiences tied to each drinking session.
*   **Behavioral Observation:** Allows users to track emotional changes over time through swipeable journal cards.

### Learning Hub
*   **Knowledge Base:** A modular library of articles regarding AUD symptoms, health facts, and "how to get help" resources.

### AUD Self-Assessment
*   **Clinical Screening:** An interactive quiz evaluating responses against clinical logic to determine risk levels (Low to High).
*   **Supportive Feedback:** Instant baseline risk assessment with non-judgmental guidance.

---

## Try it Out (Demo APK)

*   **Download**: You can find the latest stable version of the app in the **[Releases](https://github.com/SChen2004/DrinkWise/tree/main/app/release)** section of this repository.
*   **Installation**:
    1. Download the `.apk` file directly to your Android device.
    2. Open your device's File Manager and locate the downloaded file.
    3. Tap the file to install. If prompted, allow "Install from Unknown Sources" in your Android settings.


