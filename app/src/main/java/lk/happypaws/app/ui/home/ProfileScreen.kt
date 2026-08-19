package lk.happypaws.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.ui.home.components.ProfileHeaderCard
import lk.happypaws.app.ui.home.components.ProfileListItem
import lk.happypaws.app.ui.home.components.ProfileSection
import lk.happypaws.app.ui.navigation.AppNavKey

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit = {},
    onNavigateTo: (AppNavKey) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProfileUiState.Unauthenticated -> {
                    Text(
                        text = "Session expired",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProfileUiState.Success -> {
                    val profile = state.profile
                    val roleNames = profile.roles.map { it.role }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            ProfileHeaderCard(
                                profile = profile,
                                onEditProfile = { onNavigateTo(AppNavKey.EditProfile) }
                            )
                        }

                        item {
                            ProfileSection(title = "General") {
                                ProfileListItem(
                                    icon = Icons.Default.Favorite,
                                    title = "Lifestyle Profile",
                                    subtitle = "Update matching preferences",
                                    onClick = { onNavigateTo(AppNavKey.LifestyleProfile) }
                                )
                                ProfileListItem(
                                    icon = Icons.Default.Assignment,
                                    title = "My Applications",
                                    subtitle = "Track adoption requests",
                                    onClick = { onNavigateTo(AppNavKey.MyApplications) }
                                )
                                ProfileListItem(
                                    icon = Icons.Default.Pets,
                                    title = "My Animal Listings",
                                    onClick = { onNavigateTo(AppNavKey.MyListings) }
                                )
                                ProfileListItem(
                                    icon = Icons.Default.Report,
                                    title = "My Rescue Reports",
                                    onClick = { onNavigateTo(AppNavKey.RescueReports) }
                                )
                            }
                        }

                        item {
                            ProfileSection(title = "Verification & Identity") {
                                ProfileListItem(
                                    icon = Icons.Default.VerifiedUser,
                                    title = "Identity Verification (KYC)",
                                    subtitle = if (profile.isVerified) "Verified" else "Upload documents",
                                    onClick = { onNavigateTo(AppNavKey.KycVerification) }
                                )
                                ProfileListItem(
                                    icon = Icons.Default.AccountBox,
                                    title = "Manage Roles",
                                    subtitle = "Become a foster, transporter, or sponsor",
                                    onClick = { onNavigateTo(AppNavKey.RoleManagement) }
                                )
                            }
                        }

                        if (roleNames.contains("Foster")) {
                            item {
                                ProfileSection(title = "Foster Space") {
                                    ProfileListItem(
                                        icon = Icons.Default.Pets,
                                        title = "Foster Dashboard",
                                        subtitle = "Manage active placements",
                                        onClick = { onNavigateTo(AppNavKey.FosterDashboard) }
                                    )
                                }
                            }
                        }

                        if (roleNames.contains("Transporter")) {
                            item {
                                ProfileSection(title = "Transporter Hub") {
                                    ProfileListItem(
                                        icon = Icons.Default.LocalShipping,
                                        title = "Transport Tasks",
                                        subtitle = "Active runs and logs",
                                        onClick = { onNavigateTo(AppNavKey.TransportTasks) }
                                    )
                                }
                            }
                        }

                        if (roleNames.contains("Sponsor")) {
                            item {
                                ProfileSection(title = "Sponsor Dashboard") {
                                    ProfileListItem(
                                        icon = Icons.Default.Favorite,
                                        title = "My Sponsorships",
                                        subtitle = "Track pledged cases",
                                        onClick = { onNavigateTo(AppNavKey.Sponsorships) }
                                    )
                                }
                            }
                        }

                        if (roleNames.contains("Veterinarian")) {
                            item {
                                ProfileSection(title = "Veterinarian Portal") {
                                    ProfileListItem(
                                        icon = Icons.Default.LocalHospital,
                                        title = "Medical Consultations",
                                        subtitle = "Triage reviews and advice",
                                        onClick = { onNavigateTo(AppNavKey.VetConsultations) }
                                    )
                                }
                            }
                        }

                        item {
                            ProfileSection(title = "Account & Security") {
                                ProfileListItem(
                                    icon = Icons.Default.Lock,
                                    title = "Change Password",
                                    onClick = { onNavigateTo(AppNavKey.ChangePassword) }
                                )
                                ProfileListItem(
                                    icon = Icons.Default.Devices,
                                    title = "Registered Devices",
                                    onClick = { onNavigateTo(AppNavKey.RegisteredDevices) }
                                )
                            }
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { viewModel.logout(onLogout) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                                    Text(
                                        text = "Log Out",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                            Text(
                                text = "HappyPaws v1.0.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
