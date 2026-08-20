package lk.happypaws.app.ui.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import lk.happypaws.app.ui.profile.components.EditEmailBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import lk.happypaws.app.ui.components.HappyPawsBackButton
import lk.happypaws.app.ui.profile.components.AvatarActionBottomSheet
import lk.happypaws.app.ui.profile.components.ProfileOverviewSection
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAvatarActionSheet by remember { mutableStateOf(false) }
    var rawBitmapToCrop by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Gallery Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        rawBitmapToCrop = bitmap
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Capture Launcher
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            try {
                context.contentResolver.openInputStream(tempCameraUri!!)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        rawBitmapToCrop = bitmap
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchCamera() {
        try {
            val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot access camera storage", Toast.LENGTH_SHORT).show()
        }
    }

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
                        text = "Edit Profile",
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
            val profile = uiState.profile
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                if (profile != null) {
                    // ── New Redesigned Profile Overview Section ──
                    ProfileOverviewSection(
                        profile = profile,
                        selectedImageBitmap = uiState.selectedImageBitmap,
                        onEditPhotoClick = { showAvatarActionSheet = true }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Personal Information Form ──
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Full Name Field
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Full Name") },
                    placeholder = { Text("Enter your full display name") },
                    isError = uiState.nameError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                if (uiState.nameError != null) {
                    Text(
                        text = uiState.nameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "This name appears publicly on rescue posts and adoptions",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 16.dp)
                    )
                }

                // Email Address Field
                OutlinedTextField(
                    value = profile?.email ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Email Address") },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.openEditEmailSheet() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                        disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                    )
                )

                Text(
                    text = "Tap edit icon to change your email address with password verification",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 24.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ── Save Changes Button ──
                Button(
                    onClick = {
                        viewModel.saveChanges {
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    },
                    enabled = !uiState.isSaving && uiState.nameError == null && uiState.name.isNotBlank(),
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
                        Text("Saving Changes...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ── Photo Action Bottom Sheet Modal ──
    if (showAvatarActionSheet) {
        AvatarActionBottomSheet(
            sheetState = sheetState,
            hasAvatar = uiState.selectedImageBitmap != null || uiState.profile?.avatarKey != null,
            onDismissRequest = { showAvatarActionSheet = false },
            onChoosePhoto = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onTakePhoto = { launchCamera() },
            onRemovePhoto = {
                if (uiState.selectedImageBitmap != null) {
                    viewModel.clearSelectedImage()
                } else if (uiState.profile?.avatarKey != null) {
                    showDeleteConfirmDialog = true
                }
            }
        )
    }

    // ── Image Cropping Dialog ──
    rawBitmapToCrop?.let { raw ->
        lk.happypaws.app.ui.profile.components.ImageCropDialog(
            rawBitmap = raw,
            onDismissRequest = { rawBitmapToCrop = null },
            onCropApplied = { croppedBitmap, bytes ->
                rawBitmapToCrop = null
                viewModel.onImageSelected(bytes, croppedBitmap, "image/jpeg")
            }
        )
    }

    // ── Remove Avatar Confirmation Dialog ──
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Remove Profile Photo", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to remove your profile photo? Your avatar will revert to your name initials.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteAvatar {
                            Toast.makeText(context, "Profile photo removed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove Photo", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Edit Email Bottom Sheet Modal ──
    if (uiState.showEditEmailSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        EditEmailBottomSheet(
            sheetState = sheetState,
            step = uiState.emailChangeStep,
            newEmail = uiState.newEmail,
            currentPassword = uiState.currentPassword,
            code = uiState.emailOtpCode,
            isLoading = uiState.isEmailSubmitting,
            errorMessage = uiState.emailSheetError,
            onNewEmailChange = viewModel::onNewEmailChange,
            onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
            onCodeChange = viewModel::onEmailOtpCodeChange,
            onRequestCode = viewModel::requestEmailChange,
            onConfirmEmailChange = {
                viewModel.confirmEmailChange { updatedEmail ->
                    Toast.makeText(context, "Email address updated successfully!", Toast.LENGTH_LONG).show()
                }
            },
            onDismiss = viewModel::dismissEditEmailSheet
        )
    }
}
