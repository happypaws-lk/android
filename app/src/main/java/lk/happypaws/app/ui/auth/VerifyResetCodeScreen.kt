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
import lk.happypaws.app.ui.components.OtpInputField
import lk.happypaws.app.ui.theme.HappyPawsTheme
import lk.happypaws.app.ui.theme.Neutral60

@Composable
fun VerifyResetCodeScreen(
    email: String,
    viewModel: PasswordResetViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onVerifySuccess: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    VerifyResetCodeContent(
        uiState = uiState,
        onOtpChange = viewModel::onOtpChange,
        onVerify = { viewModel.verifyOtp(onVerifySuccess) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun VerifyResetCodeContent(
    uiState: PasswordResetUiState,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
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
                text = "Check your email",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We've sent a 6-digit code to ${uiState.email}",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral60,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OtpInputField(
            otpText = uiState.otp,
            onOtpTextChange = onOtpChange,
            isError = uiState.isOtpError
        )

        uiState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVerify,
            enabled = uiState.otp.length == 6 && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Verify", style = MaterialTheme.typography.labelLarge)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun VerifyResetCodeEmptyPreview() {
    HappyPawsTheme {
        VerifyResetCodeContent(
            uiState = PasswordResetUiState(email = "test@example.com"),
            onOtpChange = {},
            onVerify = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Filled State")
@Composable
fun VerifyResetCodeFilledPreview() {
    HappyPawsTheme {
        VerifyResetCodeContent(
            uiState = PasswordResetUiState(email = "test@example.com", otp = "123456"),
            onOtpChange = {},
            onVerify = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun VerifyResetCodeErrorPreview() {
    HappyPawsTheme {
        VerifyResetCodeContent(
            uiState = PasswordResetUiState(email = "test@example.com", otp = "123456", isOtpError = true, error = "Invalid reset code"),
            onOtpChange = {},
            onVerify = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun VerifyResetCodeLoadingPreview() {
    HappyPawsTheme {
        VerifyResetCodeContent(
            uiState = PasswordResetUiState(email = "test@example.com", otp = "123456", isLoading = true),
            onOtpChange = {},
            onVerify = {},
            onNavigateBack = {}
        )
    }
}
