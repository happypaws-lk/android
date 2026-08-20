package lk.happypaws.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import lk.happypaws.app.ui.navigation.BottomNavItem
import lk.happypaws.app.ui.navigation.HappyPawsBottomNavBar
import lk.happypaws.app.ui.navigation.HomeNavKey
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
    private val createPostViewModel: lk.happypaws.app.ui.post.CreatePostViewModel by viewModels()

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
                val meProfile by mainViewModel.meProfile.collectAsStateWithLifecycle()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var selectedTabName by rememberSaveable { mutableStateOf("CommunityTab") }
                val currentHomeTab: HomeNavKey = when (selectedTabName) {
                    "NearbyTab" -> HomeNavKey.NearbyTab
                    "ChatsTab" -> HomeNavKey.ChatsTab
                    "ProfileTab" -> HomeNavKey.ProfileTab
                    else -> HomeNavKey.CommunityTab
                }

                val isAuthRoute = if (currentRoute != null) {
                    currentRoute.contains("Onboarding") ||
                    currentRoute.contains("Login") ||
                    currentRoute.contains("SignUp") ||
                    currentRoute.contains("RegistrationSuccess") ||
                    currentRoute.contains("ForgotPassword") ||
                    currentRoute.contains("VerifyResetCode") ||
                    currentRoute.contains("SetNewPassword") ||
                    currentRoute.contains("PasswordResetSuccess")
                } else {
                    !authRepository.isLoggedIn()
                }

                val showBottomBar = !isAuthRoute

                val activeBottomNavTab: HomeNavKey = when {
                    currentRoute?.contains("Profile") == true ||
                    currentRoute?.contains("KycVerification") == true ||
                    currentRoute?.contains("LifestyleProfile") == true ||
                    currentRoute?.contains("MyApplications") == true ||
                    currentRoute?.contains("CommunityActivity") == true ||
                    currentRoute?.contains("RoleManagement") == true ||
                    currentRoute?.contains("RequestRole") == true ||
                    currentRoute?.contains("ChangePassword") == true ||
                    currentRoute?.contains("RegisteredDevices") == true ||
                    currentRoute?.contains("FosterDashboard") == true ||
                    currentRoute?.contains("TransportTasks") == true ||
                    currentRoute?.contains("Sponsorships") == true ||
                    currentRoute?.contains("VetConsultations") == true -> HomeNavKey.ProfileTab

                    currentRoute?.contains("Home") == true -> currentHomeTab
                    else -> currentHomeTab
                }

                val onTabSelected: (BottomNavItem) -> Unit = { item ->
                    selectedTabName = item.route::class.simpleName ?: "CommunityTab"
                    if (currentRoute?.contains("AppNavKey.Home") != true) {
                        navController.navigate(AppNavKey.Home) {
                            popUpTo(AppNavKey.Home) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }

                val onFabClick: () -> Unit = {
                    if (currentRoute?.contains("CreatePostTypeSelection") != true) {
                        navController.navigate(AppNavKey.CreatePostTypeSelection)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            HappyPawsBottomNavBar(
                                currentRoute = activeBottomNavTab,
                                onTabSelected = onTabSelected,
                                onFabClick = onFabClick,
                                userAvatarKey = meProfile?.avatarKey,
                                userName = meProfile?.name ?: ""
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
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
                                    currentTab = currentHomeTab,
                                    onLogout = {
                                        selectedTabName = "CommunityTab"
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
                                lk.happypaws.app.ui.profile.EditProfileScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable<AppNavKey.KycVerification> {
                                lk.happypaws.app.ui.profile.KycVerificationScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable<AppNavKey.LifestyleProfile> {
                                lk.happypaws.app.ui.profile.LifestyleProfileScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToKyc = { navController.navigate(AppNavKey.KycVerification) }
                                )
                            }
                            composable<AppNavKey.MyApplications> {
                                lk.happypaws.app.ui.profile.StubScreen("My Applications") { navController.popBackStack() }
                            }
                            composable<AppNavKey.CommunityActivity> {
                                lk.happypaws.app.ui.profile.CommunityActivityScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateTo = { route -> navController.navigate(route) }
                                )
                            }
                            composable<AppNavKey.PostDetail> { backStackEntry ->
                                val route = backStackEntry.toRoute<AppNavKey.PostDetail>()
                                lk.happypaws.app.ui.post.PostDetailScreen(
                                    id = route.id,
                                    type = route.type,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable<AppNavKey.RoleManagement> {
                                lk.happypaws.app.ui.profile.ManageRolesScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToRequestRole = { roleValue ->
                                        navController.navigate(AppNavKey.RequestRole(roleValue))
                                    }
                                )
                            }
                            composable<AppNavKey.RequestRole> {
                                lk.happypaws.app.ui.profile.RequestRoleScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable<AppNavKey.ChangePassword> {
                                lk.happypaws.app.ui.profile.ChangePasswordScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable<AppNavKey.RegisteredDevices> {
                                lk.happypaws.app.ui.profile.RegisteredDevicesScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
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

                            // Community Post Creation Wizard
                            composable<AppNavKey.CreatePostTypeSelection> {
                                lk.happypaws.app.ui.post.PostTypeSelectionScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onPostTypeSelected = { postType ->
                                        when (postType) {
                                            lk.happypaws.app.ui.post.model.CommunityPostType.ADOPTION_LISTING -> {
                                                navController.navigate(AppNavKey.CreateAdoptionListing)
                                            }
                                            lk.happypaws.app.ui.post.model.CommunityPostType.RESCUE_REPORT -> {
                                                navController.navigate(AppNavKey.CreateRescueReport)
                                            }
                                            lk.happypaws.app.ui.post.model.CommunityPostType.TRANSPORT_REQUEST -> {
                                                navController.navigate(AppNavKey.CreateTransportRequest)
                                            }
                                            lk.happypaws.app.ui.post.model.CommunityPostType.COMMUNITY_STORY -> {
                                                navController.navigate(AppNavKey.CreateCommunityStory)
                                            }
                                        }
                                    },
                                    viewModel = createPostViewModel
                                 )
                            }
                            composable<AppNavKey.CreateAdoptionListing> {
                                lk.happypaws.app.ui.post.CreateAdoptionListingScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    viewModel = createPostViewModel
                                )
                            }
                            composable<AppNavKey.CreateRescueReport> {
                                lk.happypaws.app.ui.post.rescue.RescueReportFlow(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable<AppNavKey.CreateTransportRequest> {
                                lk.happypaws.app.ui.post.CreateTransportRequestScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    viewModel = createPostViewModel
                                )
                            }
                            composable<AppNavKey.CreateCommunityStory> {
                                lk.happypaws.app.ui.post.CreateCommunityStoryScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    viewModel = createPostViewModel
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = connectionState == ConnectionState.LOADING,
                            enter = fadeIn(animationSpec = tween(250)),
                            exit = fadeOut(animationSpec = tween(250))
                        ) {
                            lk.happypaws.app.ui.components.AppSkeletonScreen(
                                isLoggedIn = authRepository.isLoggedIn()
                            )
                        }

                        AnimatedVisibility(
                            visible = connectionState == ConnectionState.ERROR,
                            enter = fadeIn(animationSpec = tween(250)),
                            exit = fadeOut(animationSpec = tween(250))
                        ) {
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