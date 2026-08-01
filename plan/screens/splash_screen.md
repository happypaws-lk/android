# Splash Screen

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `SplashScreen`
- **Flow Position**: Entry point of the application.
- **Route & Arguments**: `Route.Splash` (No arguments).
- **Access & Permissions**: Public. No permissions required.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: Full-screen `Box` with content centered. `EdgeToEdge` enabled.
- **Component Placement & Spacing**: 
  - Center: Animated HappyPaws logo (`ic_splash_logo_animated.svg` Lottie animation).
  - Bottom (padding 32dp): Loading indicator (if doing a long network check) or version number text (`12sp`, `#A0A0AB`).
- **Colors & Styling**: Background is `Deep Charcoal` (`#1E1E24`). Logo uses `Vibrant Cyan` (`#4CE5E5`).
- **Light & Dark Mode Adaptations**: Dark mode only.
- **Micro-Interactions & Animations**: The logo fades in and slightly scales up (spring animation). Once DataStore resolves the auth token (valid/invalid), it crossfades to the next route.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: Indirectly checks `/api/v1/users/me` if a token exists to verify validity.
- **Request & Response Payload**: 
  - Request: `GET /api/v1/users/me` (Bearer JWT)
- **Error & Loading States**: If token is invalid or network fails, fallback to `Login` screen.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class SplashUiState(val isLoading: Boolean = true)
sealed interface SplashUiEvent { object AnimationFinished : SplashUiEvent }
sealed interface SplashEffect { object NavigateToHome : SplashEffect; object NavigateToLogin : SplashEffect }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _effect = MutableSharedFlow<SplashEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SplashUiEvent) {
        when (event) {
            is SplashUiEvent.AnimationFinished -> checkAuthStatus()
        }
    }

    private fun checkAuthStatus() = viewModelScope.launch {
        if (authRepository.isTokenValid()) {
            _effect.emit(SplashEffect.NavigateToHome)
        } else {
            _effect.emit(SplashEffect.NavigateToLogin)
        }
    }
}
```

## 5. Jetpack Compose UI Code (Kotlin)
```kotlin
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigate: (Route) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> onNavigate(Route.HomeMap)
                is SplashEffect.NavigateToLogin -> onNavigate(Route.Login)
            }
        }
    }

    var startAnimation by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "SplashScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1500) // Minimum splash duration for branding
        viewModel.onEvent(SplashUiEvent.AnimationFinished)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E24)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "HappyPaws Logo",
            modifier = Modifier.scale(scale.value).size(120.dp)
        )
    }
}
```

## 6. AI UI Design Generation Prompt
> Generate a minimalist, modern Android splash screen for a pet rescue app named HappyPaws. The background is a solid deep charcoal (#1E1E24). In the exact center, place a sleek, modern vector logo of a dog and cat silhouette intertwined in a heart shape, colored in vibrant cyan (#4CE5E5). The logo should look slightly illuminated. At the very bottom center, put tiny, discreet text reading 'v1.0' in cool grey (#A0A0AB). The overall aesthetic is dark mode, premium, clean, and highly functional. No extra UI elements.
