package lk.happypaws.app.ui.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.happypaws.app.data.remote.model.RoleResponse

data class RoleBadgeStyle(
    val label: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val contentColor: Color
)

fun getRoleBadgeStyle(roleName: String): RoleBadgeStyle {
    return when (roleName.trim().lowercase()) {
        "adopter" -> RoleBadgeStyle(
            label = "Adopter",
            icon = Icons.Default.Pets,
            backgroundColor = Color(0xFFE0F2F1),
            contentColor = Color(0xFF008585)
        )
        "foster" -> RoleBadgeStyle(
            label = "Foster",
            icon = Icons.Default.Home,
            backgroundColor = Color(0xFFFFF3E0),
            contentColor = Color(0xFFE65100)
        )
        "transporter" -> RoleBadgeStyle(
            label = "Transporter",
            icon = Icons.Default.LocalShipping,
            backgroundColor = Color(0xFFE8EAF6),
            contentColor = Color(0xFF283593)
        )
        "sponsor" -> RoleBadgeStyle(
            label = "Sponsor",
            icon = Icons.Default.VolunteerActivism,
            backgroundColor = Color(0xFFFCE4EC),
            contentColor = Color(0xFFC2185B)
        )
        "veterinarian" -> RoleBadgeStyle(
            label = "Veterinarian",
            icon = Icons.Default.LocalHospital,
            backgroundColor = Color(0xFFE8F5E9),
            contentColor = Color(0xFF2E7D32)
        )
        "admin" -> RoleBadgeStyle(
            label = "Admin",
            icon = Icons.Default.AdminPanelSettings,
            backgroundColor = Color(0xFFF3E5F5),
            contentColor = Color(0xFF6A1B9A)
        )
        else -> RoleBadgeStyle(
            label = roleName,
            icon = Icons.Default.Pets,
            backgroundColor = Color(0xFFF5F5F5),
            contentColor = Color(0xFF616161)
        )
    }
}

@Composable
fun RoleBadge(
    roleName: String,
    modifier: Modifier = Modifier
) {
    val style = getRoleBadgeStyle(roleName)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = style.backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = style.contentColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = style.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = style.contentColor
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoleBadgesLayout(
    roles: List<RoleResponse>,
    modifier: Modifier = Modifier
) {
    if (roles.isEmpty()) return

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        roles.forEach { role ->
            RoleBadge(
                roleName = role.role,
                modifier = Modifier.padding(horizontal = 3.dp)
            )
        }
    }
}
