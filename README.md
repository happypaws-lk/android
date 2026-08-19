<a href="https://github.com/happypaws-lk/happypaws-android" align="center">
    <img src=".github/assets/banner.jpg" alt="HappyPaws Android App">
</a>

<p align="center">The official native Android application for HappyPaws.lk.</p>

<!-- Badges -->
<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.4-7F52FF?style=flat&logo=kotlin&labelColor=171717" alt="Kotlin 2.4" />
  <img src="https://img.shields.io/badge/Android-SDK_37-3DDC84?style=flat&logo=android&labelColor=171717" alt="Android SDK 37" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=flat&logo=jetpackcompose&labelColor=171717" alt="Jetpack Compose Material 3" />
  <img src="https://img.shields.io/badge/Gradle-9.6-02303A?style=flat&logo=gradle&labelColor=171717" alt="Gradle 9.6" />
  <img src="https://img.shields.io/badge/License-Proprietary-c03dfe?style=flat&labelColor=171717" alt="License" />
</p>

<h4 align="center">
    <a href="#introduction">Introduction</a> 
    <span> · </span>    
    <a href="#download-the-app">Download</a>
    <span> · </span>
    <a href="#getting-started">Getting started</a>
    <span> · </span>
    <a href="#tech-stack">Tech stack</a>
    <span> · </span>
    <a href="#building-and-testing">Building and testing</a>
    <span> · </span>
    <a href="#release-pipeline">Release pipeline</a>
    <span> · </span>
    <a href="#license">License</a>
</h4>

<br />

## Introduction

HappyPaws.lk is a verified-identity and reputation-led platform for animal rescue and rehoming in Sri Lanka. This repository contains the native Android application for rescuers, adopters, fosters, and veterinarians across Sri Lanka.

The app supports emergency rescue reporting, adoption management, verified KYC identity checkups, and direct community messaging.

## Download the app

You can install the latest production build directly on any Android device running Android 8.0 (API 26) or newer.

- **Download website:** [happypaws-lk.github.io/happypaws-android](https://happypaws-lk.github.io/happypaws-android/)
- **GitHub releases:** [github.com/happypaws-lk/happypaws-android/releases](https://github.com/happypaws-lk/happypaws-android/releases)

### How to install on your device

1. Visit the [download website](https://happypaws-lk.github.io/happypaws-android/) or open [GitHub releases](https://github.com/happypaws-lk/happypaws-android/releases) on your phone.
2. Tap **Download Android APK** to download the latest signed `HappyPaws-v*.apk` package.
3. If prompted by Android, open **Settings** and turn on **Allow from this source** for your browser or file manager.
4. Tap the downloaded file to complete installation and open HappyPaws.

## Tech stack

- **Language:** [Kotlin 2.4](https://kotlinlang.org/)
- **UI framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
- **Target SDK:** Android SDK 37 (Minimum SDK 26)
- **Dependency injection:** [Hilt](https://dagger.dev/hilt/)
- **Networking:** [Retrofit 2](https://square.github.io/retrofit/) and [OkHttp 4](https://square.github.io/okhttp/)
- **Local storage:** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Build system:** Gradle 9.6 with Android Gradle Plugin 9.3 and Version Catalogs (`libs.versions.toml`)

## Getting started

### Prerequisites

Make sure you have these tools installed:
- **Android Studio** (Ladybug or newer)
- **JDK 17** or **JDK 21**
- **Android SDK 37** platform tools

### Setup instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/happypaws-lk/happypaws-android.git
   cd happypaws-android
   ```

2. Open the project:
   - Start Android Studio.
   - Select **Open** and choose the `android/` folder.
   - Wait for Gradle sync to finish.

3. Build and run:
   - Pick an emulator or plug in an Android device with USB debugging enabled.
   - Press **Run 'app'** in Android Studio or run from your terminal:
     ```bash
     ./gradlew assembleDebug
     ```

## Building and testing

Run these Gradle commands from the `android/` folder:

- **Run unit tests:**
  ```bash
  ./gradlew test
  ```

- **Build debug APK:**
  ```bash
  ./gradlew assembleDebug
  ```

- **Build signed release APK:**
  ```bash
  ./gradlew assembleRelease
  ```

- **Run Android lint:**
  ```bash
  ./gradlew lint
  ```

## Release pipeline

The repository uses GitHub Actions for automated production releases:

- **Pre-flight health check:** The release workflow pings your Lightsail container API `/health` endpoint before compiling.
- **Automated signing:** Builds and signs the APK using keystore credentials stored in GitHub Secrets.
- **GitHub releases:** Publishes the signed `HappyPaws-v*.apk` artifact automatically when you push a version tag (such as `v1.0.0`) or trigger the workflow manually.

### Required GitHub secrets

To enable the release workflow, add these secrets under **Settings -> Secrets and variables -> Actions** in your GitHub repository:

| Secret name | Purpose | Example / format |
| :--- | :--- | :--- |
| `API_BASE_URL` | Lightsail container base URL | `https://api.happypaws.lk/` |
| `KEYSTORE_BASE64` | Base64-encoded release `.jks` file | Base64 string |
| `KEYSTORE_PASSWORD` | Keystore password | Secret password |
| `KEY_ALIAS` | Key alias in the keystore | `happypaws-key` |
| `KEY_PASSWORD` | Password for the key alias | Secret password |

To generate the `KEYSTORE_BASE64` string from your `.jks` file, run:

- **macOS / Linux:**
  ```bash
  base64 -i release.keystore | tr -d '\n'
  ```

- **Windows (PowerShell):**
  ```powershell
  [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore"))
  ```

## License

Proprietary. All rights reserved by HappyPaws.lk.
