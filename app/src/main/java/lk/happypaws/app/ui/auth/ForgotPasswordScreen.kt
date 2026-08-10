package lk.happypaws.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.R
import lk.happypaws.app.ui.components.CollapsibleHeader
import lk.happypaws.app.ui.theme.HappyPawsTheme
import lk.happypaws.app.ui.theme.Neutral60

@Composable
fun ForgotPasswordScreen(
    viewModel: PasswordResetViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToVerify: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ForgotPasswordContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onContinue = { viewModel.requestOtp(onNavigateToVerify) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ForgotPasswordContent(
    uiState: PasswordResetUiState,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                text = "Forgot password?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your email address and we'll send you a 6-digit code to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral60,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your Email",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = { 
                Text(
                    text = "Enter your email",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Neutral60
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.error != null,
            supportingText = {
                uiState.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Neutral60.copy(alpha = 0.3f),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            enabled = uiState.isEmailValid && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Send Code", style = MaterialTheme.typography.labelLarge)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun ForgotPasswordEmptyPreview() {
    HappyPawsTheme {
        ForgotPasswordContent(
            uiState = PasswordResetUiState(),
            onEmailChange = {},
            onContinue = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Filled State")
@Composable
fun ForgotPasswordFilledPreview() {
    HappyPawsTheme {
        ForgotPasswordContent(
            uiState = PasswordResetUiState(email = "test@example.com", isEmailValid = true),
            onEmailChange = {},
            onContinue = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun ForgotPasswordErrorPreview() {
    HappyPawsTheme {
        ForgotPasswordContent(
            uiState = PasswordResetUiState(email = "invalid-email", error = "Invalid email format"),
            onEmailChange = {},
            onContinue = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun ForgotPasswordLoadingPreview() {
    HappyPawsTheme {
        ForgotPasswordContent(
            uiState = PasswordResetUiState(email = "test@example.com", isEmailValid = true, isLoading = true),
            onEmailChange = {},
            onContinue = {},
            onNavigateBack = {}
        )
    }
}
