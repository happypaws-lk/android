package lk.happypaws.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(
    val route: HomeNavKey,
    val title: String,
    val icon: ImageVector
) {
    data object Community : BottomNavItem(HomeNavKey.CommunityTab, "Community", Icons.Outlined.People)
    data object Nearby : BottomNavItem(HomeNavKey.NearbyTab, "Nearby", Icons.Outlined.LocationOn)
    data object Chats : BottomNavItem(HomeNavKey.ChatsTab, "Chats", Icons.Outlined.Forum)
    data object Profile : BottomNavItem(HomeNavKey.ProfileTab, "Profile", Icons.Outlined.AccountCircle)
}

val bottomNavItems = listOf(
    BottomNavItem.Community,
    BottomNavItem.Nearby,
    BottomNavItem.Chats,
    BottomNavItem.Profile
)

@Composable
fun HappyPawsBottomNavBar(
    currentRoute: HomeNavKey,
    onTabSelected: (BottomNavItem) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Custom colors from the provided design image
    val activeTeal = Color(0xFF00897B)
    val inactiveGrey = Color(0xFF8E8E93)
    val navBackground = Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Bar Background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = navBackground,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First 2 Tabs
                NavItemCell(
                    item = BottomNavItem.Community,
                    isSelected = currentRoute == BottomNavItem.Community.route,
                    activeColor = activeTeal,
                    inactiveColor = inactiveGrey,
                    onClick = { onTabSelected(BottomNavItem.Community) }
                )
                NavItemCell(
                    item = BottomNavItem.Nearby,
                    isSelected = currentRoute == BottomNavItem.Nearby.route,
                    activeColor = activeTeal,
                    inactiveColor = inactiveGrey,
                    onClick = { onTabSelected(BottomNavItem.Nearby) }
                )

                // Spacer for FAB
                Spacer(modifier = Modifier.width(56.dp))

                // Last 2 Tabs
                NavItemCell(
                    item = BottomNavItem.Chats,
                    isSelected = currentRoute == BottomNavItem.Chats.route,
                    activeColor = activeTeal,
                    inactiveColor = inactiveGrey,
                    onClick = { onTabSelected(BottomNavItem.Chats) }
                )
                NavItemCell(
                    item = BottomNavItem.Profile,
                    isSelected = currentRoute == BottomNavItem.Profile.route,
                    activeColor = activeTeal,
                    inactiveColor = inactiveGrey,
                    onClick = { onTabSelected(BottomNavItem.Profile) }
                )
            }
        }

        // Center Floating Action Button (+)
        FloatingActionButton(
            onClick = onFabClick,
            shape = CircleShape,
            containerColor = activeTeal,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(6.dp),
            modifier = Modifier
                .offset(y = (-24).dp)
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Post",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItemCell(
    item: BottomNavItem,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        label = "tabColor"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tabScale"
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = animatedColor,
            modifier = Modifier
                .scale(animatedScale)
                .size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = animatedColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
