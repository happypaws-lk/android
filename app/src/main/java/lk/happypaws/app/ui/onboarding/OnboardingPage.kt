package lk.happypaws.app.ui.onboarding

import androidx.annotation.DrawableRes

data class OnboardingScreen(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
)
