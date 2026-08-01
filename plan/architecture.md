# Technical Architecture Specification

## 1. Clean Architecture Layers

The app strictly follows Clean Architecture principles, divided into three layers: Data, Domain, and UI.

### 1.1 Data Layer
- **Responsibilities**: Network API calls, local database caching, data mapping.
- **Components**: 
  - `Repositories`: Implement domain interfaces, handle source of truth logic.
  - `NetworkDataSource`: Uses Retrofit/Ktor for API interactions. Maps OpenAPI DTOs to Domain Models.
  - `LocalDataSource`: Uses Room Database for offline caching (especially important for Rescue Radar maps and draft reports). DataStore for JWT tokens and user preferences.
- **Libraries**: Ktor/Retrofit, Room, DataStore, Kotlinx Serialization.

### 1.2 Domain Layer
- **Responsibilities**: Business logic, use cases, domain models. This layer contains no Android framework dependencies.
- **Components**:
  - `Domain Models`: Pure Kotlin data classes (e.g., `User`, `RescueCase`, `PetListing`).
  - `Use Cases (Interactors)`: Single-responsibility classes like `ReportStrayUseCase`, `VerifyOtpUseCase`.
  - `Repository Interfaces`: Defined here, implemented in the Data layer.

### 1.3 UI Layer (Presentation)
- **Responsibilities**: Managing UI state, reacting to user intents, rendering Jetpack Compose UI.
- **Pattern**: MVI / MVVM with unidirectional data flow.
- **Components**:
  - `ViewModel`: Exposes `StateFlow<ScreenUiState>` for the UI to consume. Handles user actions via `onEvent(event: ScreenUiEvent)`. Emits one-off events (like navigation or toasts) via `SharedFlow<ScreenEffect>`.
  - `Compose Screens`: Stateless composables that observe `UiState` and emit `UiEvent`.

## 2. Networking & API Integration

- **Base Setup**: All network requests pass through an Interceptor that attaches the JWT token from DataStore.
- **Auth Token Refresh**: A dedicated `Authenticator` intercepts `401 Unauthorized` responses and automatically calls `/api/v1/auth/refresh` to get a new JWT, saving it to DataStore and retrying the failed request seamlessly.
- **File Uploads (KYC & Photos)**: Handled via MinIO. The API returns a pre-signed URL; the Android client uses `OkHttp` or `Ktor` to `PUT` the multipart/form-data payload directly to the storage bucket.
- **Error Mapping**: Network errors are mapped to a sealed class `Result<T>` containing `Success`, `Error(message)`, or `NetworkException`.

## 3. Dependency Injection
- **Framework**: Hilt is used for constructor injection.
- **Modules**:
  - `NetworkModule`: Provides Ktor/Retrofit instances, interceptors, API service definitions.
  - `DataModule`: Binds Repository implementations to Domain interfaces.
  - `DatabaseModule`: Provides Room database instance and DAOs.
  - `UseCaseModule`: Provides Domain UseCases.

## 4. Offline & Caching Strategy
- **Draft Syncing**: Rescue reports created while offline are saved to a Room database table `pending_reports`. A background `WorkManager` worker attempts to sync these to the `/api/v1/rescues` endpoint when network connectivity is restored.
- **Active Case Caching**: The most recent active cases for the map are cached in Room so the map can render immediately on launch before the network fetch completes.
