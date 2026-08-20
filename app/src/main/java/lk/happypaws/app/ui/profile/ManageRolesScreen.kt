package lk.happypaws.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.data.remote.model.RoleRequestResponse
import lk.happypaws.app.domain.model.RoleRequestStatus
import lk.happypaws.app.domain.model.UserRole
import lk.happypaws.app.ui.components.HappyPawsBackButton
import lk.happypaws.app.ui.profile.components.RoleBadgesLayout
import lk.happypaws.app.ui.profile.components.getRoleBadgeStyle

private enum class RoleCardStatus { NOT_APPLIED, PENDING, ACTIVE, REJECTED }

private data class RoleInfo(
    val userRole: UserRole,
    val description: String,
    val requiredDocs: String
)

private val requestableRoles = listOf(
    RoleInfo(UserRole.FOSTER, "Provide a temporary home for animals awaiting adoption.", "National ID or Passport"),
    RoleInfo(UserRole.TRANSPORTER, "Transport animals safely between shelters, fosters, and adopters.", "Driving License"),
    RoleInfo(UserRole.SPONSOR, "Fund rescue operations, medical care, and shelter costs.", "National ID or Passport"),
    RoleInfo(UserRole.VETERINARIAN, "Provide medical expertise and triage support for rescued animals.", "Vet / Clinic Registration")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRolesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRequestRole: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageRolesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Roles",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        HappyPawsBackButton(onClick = onNavigateBack)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.roleRequests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Active roles section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Active roles",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (uiState.activeRoles.isEmpty()) {
                        Text(
                            text = "You haven't been assigned any roles yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        RoleBadgesLayout(roles = uiState.activeRoles)
                    }
                }
            }

            // Section header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "Take on a new role",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Expand your impact in the rescue network.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Role cards
            items(requestableRoles.size) { index ->
                val info = requestableRoles[index]
                val status = resolveRoleStatus(info.userRole, uiState.activeRoles.map { it.role }, uiState.roleRequests)
                val latestRequest = uiState.roleRequests
                    .firstOrNull { it.role.equals(info.userRole.name, ignoreCase = true) }

                RoleCard(
                    info = info,
                    status = status,
                    rejectionReason = if (status == RoleCardStatus.REJECTED) latestRequest?.rejectionReason else null,
                    onApply = { onNavigateToRequestRole(info.userRole.value) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun resolveRoleStatus(
    role: UserRole,
    activeRoleNames: List<String>,
    requests: List<RoleRequestResponse>
): RoleCardStatus {
    if (activeRoleNames.any { it.equals(role.name, ignoreCase = true) }) return RoleCardStatus.ACTIVE
    val latestRequest = requests.firstOrNull { it.role.equals(role.name, ignoreCase = true) }
    return when {
        latestRequest == null -> RoleCardStatus.NOT_APPLIED
        RoleRequestStatus.from(latestRequest.status) == RoleRequestStatus.PENDING -> RoleCardStatus.PENDING
        RoleRequestStatus.from(latestRequest.status) == RoleRequestStatus.APPROVED -> RoleCardStatus.ACTIVE
        else -> RoleCardStatus.REJECTED
    }
}

@Composable
private fun RoleCard(
    info: RoleInfo,
    status: RoleCardStatus,
    rejectionReason: String?,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeStyle = getRoleBadgeStyle(info.userRole.name)
    val cardAlpha = if (status == RoleCardStatus.ACTIVE || status == RoleCardStatus.PENDING) 0.6f else 1f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = badgeStyle.icon,
                        contentDescription = null,
                        tint = badgeStyle.contentColor.copy(alpha = cardAlpha),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = badgeStyle.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = cardAlpha)
                    )
                }
                StatusChip(status = status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = info.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (status == RoleCardStatus.ACTIVE) 0.5f else 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Required: ${info.requiredDocs}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            if (status == RoleCardStatus.REJECTED && rejectionReason != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Rejection reason: $rejectionReason",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (status == RoleCardStatus.NOT_APPLIED || status == RoleCardStatus.REJECTED) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onApply,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (status == RoleCardStatus.REJECTED) "Reapply" else "Apply",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: RoleCardStatus, modifier: Modifier = Modifier) {
    when (status) {
        RoleCardStatus.NOT_APPLIED -> Unit
        RoleCardStatus.ACTIVE -> AssistChip(
            onClick = {},
            label = { Text("Active", style = MaterialTheme.typography.labelSmall) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                labelColor = MaterialTheme.colorScheme.primary
            ),
            border = null,
            modifier = modifier
        )
        RoleCardStatus.PENDING -> AssistChip(
            onClick = {},
            label = { Text("Under Review", style = MaterialTheme.typography.labelSmall) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color(0xFFFFF3E0),
                labelColor = Color(0xFFE65100)
            ),
            border = null,
            modifier = modifier
        )
        RoleCardStatus.REJECTED -> AssistChip(
            onClick = {},
            label = { Text("Rejected", style = MaterialTheme.typography.labelSmall) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                labelColor = MaterialTheme.colorScheme.error
            ),
            border = null,
            modifier = modifier
        )
    }
}
