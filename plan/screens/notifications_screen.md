# Notifications List

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `NotificationsScreen`
- **Flow Position**: Accessed from bell icon on Home.
- **Route & Arguments**: `Route.Notifications`
- **Access & Permissions**: Authenticated users.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` container. Uses `EdgeToEdge` padding.
- **Component Placement & Spacing**: Standard 16dp horizontal padding. Elements spaced vertically by 8dp and 16dp.
- **Colors & Styling**: Deep Charcoal (`#1E1E24`) background. Surface cards in Slate Grey (`#2B2B36`). Primary actions in Vibrant Cyan (`#4CE5E5`).
- **Light & Dark Mode Adaptations**: Dark mode strictly enforced per Design System.
- **Micro-Interactions**: Hover/press animations on interactive elements.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `GET /api/v1/notification/api/v1/notifications`
- **Request/Response**: Utilizes standard DTO mapping.
- **Error Handling**: Displays `Snackbar` on network failure. Shimmer effect during data fetch.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class NotificationsScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface NotificationsScreenUiEvent {
    object LoadData : NotificationsScreenUiEvent
}

@HiltViewModel
class NotificationsScreenViewModel @Inject constructor(
    private val repository: DefaultRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsScreenUiState())
    val state = _state.asStateFlow()
    
    // Logic handles data loading, validation, and API execution.
}
```

## 5. Jetpack Compose UI Code (Kotlin)
```kotlin
@Composable
fun NotificationsScreen(
    viewModel: NotificationsScreenViewModel = hiltViewModel(),
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
> UI Design of a Notifications screen. Dark mode. A list of notification items: 'Your application was accepted!', 'Emergency stray reported nearby'. Unread items have a cyan background tint.
