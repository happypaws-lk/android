package lk.happypaws.app.ui.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.KycDocumentResponse
import lk.happypaws.app.domain.model.DocumentType
import lk.happypaws.app.ui.components.HappyPawsBackButton
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycVerificationScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KycVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Identity Verification",
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
        when (val state = uiState) {
            is KycUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is KycUiState.UploadForm -> {
                KycUploadForm(
                    state = state,
                    innerPadding = innerPadding,
                    onSelectType = viewModel::setDocumentType,
                    onSetDocument = viewModel::setDocument,
                    onClearDocument = viewModel::clearDocument,
                    onSubmit = viewModel::submit
                )
            }

            is KycUiState.Pending -> {
                KycStatusScreen(
                    innerPadding = innerPadding,
                    doc = state.doc,
                    statusType = KycStatusType.PENDING
                )
            }

            is KycUiState.Verified -> {
                KycStatusScreen(
                    innerPadding = innerPadding,
                    doc = state.doc,
                    statusType = KycStatusType.VERIFIED
                )
            }

            is KycUiState.Rejected -> {
                KycStatusScreen(
                    innerPadding = innerPadding,
                    doc = state.doc,
                    statusType = KycStatusType.REJECTED,
                    onRetry = viewModel::retryUpload
                )
            }
        }
    }
}

private enum class KycStatusType { PENDING, VERIFIED, REJECTED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KycStatusScreen(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    doc: KycDocumentResponse,
    statusType: KycStatusType,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        val (icon, iconTint, iconBg) = when (statusType) {
            KycStatusType.PENDING -> Triple(
                Icons.Default.HourglassTop,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
            KycStatusType.VERIFIED -> Triple(
                Icons.Default.CheckCircle,
                androidx.compose.ui.graphics.Color(0xFF2E7D32),
                androidx.compose.ui.graphics.Color(0xFFE8F5E9)
            )
            KycStatusType.REJECTED -> Triple(
                Icons.Default.Warning,
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
            )
        }

        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(44.dp)
            )
        }

        Text(
            text = when (statusType) {
                KycStatusType.PENDING -> "Verification in Progress"
                KycStatusType.VERIFIED -> "Identity Verified"
                KycStatusType.REJECTED -> "Verification Rejected"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = when (statusType) {
                KycStatusType.PENDING -> "Your document has been submitted and is under review. This usually takes 1–3 business days."
                KycStatusType.VERIFIED -> "Your identity has been successfully verified. You can now access all features available to verified adopters."
                KycStatusType.REJECTED -> "Your document could not be verified. Please review the reason below and resubmit with a clearer document."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )

        val dateLabel = when (statusType) {
            KycStatusType.PENDING -> "Submitted"
            KycStatusType.VERIFIED -> "Verified on"
            KycStatusType.REJECTED -> "Reviewed on"
        }
        val dateValue = when (statusType) {
            KycStatusType.PENDING -> doc.uploadedAt.take(10)
            else -> (doc.reviewedAt ?: doc.uploadedAt).take(10)
        }

        SuggestionChip(
            onClick = {},
            label = {
                Text("$dateLabel: $dateValue", style = MaterialTheme.typography.labelMedium)
            },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        if (statusType == KycStatusType.REJECTED && doc.rejectionReason != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Reason for rejection",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doc.rejectionReason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        if (statusType == KycStatusType.REJECTED && onRetry != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Upload a New Document",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun KycUploadForm(
    state: KycUiState.UploadForm,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onSelectType: (DocumentType) -> Unit,
    onSetDocument: (ByteArray, android.graphics.Bitmap, String) -> Unit,
    onClearDocument: () -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showMediaSheet by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bytes = stream.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) onSetDocument(bytes, bitmap, mimeType)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) onSetDocument(bytes, bitmap, "image/jpeg")
                }
            }
        }
    }

    fun launchCamera() {
        val file = File(context.cacheDir, "kyc_doc_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    if (showMediaSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMediaSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                ListItem(
                    headlineContent = { Text("Take a photo") },
                    leadingContent = {
                        Icon(Icons.Default.CameraAlt, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier.clickable {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMediaSheet = false
                            launchCamera()
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Choose from gallery") },
                    leadingContent = {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier.clickable {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMediaSheet = false
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                )
            }
        }
    }

    val kycDocTypes = listOf(DocumentType.NIC, DocumentType.PASSPORT)
    val canSubmit = state.selectedType != null && state.bytes != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Intro text
        Text(
            text = "Verify your identity to unlock all adopter features. Upload a clear photo of your document.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        // Document type
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Document type",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                kycDocTypes.forEach { docType ->
                    FilterChip(
                        selected = state.selectedType == docType,
                        onClick = { onSelectType(docType) },
                        label = { Text(docType.displayName, style = MaterialTheme.typography.labelMedium) },
                        leadingIcon = if (state.selectedType == docType) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Document upload
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Document photo",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            if (state.bitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Image(
                        bitmap = state.bitmap.asImageBitmap(),
                        contentDescription = "KYC document",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onClearDocument,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                androidx.compose.material3.TextButton(
                    onClick = { showMediaSheet = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Change document",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { showMediaSheet = true }
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Take a photo or upload from gallery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onSubmit,
            enabled = canSubmit && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Submit for Verification",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
