package lk.happypaws.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import lk.happypaws.app.BuildConfig

fun resolveAvatarUrl(avatarKey: String?): String? {
    if (avatarKey.isNullOrBlank()) return null
    if (avatarKey.startsWith("http://") || avatarKey.startsWith("https://")) return avatarKey
    val base = BuildConfig.STORAGE_BASE_URL.trimEnd('/')
    val key = avatarKey.trimStart('/')
    return "$base/$key"
}

fun getAvatarInitials(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return ""
    val parts = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        parts.isNotEmpty() -> parts[0].take(2).uppercase()
        else -> ""
    }
}

@Composable
fun UserAvatar(
    name: String,
    avatarKey: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    val avatarUrl = resolveAvatarUrl(avatarKey)
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "$name's avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    AvatarFallback(name = name, size = size, contentColor = contentColor)
                },
                error = {
                    AvatarFallback(name = name, size = size, contentColor = contentColor)
                }
            )
        } else {
            AvatarFallback(name = name, size = size, contentColor = contentColor)
        }
    }
}

@Composable
private fun AvatarFallback(
    name: String,
    size: Dp,
    contentColor: Color
) {
    val initials = getAvatarInitials(name)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (initials.isNotBlank()) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value * (if (initials.length > 1) 0.38f else 0.42f)).sp,
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor,
                textAlign = TextAlign.Center
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}
