# Login Screen

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `LoginScreen`
- **Flow Position**: Auth entry point. Appears after Splash if unauthenticated, or after Logout.
- **Route & Arguments**: `Route.Login` (No arguments).
- **Access & Permissions**: Public.

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` with `EdgeToEdge`. Content inside a `Column` centered vertically with a `ScrollState` to handle keyboard overlap.
- **Component Placement & Spacing**:
  - Top (64dp margin): HappyPaws Logo and Title (Cyan).
  - Center: `Email` Input Field (16dp bottom spacing). `Password` Input Field (Trailing icon for visibility toggle).
  - Right-aligned below password: "Forgot password?" text button.
  - Bottom (32dp margin): "Continue/Login" Primary Button.
  - Footer (32dp below button): "Don't have an account? Sign up".
- **Colors & Styling**: Background `#1E1E24`. Inputs `#2B2B36` with `#3A3A4A` borders. Active states/buttons `#4CE5E5`.
- **Light & Dark Mode Adaptations**: Dark mode only.
- **Micro-Interactions & Animations**: Input borders turn Cyan on focus. Button shows a `CircularProgressIndicator` inside when loading.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `POST /api/v1/auth/login`
- **Request Payload**: `{"email": "...", "password": "..."}`
- **Response**: `{"token": "eyJhbG...", "user": {...}}`
- **Error & Loading States**: Handle `401 Unauthorized` (Invalid credentials) showing a Toast/Snackbar.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginUiEvent {
    data class EmailChanged(val value: String) : LoginUiEvent
    data class PasswordChanged(val value: String) : LoginUiEvent
    object TogglePasswordVisibility : LoginUiEvent
    object Submit : LoginUiEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SplashEffect>() // Reusing effect channel pattern
    val effect = _effect.asSharedFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> _state.update { it.copy(email = event.value, error = null) }
            is LoginUiEvent.PasswordChanged -> _state.update { it.copy(password = event.value, error = null) }
            is LoginUiEvent.TogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is LoginUiEvent.Submit -> login()
        }
    }

    private fun login() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val result = authRepository.login(_state.value.email, _state.value.password)
        _state.update { it.copy(isLoading = false) }
        
        result.onSuccess {
            // Token is saved in repository
            // Emit navigation
        }.onFailure { err ->
            _state.update { it.copy(error = err.message ?: "Login failed") }
        }
    }
}
```

## 5. Jetpack Compose UI Code (Kotlin)
```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), onNavigate: (Route) -> Unit) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E24))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(LoginUiEvent.EmailChanged(it)) },
            label = { Text("Your Email", color = Color(0xFFA0A0AB)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFF2B2B36),
                focusedBorderColor = Color(0xFF4CE5E5),
                unfocusedBorderColor = Color(0xFF3A3A4A)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginUiEvent.PasswordChanged(it)) },
            label = { Text("Password", color = Color(0xFFA0A0AB)) },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFF2B2B36),
                focusedBorderColor = Color(0xFF4CE5E5),
                unfocusedBorderColor = Color(0xFF3A3A4A)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        TextButton(
            onClick = { onNavigate(Route.ForgotPasswordEmail()) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot password?", color = Color(0xFF4CE5E5))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.onEvent(LoginUiEvent.Submit) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CE5E5)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF1E1E24), modifier = Modifier.size(24.dp))
            } else {
                Text("Continue", color = Color(0xFF1E1E24), fontWeight = FontWeight.Bold)
            }
        }
    }
}
```

## 6. AI UI Design Generation Prompt
> Generate a modern Android Login screen for an app named "HappyPaws". The UI is strictly dark mode. Background color is deep charcoal (#1E1E24). In the center is a top-aligned vibrant cyan (#4CE5E5) logo. Below it are two large, rounded-rectangle text input fields (Email and Password) filled with slate grey (#2B2B36) and outlined in dark slate (#3A3A4A). Below the inputs on the right is a small text link "Forgot password?" in cyan. Below that is a wide, 8px rounded primary button filled with vibrant cyan (#4CE5E5) containing black text "Continue". At the very bottom, cool grey text asks "Don't have an account?" followed by a cyan "Sign up" text link. Clean, glass-like, premium utility app style.
