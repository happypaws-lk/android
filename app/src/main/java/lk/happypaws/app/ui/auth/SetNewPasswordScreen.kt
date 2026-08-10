package lk.happypaws.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.R
import lk.happypaws.app.ui.components.CollapsibleHeader
import lk.happypaws.app.ui.theme.HappyPawsTheme
import lk.happypaws.app.ui.theme.Neutral60

@Composable
fun SetNewPasswordScreen(
    email: String,
    resetToken: String,
    viewModel: PasswordResetViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(email, resetToken) {
        viewModel.setEmailAndToken(email, resetToken)
    }

    SetNewPasswordContent(
        uiState = uiState,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onResetPassword = { viewModel.resetPassword(onResetSuccess) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun SetNewPasswordContent(
    uiState: PasswordResetUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Back Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Neutral60.copy(alpha = 0.1f))
                .clickable { onNavigateBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CollapsibleHeader {
                Image(
                    painter = painterResource(id = R.drawable.illus_happypaws_icon),
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Create new password",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your new password must be unique from those previously used.",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral60,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Password Field
        Text(
            text = "New Password",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.newPassword,
            onValueChange = onPasswordChange,
            placeholder = { Text("••••••••••••", color = Neutral60) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.newPassword.isNotEmpty() && !uiState.isPasswordValid,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Neutral60)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Neutral60.copy(alpha = 0.3f)
            ),
            supportingText = {
                if (uiState.newPassword.isNotEmpty() && !uiState.isPasswordValid) {
                    Text(text = "Password must contain at least 8 characters, 1 uppercase, 1 lowercase, and 1 number")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        Text(
            text = "Confirm Password",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = { Text("••••••••••••", color = Neutral60) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.confirmPassword.isNotEmpty() && uiState.newPassword != uiState.confirmPassword,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Neutral60)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onResetPassword() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Neutral60.copy(alpha = 0.3f)
            ),
            supportingText = {
                if (uiState.confirmPassword.isNotEmpty() && uiState.newPassword != uiState.confirmPassword) {
                    Text(text = "Passwords do not match")
                }
            }
        )

        uiState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onResetPassword,
            enabled = uiState.isPasswordValid && uiState.newPassword == uiState.confirmPassword && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Reset Password", style = MaterialTheme.typography.labelLarge)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun SetNewPasswordEmptyPreview() {
    HappyPawsTheme {
        SetNewPasswordContent(
            uiState = PasswordResetUiState(),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetPassword = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Invalid State")
@Composable
fun SetNewPasswordInvalidPreview() {
    HappyPawsTheme {
        SetNewPasswordContent(
            uiState = PasswordResetUiState(newPassword = "weak"),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetPassword = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Mismatch State")
@Composable
fun SetNewPasswordMismatchPreview() {
    HappyPawsTheme {
        SetNewPasswordContent(
            uiState = PasswordResetUiState(newPassword = "Password123", confirmPassword = "Password12", isPasswordValid = true),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetPassword = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun SetNewPasswordLoadingPreview() {
    HappyPawsTheme {
        SetNewPasswordContent(
            uiState = PasswordResetUiState(newPassword = "Password123", confirmPassword = "Password123", isPasswordValid = true, isLoading = true),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetPassword = {},
            onNavigateBack = {}
        )
    }
}
