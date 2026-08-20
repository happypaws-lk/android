package lk.happypaws.app.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Fence
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.ui.components.HappyPawsBackButton
import lk.happypaws.app.ui.profile.components.ActivityLevelSelectionGroup
import lk.happypaws.app.ui.profile.components.AddCustomPetDialog
import lk.happypaws.app.ui.profile.components.ExistingPetsSelector
import lk.happypaws.app.ui.profile.components.HomeSizeSelectionGroup
import lk.happypaws.app.ui.profile.components.HouseholdToggleCard
import lk.happypaws.app.ui.profile.components.LifestyleHeaderCard
import lk.happypaws.app.ui.profile.components.UnverifiedNoticeBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifestyleProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToKyc: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LifestyleProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddPetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lifestyle Profile",
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
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                // ── Header Information Card ──
                LifestyleHeaderCard(profile = uiState.meProfile)

                // ── Unverified Notice Banner (if user is unverified) ──
                if (!uiState.isVerified) {
                    Spacer(modifier = Modifier.height(16.dp))
                    UnverifiedNoticeBanner(onNavigateToKyc = onNavigateToKyc)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── 1. Home Size & Space ──
                HomeSizeSelectionGroup(
                    selectedHomeSize = uiState.homeSize,
                    onSelectHomeSize = viewModel::onHomeSizeSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── 2. Outdoor Access ──
                Text(
                    text = "Outdoor Environment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Yard and outdoor access are essential for higher energy dogs and active pets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                HouseholdToggleCard(
                    title = "Enclosed Yard / Garden Space",
                    subtitle = "Secure, fenced outdoor area suitable for off-leash play and exercise",
                    checked = uiState.hasYard,
                    onCheckedChange = viewModel::onHasYardChanged,
                    icon = Icons.Outlined.Fence
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── 3. Household Dynamics & Children ──
                Text(
                    text = "Household Members",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Helps match with animals that thrive in family environments",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                HouseholdToggleCard(
                    title = "Children in Household",
                    subtitle = "Kids live in or frequently visit. Recommendations will prioritize gentle, kid-safe temperaments",
                    checked = uiState.hasChildren,
                    onCheckedChange = viewModel::onHasChildrenChanged,
                    icon = Icons.Outlined.ChildCare
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── 4. Household Activity Tempo ──
                ActivityLevelSelectionGroup(
                    selectedActivityLevel = uiState.activityLevel,
                    onSelectActivityLevel = viewModel::onActivityLevelSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── 5. Existing Pets in Household ──
                ExistingPetsSelector(
                    petTypes = uiState.existingPetTypes,
                    onAddPetType = viewModel::addPetType,
                    onRemovePetType = viewModel::removePetType,
                    onOpenAddPetDialog = { showAddPetDialog = true }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ── 6. Save Button ──
                Button(
                    onClick = {
                        viewModel.saveProfile {
                            Toast.makeText(context, "Lifestyle profile saved successfully!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving Preferences...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Save Lifestyle Profile", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // ── Add Custom Pet Modal Dialog ──
    if (showAddPetDialog) {
        AddCustomPetDialog(
            onDismissRequest = { showAddPetDialog = false },
            onAddPet = { newPet ->
                viewModel.addPetType(newPet)
                showAddPetDialog = false
            },
            existingPets = uiState.existingPetTypes
        )
    }
}
