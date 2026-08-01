# Forgot Password Flow - Step 1: Email

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `ForgotPasswordEmailScreen`
- **Flow Position**: Step 1 of the Forgot Password flow.
- **Route & Arguments**: `Route.ForgotPasswordEmail(val initialEmail: String?)`
- **Access & Permissions**: Public.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` with TopAppBar (Back arrow). `Column` container.
- **Component Placement & Spacing**:
  - Top: Back button in TopBar.
  - Top-center: Logo.
  - Middle: Headline "Forgot password?", Subheadline "Please enter your email to reset the password".
  - Middle: "Your Email" Input Field.
  - Bottom: "Continue" Primary Button (Cyan).
- **Colors & Styling**: Background `#1E1E24`, Input `#2B2B36`. Text white and cool grey.
- **Micro-Interactions**: Button click triggers loading state. Transition slides left to the OTP screen upon success.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `POST /api/v1/auth/otp/send`
- **Request Payload**: `{"email": "user@example.com", "purpose": "password_reset"}`
- **Response**: `200 OK` (Empty or basic success message).
- **Error Handling**: Show error if email is not found, but standard security practice recommends showing success even if not found to prevent email enumeration.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class ForgotPasswordEmailUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ForgotPasswordEmailUiEvent {
    data class EmailChanged(val value: String) : ForgotPasswordEmailUiEvent
    object Submit : ForgotPasswordEmailUiEvent
}

@HiltViewModel
class ForgotPasswordEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordEmailUiState())
    val state = _state.asStateFlow()
    
    // ... logic for submitting email to API and emitting navigation effect to OTP screen
}
```

## 5. Jetpack Compose UI Code (Kotlin)
*(Similar structure to Login, using `Scaffold` and `TopAppBar` for back navigation, single input field)*

## 6. AI UI Design Generation Prompt
> UI Design of a "Forgot Password" screen (Step 1) for a dark-mode Android app. Background is dark charcoal (#1E1E24). A top navigation bar has a simple back chevron icon. Center-top features a cyan logo. Below is white bold text "Forgot password?" and cool grey subtext "Please enter your email to reset the password". Below is a slate grey (#2B2B36) text field labeled "Your Email" containing "you@example.com". A vibrant cyan (#4CE5E5) wide button says "Continue" at the bottom. Minimalist, geometric, clean.
