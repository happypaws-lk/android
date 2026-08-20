package lk.happypaws.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.ui.components.UserAvatar
import lk.happypaws.app.ui.profile.components.ProfileStatColumn
import lk.happypaws.app.ui.profile.components.RoleBadgesLayout
import lk.happypaws.app.ui.profile.components.formatMemberSinceDate

@Composable
fun ProfileHeaderCard(
    profile: MeProfileResponse,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onEditProfile,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section with Centered Avatar and Top-Right Edit Button
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Centered User Avatar
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 16.dp)
                        .size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(
                        name = profile.name,
                        avatarKey = profile.avatarKey,
                        size = 100.dp,
                        modifier = Modifier.size(100.dp)
                    )
                }

                // Edit Button at Top-Right
                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            // Role Badges
            if (profile.roles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                RoleBadgesLayout(roles = profile.roles)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3 Stat Highlight Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stat 1: Reputation
                ProfileStatColumn(
                    icon = Icons.Default.Star,
                    iconBackgroundColor = Color(0xFFFFF8E1),
                    iconTintColor = Color(0xFFFFA000),
                    value = "${profile.reputationPoints} pts",
                    label = "Reputation"
                )

                // Stat 2: Member Since
                ProfileStatColumn(
                    icon = Icons.Default.CalendarMonth,
                    iconBackgroundColor = Color(0xFFE1F5FE),
                    iconTintColor = Color(0xFF0288D1),
                    value = formatMemberSinceDate(profile.createdAt),
                    label = "Member Since"
                )

                // Stat 3: Identity Status
                ProfileStatColumn(
                    icon = Icons.Default.VerifiedUser,
                    iconBackgroundColor = if (profile.isVerified) Color(0xFFE8F5E9) else Color(0xFFFAFAFA),
                    iconTintColor = if (profile.isVerified) Color(0xFF2E7D32) else Color(0xFF757575),
                    value = if (profile.isVerified) "Verified" else "Unverified",
                    label = "Identity Status"
                )
            }
        }
    }
}

