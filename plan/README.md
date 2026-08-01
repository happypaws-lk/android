# HappyPaws Android App - Master Implementation Roadmap

## 1. Executive Summary
The HappyPaws Android Application is a native client built using Kotlin and Jetpack Compose. It serves as the primary public interface for animal rescue, rehoming, and sponsorship in Sri Lanka. The application relies on a verified-identity model and connects to the `.NET 8` ASP.NET Core API backend.

## 2. Core Architecture
- **UI Toolkit**: Jetpack Compose (Material 3 adapted to custom Design System).
- **Architecture Pattern**: Clean Architecture with MVI (Model-View-Intent) in the UI Layer.
- **Concurrency**: Kotlin Coroutines & Flow (`StateFlow` for state, `SharedFlow` for one-shot events).
- **Dependency Injection**: Hilt.
- **Networking**: Ktor Client or Retrofit with Kotlinx Serialization.
- **Local Storage**: Room Database for offline support (drafting rescue reports, caching active cases), DataStore for user preferences and Auth tokens.
- **Navigation**: Navigation Compose with `kotlinx.serialization` for type-safe arguments.

## 3. Package Structure
```
com.happypaws.android
├── app                 # App-level entry point, DI setup, Navigation Graph
├── core
│   ├── data            # Repositories, Room DB, Ktor Client
│   ├── domain          # Use cases, domain models
│   ├── designsystem    # Theme, Colors, Typography, Reusable Composables
│   └── network         # API DTOs, Interceptors, Error Handling
└── feature
    ├── auth            # Login, Registration, Password Reset flows
    ├── verification    # KYC upload and status
    ├── rescue          # Stray reporting, Triage, Case details
    ├── adoption        # Catalog, Listing, Matching Quiz, Applications
    ├── chat            # Private messaging
    └── profile         # User profile, Badges, Sponsorships
```

## 4. Screen Index (Feature Domains)

### Auth & Onboarding
- `splash_screen.md`
- `login_screen.md`
- `register_step1_role_selection.md`
- `register_step2_email_password.md`
- `kyc_upload_screen.md`
- `forgot_password_step1_email.md`
- `forgot_password_step2_otp.md`
- `forgot_password_step3_reset.md`
- `forgot_password_step4_success.md`

### Rescue & Triage
- `home_feed_screen.md`
- `report_stray_step1_location_photo.md`
- `report_stray_step2_ai_triage.md`
- `report_stray_step3_confirmation.md`
- `rescue_case_detail_screen.md`

### Adoption & Rehoming
- `adoption_catalog_screen.md`
- `pet_listing_detail_screen.md`
- `matching_quiz_screen.md`
- `adoption_application_screen.md`
- `create_listing_screen.md`

### Messaging & User Management
- `chat_list_screen.md`
- `chat_conversation_screen.md`
- `profile_screen.md`
- `sponsorship_case_list.md`
- `notifications_screen.md`

## 5. Implementation Milestones
- **Sprint 1**: Setup project, Core DI, Design System components, and Auth Flow (Login/Register/KYC).
- **Sprint 2**: Navigation Graph, Rescue Radar features (Home Map, Reporting Flow, AI Triage integration).
- **Sprint 3**: Adoption Hub (Catalog, Matching, Listing details) and Profiles (Badges).
- **Sprint 4**: Real-time Messaging (SignalR / WebSockets) and Notifications (FCM).
