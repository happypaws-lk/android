package lk.happypaws.app.ui.profile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Fence
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import lk.happypaws.app.data.remote.model.ActivityLevel
import lk.happypaws.app.data.remote.model.HomeSize
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.ui.components.UserAvatar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatTimestamp(isoString: String?): String {
    if (isoString.isNullOrBlank()) return ""
    return try {
        val instant = Instant.parse(isoString)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
        formatter.format(zonedDateTime)
    } catch (_: Exception) {
        try {
            isoString.take(10)
        } catch (_: Exception) {
            ""
        }
    }
}

@Composable
fun LifestyleHeaderCard(
    profile: MeProfileResponse?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Centered User Avatar (no edit button)
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                UserAvatar(
                    name = profile?.name ?: "",
                    avatarKey = profile?.avatarKey,
                    size = 100.dp,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = profile?.name ?: "User Profile",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: Lifestyle Profile (replacing email)
            Text(
                text = "Lifestyle Profile",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            // Role Badges
            if (!profile?.roles.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                RoleBadgesLayout(roles = profile.roles)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Matchmaking description paragraph replacing stats row
            Text(
                text = "Keep your living situation, activity routine, and household details up to date. Our matchmaking algorithm uses these preferences to suggest compatible rescue animals.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun UnverifiedNoticeBanner(
    onNavigateToKyc: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),
        border = BorderStroke(1.dp, Color(0xFFFFE082))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFB300)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Identity Verification Required",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = "Complete KYC verification to save your lifestyle profile and unlock matching.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF795548),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = onNavigateToKyc,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Verify", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HomeSizeSelectionGroup(
    selectedHomeSize: HomeSize?,
    onSelectHomeSize: (HomeSize) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Home Size & Space",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Select the dwelling type that best matches your primary residence",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        val options = listOf(
            Triple(HomeSize.Apartment, Icons.Outlined.Apartment, HomeSize.Apartment.description),
            Triple(HomeSize.House, Icons.Outlined.Home, HomeSize.House.description),
            Triple(HomeSize.Estate, Icons.Outlined.Landscape, HomeSize.Estate.description)
        )

        options.forEach { (homeSize, icon, description) ->
            val isSelected = selectedHomeSize == homeSize
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                animationSpec = tween(200),
                label = "homeSizeBorder"
            )
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
                animationSpec = tween(200),
                label = "homeSizeBg"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectHomeSize(homeSize) },
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = homeSize.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectHomeSize(homeSize) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityLevelSelectionGroup(
    selectedActivityLevel: ActivityLevel?,
    onSelectActivityLevel: (ActivityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Household Activity Tempo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "How active is your everyday lifestyle and exercise routine?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        val options = listOf(
            Triple(ActivityLevel.Low, Icons.Outlined.SelfImprovement, ActivityLevel.Low.description),
            Triple(ActivityLevel.Moderate, Icons.AutoMirrored.Outlined.DirectionsWalk, ActivityLevel.Moderate.description),
            Triple(ActivityLevel.High, Icons.AutoMirrored.Outlined.DirectionsRun, ActivityLevel.High.description)
        )

        options.forEach { (level, icon, description) ->
            val isSelected = selectedActivityLevel == level
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                animationSpec = tween(200),
                label = "activityBorder"
            )
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
                animationSpec = tween(200),
                label = "activityBg"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectActivityLevel(level) },
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = level.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectActivityLevel(level) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HouseholdToggleCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
        animationSpec = tween(200),
        label = "toggleBorder"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "toggleBg"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExistingPetsSelector(
    petTypes: List<String>,
    onAddPetType: (String) -> Unit,
    onRemovePetType: (String) -> Unit,
    onOpenAddPetDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickSuggestions = listOf("Dog", "Cat", "Bird", "Rabbit", "Fish")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Existing Pets in Household",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Let us know what pets already live with you to ensure social compatibility (up to 10)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )

        // Quick Suggestion Chips + "+ Other" Modal Opener
        Text(
            text = "Quick suggestions:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickSuggestions.forEach { suggestion ->
                val isAdded = petTypes.any { it.equals(suggestion, ignoreCase = true) }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAdded) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (isAdded) {
                                onRemovePetType(suggestion)
                            } else {
                                onAddPetType(suggestion)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isAdded) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isAdded) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Dedicated "+ Other" custom pet button that opens the modal
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onOpenAddPetDialog)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom pet",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Other",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Pet Types Tag Cloud
        if (petTypes.isNotEmpty()) {
            Text(
                text = "Currently selected (${petTypes.size}/10):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                petTypes.forEach { pet ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pet,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = { onRemovePetType(pet) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove $pet",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            ) {
                Text(
                    text = "No existing pets specified. Tap a suggestion above or tap '+ Other' to add pets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCustomPetDialog(
    onDismissRequest: () -> Unit,
    onAddPet: (String) -> Unit,
    existingPets: List<String>
) {
    var petInput by remember { mutableStateOf("") }
    val popularChoices = listOf("Parrot", "Turtle", "Guinea Pig", "Hamster", "Duck", "Goat")

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Add Custom Pet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Specify any other pet in your home to ensure social compatibility with rescue animals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = petInput,
                    onValueChange = { petInput = it },
                    label = { Text("Pet Type") },
                    placeholder = { Text("e.g. Guinea pig, Hamster, Turtle") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (petInput.isNotEmpty()) {
                            IconButton(onClick = { petInput = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (petInput.isNotBlank()) {
                            onAddPet(petInput.trim())
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Popular choices chips within modal
                Text(
                    text = "Popular choices:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularChoices.forEach { choice ->
                        val isAlreadyAdded = existingPets.any { it.equals(choice, ignoreCase = true) }
                        val isCurrentInput = petInput.equals(choice, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when {
                                isAlreadyAdded -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                                isCurrentInput -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isCurrentInput -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                                }
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(enabled = !isAlreadyAdded) {
                                    petInput = choice
                                }
                        ) {
                            Text(
                                text = if (isAlreadyAdded) "$choice (Added)" else choice,
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    isAlreadyAdded -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    isCurrentInput -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onBackground
                                },
                                fontWeight = if (isCurrentInput) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            if (petInput.isNotBlank()) {
                                onAddPet(petInput.trim())
                            }
                        },
                        enabled = petInput.isNotBlank() && existingPets.size < 10,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Add Pet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
