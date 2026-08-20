package lk.happypaws.app.ui.post.rescue

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import lk.happypaws.app.ui.components.HappyPawsBackButton
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescueReportFlow(
    onNavigateBack: () -> Unit,
    viewModel: CreateRescueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableStateOf(1) }
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Rescue") },
                navigationIcon = {
                    HappyPawsBackButton(onClick = {
                        if (currentStep > 1 && currentStep < 4) {
                            currentStep -= 1
                        } else {
                            onNavigateBack()
                        }
                    })
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Crossfade(targetState = currentStep, label = "StepTransition") { step ->
                when (step) {
                    1 -> PhotoCaptureStep(
                        uiState = uiState,
                        onPhotoSelected = { uri -> viewModel.updatePhoto(uri) },
                        onNext = { currentStep = 2 }
                    )
                    2 -> LocationDetectionStep(
                        uiState = uiState,
                        onLocationFound = { lat, lng, name -> viewModel.updateLocation(lat, lng, name) },
                        onNext = { currentStep = 3 }
                    )
                    3 -> DetailsStep(
                        uiState = uiState,
                        onDetailsChanged = { title, desc, notes -> viewModel.updateDetails(title, desc, notes) },
                        onTagToggled = { viewModel.toggleTag(it) },
                        onSubmit = {
                            viewModel.submitRescueCase()
                            currentStep = 4
                        }
                    )
                    4 -> SubmissionStep(
                        uiState = uiState,
                        onDone = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoCaptureStep(
    uiState: CreateRescueState,
    onPhotoSelected: (Uri) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempUri != null) {
            onPhotoSelected(tempUri!!)
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Step 1: Photo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("A photo is strictly required for AI triage.", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(uiState.photoUri),
                contentDescription = null,
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008585))
            ) {
                Text("Continue to Location")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        try {
                            val file = File.createTempFile("rescue_", ".jpg", context.cacheDir)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            tempUri = uri
                            cameraLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open camera: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Camera")
                    }
                }
                
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Gallery")
                    }
                }
            }
        }
        
        if (uiState.error != null && uiState.photoUri == null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(uiState.error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun LocationDetectionStep(
    uiState: CreateRescueState,
    onLocationFound: (Double, Double, String) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var isFetching by remember { mutableStateOf(false) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            fetchLocation(context, onLocationFound, { isFetching = it })
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.latitude == null) {
            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (hasFine || hasCoarse) {
                fetchLocation(context, onLocationFound, { isFetching = it })
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF008585))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Step 2: Location", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isFetching) {
            CircularProgressIndicator(color = Color(0xFF008585))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Detecting your location...")
        } else if (uiState.latitude != null) {
            Text("Location found: ${uiState.locationName}", fontWeight = FontWeight.SemiBold)
            Text("Coordinates: ${uiState.latitude}, ${uiState.longitude}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008585))
            ) {
                Text("Continue to Details")
            }
        } else {
            Text("Location permission is required to automatically detect the emergency location.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                locationPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }) {
                Text("Grant Permission")
            }
        }
    }
}

private fun fetchLocation(
    context: Context,
    onLocationFound: (Double, Double, String) -> Unit,
    setFetching: (Boolean) -> Unit
) {
    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) {
        setFetching(false)
        return
    }

    setFetching(true)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val priority = if (hasFine) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY
    val locationRequest = LocationRequest.Builder(priority, 100).build()
    
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
            if (lastLoc != null) {
                resolveLocation(context, lastLoc.latitude, lastLoc.longitude, onLocationFound)
                setFetching(false)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    fusedLocationClient.removeLocationUpdates(this)
                    resolveLocation(context, location.latitude, location.longitude, onLocationFound)
                    setFetching(false)
                }
            }
        }, Looper.getMainLooper())
    } catch (e: SecurityException) {
        setFetching(false)
    }
}

private fun resolveLocation(
    context: Context,
    latitude: Double,
    longitude: Double,
    onLocationFound: (Double, Double, String) -> Unit
) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val name = addresses?.firstOrNull()?.let { 
            it.featureName ?: it.locality ?: it.subAdminArea ?: "Unknown Location" 
        } ?: "Unknown Location"
        onLocationFound(latitude, longitude, name)
    } catch (e: Exception) {
        onLocationFound(latitude, longitude, "Unknown Location")
    }
}

@Composable
fun DetailsStep(
    uiState: CreateRescueState,
    onDetailsChanged: (String, String, String) -> Unit,
    onTagToggled: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState)
    ) {
        Text("Step 3: Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { onDetailsChanged(it, uiState.description, uiState.conditionNotes) },
            label = { Text("Headline / Title (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { onDetailsChanged(uiState.title, it, uiState.conditionNotes) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.conditionNotes,
            onValueChange = { onDetailsChanged(uiState.title, uiState.description, it) },
            label = { Text("Medical / Condition Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tags", fontWeight = FontWeight.SemiBold)
        val predefinedTags = listOf("Urgent", "Injured", "Stray", "Needs Transport")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            predefinedTags.forEach { tag ->
                val isSelected = uiState.tags.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagToggled(tag) },
                    label = { Text(tag) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008585))
        ) {
            Text("Submit Rescue Case")
        }
    }
}

@Composable
fun SubmissionStep(
    uiState: CreateRescueState,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color(0xFF008585), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("AI Triage in progress...", style = MaterialTheme.typography.titleMedium)
            Text("Please wait while we process the emergency.")
        } else if (uiState.successResponse != null) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Green)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rescue Case Submitted", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("The case is now Pending Approval.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI Triage Result:", fontWeight = FontWeight.SemiBold)
                    Text(uiState.successResponse.urgency, style = MaterialTheme.typography.headlineMedium, color = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008585))
            ) {
                Text("Return to Home")
            }
        } else if (uiState.error != null) {
            Text("Failed to submit", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onDone) {
                Text("Go Back")
            }
        }
    }
}
