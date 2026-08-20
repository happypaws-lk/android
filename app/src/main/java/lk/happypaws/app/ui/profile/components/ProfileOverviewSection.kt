package lk.happypaws.app.ui.profile.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lk.happypaws.app.data.remote.model.MeProfileResponse
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatMemberSinceDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "2026"
    return try {
        val instant = Instant.parse(createdAt)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
        formatter.format(zonedDateTime)
    } catch (_: Exception) {
        try {
            val date = LocalDate.parse(createdAt.substring(0, 10))
            val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
            formatter.format(date)
        } catch (_: Exception) {
            "2026"
        }
    }
}

@Composable
fun ProfileOverviewSection(
    profile: MeProfileResponse,
    selectedImageBitmap: Bitmap?,
    onEditPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Avatar with Teal Edit Pencil Badge (Half in / Half out) ──
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(117.dp)
                .clickable(onClick = onEditPhotoClick)
        ) {
            // Main Circular Avatar (Top: 0 to 100dp)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageBitmap != null) {
                    Image(
                        bitmap = selectedImageBitmap.asImageBitmap(),
                        contentDescription = "Profile Photo Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                } else {
                    lk.happypaws.app.ui.components.UserAvatar(
                        name = profile.name,
                        avatarKey = profile.avatarKey,
                        size = 100.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Edit Pencil Icon Badge (Overlapping Bottom Center: 83dp to 117dp)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Photo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Name & Email ──
        Text(
            text = profile.name,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = profile.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        // ── Role Badges Area ──
        if (profile.roles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            RoleBadgesLayout(roles = profile.roles)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── 3 Stat Highlight Columns ──
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

@Composable
fun ProfileStatColumn(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTintColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}
