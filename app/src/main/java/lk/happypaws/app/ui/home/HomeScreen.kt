package lk.happypaws.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lk.happypaws.app.ui.navigation.BottomNavItem
import lk.happypaws.app.ui.navigation.HappyPawsBottomNavBar
import lk.happypaws.app.ui.navigation.HomeNavKey
import lk.happypaws.app.ui.navigation.AppNavKey
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit = {},
    onNavigateTo: (AppNavKey) -> Unit = {}
) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    
    // Determine the current route from the back stack, defaulting to CommunityTab
    val currentRoute = navBackStackEntry?.destination?.route?.let { routeString ->
        when {
            routeString.contains("CommunityTab") -> HomeNavKey.CommunityTab
            routeString.contains("NearbyTab") -> HomeNavKey.NearbyTab
            routeString.contains("ChatsTab") -> HomeNavKey.ChatsTab
            routeString.contains("ProfileTab") -> HomeNavKey.ProfileTab
            else -> HomeNavKey.CommunityTab
        }
    } ?: HomeNavKey.CommunityTab

    var showCreatePostSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            HappyPawsBottomNavBar(
                currentRoute = currentRoute,
                onTabSelected = { item ->
                    if (currentRoute != item.route) {
                        nestedNavController.navigate(item.route) {
                            popUpTo(nestedNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onFabClick = {
                    showCreatePostSheet = true
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 28.dp)
        ) {
            NavHost(
                navController = nestedNavController,
                startDestination = HomeNavKey.CommunityTab
            ) {
                composable<HomeNavKey.CommunityTab> {
                    CommunityScreen(
                        viewModel = viewModel,
                        onLogout = onLogout
                    )
                }
                composable<HomeNavKey.NearbyTab> {
                    NearbyScreen()
                }
                composable<HomeNavKey.ChatsTab> {
                    ChatsScreen()
                }
                composable<HomeNavKey.ProfileTab> {
                    ProfileScreen(
                        onLogout = onLogout,
                        onNavigateTo = onNavigateTo
                    )
                }
            }
        }
    }

    if (showCreatePostSheet) {
        CreatePostBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showCreatePostSheet = false }
        )
    }
}
