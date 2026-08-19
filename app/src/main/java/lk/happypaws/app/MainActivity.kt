package lk.happypaws.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.toRoute
import lk.happypaws.app.data.remote.api.SessionManager
import lk.happypaws.app.domain.repository.AuthRepository
import lk.happypaws.app.ui.auth.ForgotPasswordScreen
import lk.happypaws.app.ui.auth.LoginScreen
import lk.happypaws.app.ui.auth.PasswordResetSuccessScreen
import lk.happypaws.app.ui.auth.RegistrationSuccessScreen
import lk.happypaws.app.ui.auth.SetNewPasswordScreen
import lk.happypaws.app.ui.auth.SignUpDetailsScreen
import lk.happypaws.app.ui.auth.SignUpEmailScreen
import lk.happypaws.app.ui.auth.SignUpOtpScreen
import lk.happypaws.app.ui.auth.VerifyResetCodeScreen
import lk.happypaws.app.ui.home.HomeScreen
import lk.happypaws.app.ui.navigation.AppNavKey
import lk.happypaws.app.ui.onboarding.OnboardingScreen
import lk.happypaws.app.ui.theme.HappyPawsTheme
import javax.inject.Inject

import androidx.activity.viewModels

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var sessionManager: SessionManager

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        splashScreen.setKeepOnScreenCondition { 
            mainViewModel.connectionState.value == ConnectionState.LOADING 
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyPawsTheme {
                val navController = rememberNavController()
                val scope = androidx.compose.runtime.rememberCoroutineScope()

                androidx.compose.runtime.LaunchedEffect(Unit) {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        sessionManager.sessionExpiredEvent.collectLatest { message ->
                            android.widget.Toast.makeText(
                                this@MainActivity,
                                message,
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(AppNavKey.Onboarding) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                }

                val startRoute: AppNavKey = remember {
                    if (authRepository.isLoggedIn()) AppNavKey.Home else AppNavKey.Onboarding
                }

                androidx.compose.runtime.LaunchedEffect(Unit) {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        mainViewModel.checkConnectivity()
                    }
                }

                val connectionState by mainViewModel.connectionState.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = startRoute,
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(500)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(500)
                                )
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(500)
                                )
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(500)
                                )
                            }
                        ) {
                            composable<AppNavKey.Onboarding> {
                                OnboardingScreen(
                                    onNavigateToSignUp = {
                                        navController.navigate(AppNavKey.SignUpEmail)
                                    },
                                    onNavigateToLogin = {
                                        navController.navigate(AppNavKey.Login)
                                    }
                                )
                            }
                            composable<AppNavKey.Login> {
                                LoginScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onLoginSuccess = {
                                        scope.launch {
                                            authRepository.setOnboardingCompleted(true)
                                            navController.navigate(AppNavKey.Home) {
                                                popUpTo(navController.graph.id) { inclusive = true }
                                            }
                                        }
                                    },
                                    onForgotPassword = {
                                        navController.navigate(AppNavKey.ForgotPassword)
                                    }
                                )
                            }
                            composable<AppNavKey.ForgotPassword> {
                                ForgotPasswordScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToVerify = { email ->
                                        navController.navigate(AppNavKey.VerifyResetCode(email))
                                    }
                                )
                            }
                            composable<AppNavKey.VerifyResetCode> { backStackEntry ->
                                val route = backStackEntry.toRoute<AppNavKey.VerifyResetCode>()
                                VerifyResetCodeScreen(
                                    email = route.email,
                                    onNavigateBack = { navController.popBackStack() },
                                    onVerifySuccess = { emailArg, token ->
                                        navController.navigate(AppNavKey.SetNewPassword(emailArg, token))
                                    }
                                )
                            }
                            composable<AppNavKey.SetNewPassword> { backStackEntry ->
                                val route = backStackEntry.toRoute<AppNavKey.SetNewPassword>()
                                SetNewPasswordScreen(
                                    email = route.email,
                                    resetToken = route.resetToken,
                                    onNavigateBack = { navController.popBackStack() },
                                    onResetSuccess = {
                                        navController.navigate(AppNavKey.PasswordResetSuccess)
                                    }
                                )
                            }
                            composable<AppNavKey.PasswordResetSuccess> {
                                PasswordResetSuccessScreen(
                                    onBackToLogin = {
                                        navController.navigate(AppNavKey.Login) {
                                            popUpTo(AppNavKey.ForgotPassword) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable<AppNavKey.SignUpEmail> {
                                SignUpEmailScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToOtp = { email: String ->
                                        navController.navigate(AppNavKey.SignUpOtp(email))
                                    }
                                )
                            }
                            composable<AppNavKey.SignUpOtp> { backStackEntry ->
                                val route = backStackEntry.toRoute<AppNavKey.SignUpOtp>()
                                SignUpOtpScreen(
                                    email = route.email,
                                    onNavigateBack = { navController.popBackStack() },
                                    onVerifySuccess = { signupToken ->
                                        navController.navigate(AppNavKey.SignUpDetails(route.email, signupToken))
                                    }
                                )
                            }
                            composable<AppNavKey.SignUpDetails> { backStackEntry ->
                                val route = backStackEntry.toRoute<AppNavKey.SignUpDetails>()
                                SignUpDetailsScreen(
                                    email = route.email,
                                    signupToken = route.signupToken,
                                    onNavigateBack = { navController.popBackStack() },
                                    onRegistrationSuccess = {
                                        navController.navigate(AppNavKey.RegistrationSuccess)
                                    }
                                )
                            }
                            composable<AppNavKey.RegistrationSuccess> {
                                RegistrationSuccessScreen(
                                    onGoToDashboard = {
                                        navController.navigate(AppNavKey.Home) {
                                            popUpTo(navController.graph.id) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable<AppNavKey.Home> {
                                HomeScreen(
                                    onLogout = {
                                        navController.navigate(AppNavKey.Onboarding) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    onNavigateTo = { route ->
                                        navController.navigate(route)
                                    }
                                )
                            }
                            
                            // Profile & Role Feature Stubs
                            composable<AppNavKey.EditProfile> {
                                lk.happypaws.app.ui.profile.StubScreen("Edit Profile") { navController.popBackStack() }
                            }
                            composable<AppNavKey.KycVerification> {
                                lk.happypaws.app.ui.profile.StubScreen("KYC Verification") { navController.popBackStack() }
                            }
                            composable<AppNavKey.LifestyleProfile> {
                                lk.happypaws.app.ui.profile.StubScreen("Lifestyle Profile") { navController.popBackStack() }
                            }
                            composable<AppNavKey.MyListings> {
                                lk.happypaws.app.ui.profile.StubScreen("My Animal Listings") { navController.popBackStack() }
                            }
                            composable<AppNavKey.MyApplications> {
                                lk.happypaws.app.ui.profile.StubScreen("My Adoption Applications") { navController.popBackStack() }
                            }
                            composable<AppNavKey.RescueReports> {
                                lk.happypaws.app.ui.profile.StubScreen("My Rescue Reports") { navController.popBackStack() }
                            }
                            composable<AppNavKey.RoleManagement> {
                                lk.happypaws.app.ui.profile.StubScreen("Manage Roles") { navController.popBackStack() }
                            }
                            composable<AppNavKey.ChangePassword> {
                                lk.happypaws.app.ui.profile.StubScreen("Change Password") { navController.popBackStack() }
                            }
                            composable<AppNavKey.RegisteredDevices> {
                                lk.happypaws.app.ui.profile.StubScreen("Registered Devices") { navController.popBackStack() }
                            }
                            composable<AppNavKey.FosterDashboard> {
                                lk.happypaws.app.ui.profile.StubScreen("Foster Dashboard") { navController.popBackStack() }
                            }
                            composable<AppNavKey.TransportTasks> {
                                lk.happypaws.app.ui.profile.StubScreen("Transport Tasks") { navController.popBackStack() }
                            }
                            composable<AppNavKey.Sponsorships> {
                                lk.happypaws.app.ui.profile.StubScreen("Sponsorships") { navController.popBackStack() }
                            }
                            composable<AppNavKey.VetConsultations> {
                                lk.happypaws.app.ui.profile.StubScreen("Veterinarian Consultations") { navController.popBackStack() }
                            }
                        }

                        if (connectionState == ConnectionState.ERROR) {
                            lk.happypaws.app.ui.components.NoConnectionScreen(
                                onRetry = { mainViewModel.checkConnectivity() }
                            )
                        }
                    }
                }
            }
        }
    }
}