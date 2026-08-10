package lk.happypaws.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lk.happypaws.app.R
import lk.happypaws.app.ui.theme.HappyPawsTheme
import lk.happypaws.app.ui.theme.Neutral60
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RegistrationSuccessScreen(
    onGoToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.illus_signup_complete),
            contentDescription = null,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "You're all set up. You're currently set up as an Adopter, but you can always add more roles later.",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral60,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1.2f))

        Button(
            onClick = onGoToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Go to Dashboard", style = MaterialTheme.typography.labelLarge)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationSuccessScreenPreview() {
    HappyPawsTheme {
        RegistrationSuccessScreen(onGoToDashboard = {})
    }
}
