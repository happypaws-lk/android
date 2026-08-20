# HappyPaws Android instructions

Read [AGENTS.md](AGENTS.md) for all Kotlin coding conventions, architecture guidelines, Jetpack Compose standards, API integration workflows, Conventional Commits rules, and documentation writing styles. Those rules apply to every response.

## Domain context

Check the relevant local guides before implementing or modifying screens and workflows:

| Area | File to read |
|---|---|
| Design constraints, typography, and color palette | `DESIGN.md` |
| Feature phases and dependency-driven milestones | `IMPLEMENTATION_ROADMAP.md` |
| Backend API specification | `app/openapi.v1.json` |
| Syncing API specification from backend | `sync-spec.ps1` |

Read only what you need for the current task.

## Skill references

These skills live in `.agents/skills/` and contain domain patterns, best practices, and code examples. Read the relevant `SKILL.md` before implementing the task it covers.

### `adaptive` (`.agents/skills/adaptive/SKILL.md`)
Load this when building or updating UI for diverse Android form factors (phones, foldables, tablets, laptops, desktop, TV, Auto, and XR). Covers the Compose MediaQuery API, multi-pane layouts with Navigation 3 Scenes (such as list-detail), and adaptive layouts with Compose Grid and FlexBox APIs.
Trigger keywords: "adaptive", "foldable", "tablet", "multi-pane", "list-detail", "MediaQuery", "FlexBox", "window size", "screen size".

### `agp-9-upgrade` (`.agents/skills/agp-9-upgrade/SKILL.md`)
Load this when upgrading or migrating build scripts to Android Gradle Plugin (AGP) version 9. Covers built-in Kotlin support, KSP and kapt migration, and Paparazzi compatibility.
Trigger keywords: "AGP 9", "Android Gradle Plugin", "upgrade AGP", "gradle upgrade", "build.gradle.kts".

### `android-cli` (`.agents/skills/android-cli/SKILL.md`)
Load this when using the `android` command-line tool. Covers managing virtual devices, running apps on emulators or hardware, inspecting UI hierarchies, taking screenshots, and managing SDK components.
Trigger keywords: "android CLI", "emulator", "AVD", "screenshot", "inspect UI", "run app", "device".

### `android-intent-security` (`.agents/skills/android-intent-security/SKILL.md`)
Load this when configuring or auditing component declarations in `AndroidManifest.xml` (activities, services, and receivers) and handling incoming Intents (`getIntent`, `getParcelableExtra`) to prevent Intent Redirection.
Trigger keywords: "intent", "Intent Redirection", "AndroidManifest.xml", "exported", "pending intent", "security audit".

### `appfunctions` (`.agents/skills/appfunctions/SKILL.md`)
Load this when exposing key workflows (such as reporting a rescue or submitting an adoption application) to system shortcuts and AI agents without opening the full app UI, including KDoc documentation refinement.
Trigger keywords: "AppFunctions", "shortcuts", "voice commands", "system actions", "agent triggers".

### `camerax` (`.agents/skills/camerax/SKILL.md`)
Load this when working with camera capture flows (such as KYC document photos and rescue triage reporting). Covers CameraX lifecycle binding, asynchronous capture, and ML Kit integration.
Trigger keywords: "camera", "CameraX", "take photo", "image capture", "ML Kit", "video recording", "KYC upload".

### `display-glasses-with-jetpack-compose-glimmer` (`.agents/skills/display-glasses-with-jetpack-compose-glimmer/SKILL.md`)
Load this when developing projected Android XR augmented reality experiences for display glasses using the Jetpack Compose Glimmer UI toolkit.
Trigger keywords: "XR", "display glasses", "Glimmer", "smart glasses", "augmented reality".

### `edge-to-edge` (`.agents/skills/edge-to-edge/SKILL.md`)
Load this when configuring edge-to-edge layouts in Jetpack Compose. Covers window insets, status and navigation bar padding, IME (keyboard) insets, and system bar contrast.
Trigger keywords: "edge to edge", "insets", "status bar", "navigation bar", "IME", "keyboard overlap", "system bars".

### `engage-sdk-integration` (`.agents/skills/engage-sdk-integration/SKILL.md`)
Load this when integrating the Google Play Engage SDK. Covers publishing recommendations, animal listing clusters, and featured items to Google Play surfaces.
Trigger keywords: "Play Engage", "Engage SDK", "recommendation cluster", "Play surface".

### `frontend-design` (`.agents/skills/frontend-design/SKILL.md`)
Load this when creating or polishing visual design, typography, color balance, and layouts for screens to deliver distinctive interfaces.
Trigger keywords: "frontend design", "UI design", "visual polish", "aesthetics", "layout styling".

### `migrate-xml-views-to-jetpack-compose` (`.agents/skills/migrate-xml-views-to-jetpack-compose/SKILL.md`)
Load this when converting legacy XML View layouts or custom views into modern Jetpack Compose composables.
Trigger keywords: "migrate XML", "convert View to Compose", "AndroidView", "ViewBinding", "legacy UI".

### `navigation-3` (`.agents/skills/navigation-3/SKILL.md`)
Load this when implementing navigation flows with Jetpack Navigation 3. Covers type-safe routes, multiple backstacks, scenes (bottom sheets, dialogs, two-pane layouts), conditional auth routing, and ViewModel scoping.
Trigger keywords: "navigation", "Navigation 3", "nav host", "backstack", "routes", "deep link", "bottom sheet scene".

### `perfetto-sql` (`.agents/skills/perfetto-sql/SKILL.md`)
Load this when querying Perfetto trace files with SQL via `trace_processor` to extract slice durations, thread states, and memory data.
Trigger keywords: "Perfetto SQL", "trace_processor", "query trace", "perfetto query".

### `perfetto-trace-analysis` (`.agents/skills/perfetto-trace-analysis/SKILL.md`)
Load this when diagnosing performance problems from Perfetto trace files, such as UI jank, dropped frames, app startup delays, and memory spikes.
Trigger keywords: "perfetto", "trace analysis", "jank", "frame drop", "app startup latency", "profiling".

### `play-billing-library-version-upgrade` (`.agents/skills/play-billing-library-version-upgrade/SKILL.md`)
Load this when upgrading or migrating Google Play Billing Library (PBL) integrations to the latest release.
Trigger keywords: "Play Billing", "billing library", "in-app purchase", "subscriptions", "PBL upgrade".

### `play-policy-insights` (`.agents/skills/play-policy-insights/SKILL.md`)
Load this when auditing the app for Google Play Policy compliance. Covers permissions hygiene, background location rules, data safety disclosures, and privacy declarations.
Trigger keywords: "Play Policy", "Google Play compliance", "data safety", "permissions audit", "privacy policy".

### `r8-analyzer` (`.agents/skills/r8-analyzer/SKILL.md`)
Load this when optimizing Proguard and R8 keep rules, reducing APK or AAB download size, and troubleshooting minification issues.
Trigger keywords: "R8", "Proguard", "keep rules", "shrink code", "APK size", "minification".

### `styles` (`.agents/skills/styles/SKILL.md`)
Load this when using the Jetpack Compose Styles API. Covers creating styleable custom components, unified component themes, and managing interaction states with `Modifier.styleable`.
Trigger keywords: "styles API", "Compose styles", "styleable", "component theme", "design system components".

### `testing-setup` (`.agents/skills/testing-setup/SKILL.md`)
Load this when setting up or expanding Android testing. Covers unit testing with JUnit and MockK, Jetpack Compose UI tests, screenshot testing with Paparazzi or Robolectric, and end-to-end test infrastructure.
Trigger keywords: "testing", "unit test", "Compose test", "screenshot test", "Paparazzi", "Robolectric", "test setup".

### `verified-email` (`.agents/skills/verified-email/SKILL.md`)
Load this when integrating Google verified email retrieval using the Android Credential Manager API for one-tap, OTP-less registration and authentication.
Trigger keywords: "verified email", "Credential Manager", "Google Sign In", "OTP-less login", "auth credentials".

### `wear-compose-m3` (`.agents/skills/wear-compose-m3/SKILL.md`)
Load this when building Wear OS companion screens with Wear Compose Material 3 (`androidx.wear.compose.material3`). Covers AppScaffold, ScreenScaffold, TransformingLazyColumn, and ambient mode.
Trigger keywords: "Wear OS", "smartwatch", "wear compose", "TransformingLazyColumn", "ambient mode".
