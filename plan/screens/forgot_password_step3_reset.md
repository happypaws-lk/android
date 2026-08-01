# Forgot Password Flow - Step 3: Set New Password

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `ForgotPasswordResetScreen`
- **Flow Position**: Step 3 of the Forgot Password flow.
- **Route & Arguments**: `Route.ForgotPasswordReset(val email: String, val otpToken: String)`
- **Access & Permissions**: Public.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` container. Uses `EdgeToEdge` padding.
- **Component Placement & Spacing**: Standard 16dp horizontal padding. Elements spaced vertically by 8dp and 16dp.
- **Colors & Styling**: Deep Charcoal (`#1E1E24`) background. Surface cards in Slate Grey (`#2B2B36`). Primary actions in Vibrant Cyan (`#4CE5E5`).
- **Light & Dark Mode Adaptations**: Dark mode strictly enforced per Design System.
- **Micro-Interactions**: Hover/press animations on interactive elements.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `POST /api/v1/auth/reset-password`
- **Request/Response**: Utilizes standard DTO mapping.
- **Error Handling**: Displays `Snackbar` on network failure. Shimmer effect during data fetch.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class ForgotPasswordResetScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ForgotPasswordResetScreenUiEvent {
    object LoadData : ForgotPasswordResetScreenUiEvent
}

@HiltViewModel
class ForgotPasswordResetScreenViewModel @Inject constructor(
    private val repository: DefaultRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordResetScreenUiState())
    val state = _state.asStateFlow()
    
    // Logic handles data loading, validation, and API execution.
}
```

## 5. Jetpack Compose UI Code (Kotlin)
```kotlin
@Composable
fun ForgotPasswordResetScreen(
    viewModel: ForgotPasswordResetScreenViewModel = hiltViewModel(),
    onNavigate: (Route) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        containerColor = Color(0xFF1E1E24)
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            // Screen content rendered based on state
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF4CE5E5))
            }
        }
    }
}
```

## 6. AI UI Design Generation Prompt
> UI Design of a Set New Password screen for a dark-mode app. Background charcoal (#1E1E24). Bold white text 'Set a new password'. Two input fields 'Password' and 'Confirm Password' filled with slate grey (#2B2B36). Cyan button 'Update Password' at the bottom.
