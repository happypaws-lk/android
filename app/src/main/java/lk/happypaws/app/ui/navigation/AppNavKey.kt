package lk.happypaws.app.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppNavKey {
    
    @Serializable
    data object Onboarding : AppNavKey
    
    @Serializable
    data object Login : AppNavKey
    
    @Serializable
    data object Home : AppNavKey

    @Serializable
    data object SignUpEmail : AppNavKey

    @Serializable
    data class SignUpOtp(val email: String) : AppNavKey

    @Serializable
    data class SignUpDetails(val email: String, val signupToken: String) : AppNavKey

    @Serializable
    data object RegistrationSuccess : AppNavKey

    @Serializable
    data object ForgotPassword : AppNavKey

    @Serializable
    data class VerifyResetCode(val email: String) : AppNavKey

    @Serializable
    data class SetNewPassword(val email: String, val resetToken: String) : AppNavKey

    @Serializable
    data object PasswordResetSuccess : AppNavKey
}
