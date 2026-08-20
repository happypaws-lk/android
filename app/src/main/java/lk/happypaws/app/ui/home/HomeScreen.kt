package lk.happypaws.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.ui.navigation.AppNavKey
import lk.happypaws.app.ui.navigation.HomeNavKey

@Composable
fun HomeScreen(
    currentTab: HomeNavKey,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit = {},
    onNavigateTo: (AppNavKey) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 28.dp)
    ) {
        when (currentTab) {
            HomeNavKey.CommunityTab -> {
                CommunityScreen(
                    viewModel = viewModel,
                    onLogout = onLogout
                )
            }
            HomeNavKey.NearbyTab -> {
                NearbyScreen()
            }
            HomeNavKey.ChatsTab -> {
                ChatsScreen()
            }
            HomeNavKey.ProfileTab -> {
                ProfileScreen(
                    onLogout = onLogout,
                    onNavigateTo = onNavigateTo
                )
            }
        }
    }
}

