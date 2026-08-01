# Lifestyle Matching Quiz

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `MatchingQuizScreen`
- **Flow Position**: Wizard to build adopter profile.
- **Route & Arguments**: `Route.MatchingQuiz`
- **Access & Permissions**: Authenticated Adopters.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` container. Uses `EdgeToEdge` padding.
- **Component Placement & Spacing**: Standard 16dp horizontal padding. Elements spaced vertically by 8dp and 16dp.
- **Colors & Styling**: Deep Charcoal (`#1E1E24`) background. Surface cards in Slate Grey (`#2B2B36`). Primary actions in Vibrant Cyan (`#4CE5E5`).
- **Light & Dark Mode Adaptations**: Dark mode strictly enforced per Design System.
- **Micro-Interactions**: Hover/press animations on interactive elements.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `PUT /api/v1/users/me/profile`
- **Request/Response**: Utilizes standard DTO mapping.
- **Error Handling**: Displays `Snackbar` on network failure. Shimmer effect during data fetch.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class MatchingQuizScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface MatchingQuizScreenUiEvent {
    object LoadData : MatchingQuizScreenUiEvent
}

@HiltViewModel
class MatchingQuizScreenViewModel @Inject constructor(
    private val repository: DefaultRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MatchingQuizScreenUiState())
    val state = _state.asStateFlow()
    
    // Logic handles data loading, validation, and API execution.
}
```

## 5. Jetpack Compose UI Code (Kotlin)
```kotlin
@Composable
fun MatchingQuizScreen(
    viewModel: MatchingQuizScreenViewModel = hiltViewModel(),
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
> UI Design of a Lifestyle Matching quiz. Dark mode. Question: 'What is your home size?'. Three large selectable cards: 'Apartment', 'House without yard', 'House with yard'. A cyan progress bar at the top.
