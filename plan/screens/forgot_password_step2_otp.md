# Forgot Password Flow - Step 2: OTP Verification

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `ForgotPasswordOtpScreen`
- **Flow Position**: Step 2 of the Forgot Password flow.
- **Route & Arguments**: `Route.ForgotPasswordOtp(val email: String)`
- **Access & Permissions**: Public.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` with TopAppBar. Content in `Column`.
- **Component Placement & Spacing**:
  - Headline: "Check your email"
  - Subheadline: "We sent a reset link to contact@domain.com, enter 5 digit code that mentioned in the email."
  - Middle: OTP Input Row (5 distinct square blocks).
  - Middle-bottom: "Verify Code" Button.
  - Bottom footer: "Haven't got the email yet? Resend email"
- **Colors & Styling**: OTP boxes are `#2B2B36` with `#3A3A4A` borders. Active box border is `#4CE5E5`.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `POST /api/v1/auth/otp/verify`
- **Request Payload**: `{"email": "...", "code": "863..."}`
- **Response**: `{"otpToken": "temp_token_for_reset"}`

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class ForgotPasswordOtpUiState(
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

// ViewModel logic parses the OTP string, ensures 5 digits, calls API.
```

## 5. Jetpack Compose UI Code (Kotlin)
*(Compose UI using a custom `OtpTextField` that draws 5 rounded squares `Box` items side-by-side, intercepting a hidden `BasicTextField`)*

## 6. AI UI Design Generation Prompt
> UI Design of an OTP Verification screen for a dark-mode app. Background charcoal (#1E1E24). Bold white text "Check your email". Below it, cool grey text explaining a 5-digit code was sent. The main UI element is a row of 5 rounded square input boxes filled with slate grey (#2B2B36). The first three boxes have digits "8", "6", "3" in white, the others are empty. A vibrant cyan button "Verify Code" is below the squares. A "Resend email" link is at the bottom. Premium dark theme.
