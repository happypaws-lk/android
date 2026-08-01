package lk.happypaws.app.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import lk.happypaws.app.ui.theme.HappyPawsTheme

@Composable
fun WelcomeScreen() {
    Column {
        Text(text = "Welcome to Happy Paws")
        Button(onClick = { }) {
            Text("Continue")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    HappyPawsTheme {
        WelcomeScreen()
    }
}
