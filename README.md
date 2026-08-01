<a href="https://github.com/happypaws-lk/happypaws-android" align="center">
    <img src=".github/assets/banner.jpg" alt="HappyPaws Android App">
</a>

<p align="center">The official native Android application for HappyPaws.lk.</p>

<!-- Badges -->
<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=flat&logo=kotlin&labelColor=171717" alt="Kotlin 2.2" />
  <img src="https://img.shields.io/badge/Android-SDK_37-3DDC84?style=flat&logo=android&labelColor=171717" alt="Android SDK 37" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=flat&logo=jetpackcompose&labelColor=171717" alt="Jetpack Compose Material 3" />
  <img src="https://img.shields.io/badge/Gradle-9.2-02303A?style=flat&logo=gradle&labelColor=171717" alt="Gradle 9.2" />
  <img src="https://img.shields.io/badge/License-Proprietary-c03dfe?style=flat&labelColor=171717" alt="License" />
</p>

<h4 align="center">
    <a href="#introduction">Introduction</a> 
    <span> · </span>    
    <a href="#features">Features</a>
    <span> · </span>
    <a href="#getting-started">Getting Started</a>
    <span> · </span>
    <a href="#tech-stack">Tech Stack</a>
    <span> · </span>
    <a href="#architecture">Architecture</a>
    <span> · </span>
    <a href="#building--testing">Building & Testing</a>
    <span> · </span>
    <a href="#license">License</a>
</h4>

<br />

## Introduction

HappyPaws.lk is a verified-identity and reputation-led platform for animal rescue and rehoming in Sri Lanka. This repository contains the **HappyPaws Android Application**, providing a native mobile experience for users, rescuers, and animal lovers across Sri Lanka.

The app enables real-time reporting of injured or stray animals, seamless adoption workflows, verified KYC identity management, and direct community engagement.

## Tech Stack

- **Language:** [Kotlin 2.2](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
- **SDK Target:** Android SDK 37 (Minimum SDK 26)
- **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`) & Version Catalogs (`libs.versions.toml`)
- **Core Libraries:**
  - `androidx.core:core-ktx`
  - `androidx.lifecycle:lifecycle-runtime-ktx`
  - `androidx.activity:activity-compose`
  - `androidx.core:core-splashscreen`

## Getting Started

### Prerequisites

Ensure you have the following installed on your development machine:
- **Android Studio** (Ladybug / Jellyfish or newer recommended)
- **JDK 17** or **JDK 21**
- **Android SDK 37** platform tools

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/happypaws-lk/happypaws-android.git
   cd happypaws-android
   ```

2. **Open in Android Studio:**
   - Launch Android Studio.
   - Select **Open** and choose the `android/` directory.
   - Wait for Gradle sync to complete automatically.

3. **Configure Local Environment:**
   Create or verify `local.properties` in the project root with your local Android SDK location:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   ```

4. **Build & Run:**
   - Select your target emulator or connected physical Android device.
   - Click **Run 'app'** or execute via CLI:
     ```bash
     ./gradlew assembleDebug
     ```

## Building & Testing

Run the following Gradle commands from the project root:

- **Run Unit Tests:**
  ```bash
  ./gradlew test
  ```

- **Build Debug APK:**
  ```bash
  ./gradlew assembleDebug
  ```

- **Build Release APK:**
  ```bash
  ./gradlew assembleRelease
  ```

- **Run Lint Checks:**
  ```bash
  ./gradlew lint
  ```

## License

Proprietary. All rights reserved by HappyPaws.lk.
