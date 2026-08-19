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

    // Profile & General Functionalities
    @Serializable
    data object EditProfile : AppNavKey

    @Serializable
    data object KycVerification : AppNavKey

    @Serializable
    data object LifestyleProfile : AppNavKey

    @Serializable
    data object MyListings : AppNavKey

    @Serializable
    data object MyApplications : AppNavKey

    @Serializable
    data object RescueReports : AppNavKey

    @Serializable
    data object RoleManagement : AppNavKey

    @Serializable
    data object ChangePassword : AppNavKey

    @Serializable
    data object RegisteredDevices : AppNavKey

    // Role-specific Stub Routes
    @Serializable
    data object FosterDashboard : AppNavKey

    @Serializable
    data object TransportTasks : AppNavKey

    @Serializable
    data object Sponsorships : AppNavKey

    @Serializable
    data object VetConsultations : AppNavKey
}
