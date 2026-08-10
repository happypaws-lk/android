package lk.happypaws.app.ui.onboarding

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import lk.happypaws.app.R
import lk.happypaws.app.ui.theme.HappyPawsTheme
import lk.happypaws.app.ui.theme.Neutral60

@Composable
fun OnboardingScreen(
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.illus_onboarding_welcome,
            title = "Welcome to Happy Paws",
            description = "Join Sri Lanka's trusted animal rescue community. We are here to help you find your new best friend or support a pet in need."
        ),
        OnboardingPage(
            imageRes = R.drawable.illus_onboarding_trust,
            title = "A Safe Space for Every Paw",
            description = "Connect with verified shelters, rescuers, and adopters. We make sure every animal goes to a secure and loving home."
        ),
        OnboardingPage(
            imageRes = R.drawable.illus_onboarding_action,
            title = "Action for Animals",
            description = "Help us make a difference. report rescues, volunteer at shelters, or donate to support our mission."
        )
    )

    val startIndex = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { Int.MAX_VALUE }
    )

    // Auto-slide logic
    LaunchedEffect(pagerState.settledPage) {
        delay(5000.milliseconds) // Slide every 5 seconds
        pagerState.animateScrollToPage(
            page = pagerState.settledPage + 1,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page % pages.size])
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Page Indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage % pages.size == iteration) MaterialTheme.colorScheme.primary else Neutral60.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Action Buttons
        Button(
            onClick = onNavigateToSignUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Continue", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Neutral60)) {
                    append("Already have an account? ")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                    append("Sign In")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable {
                onNavigateToLogin()
            }
        )
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral60,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    HappyPawsTheme {
        OnboardingScreen()
    }
}

