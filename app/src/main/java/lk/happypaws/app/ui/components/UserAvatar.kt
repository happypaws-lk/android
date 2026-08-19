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
    val initial = name.trim().take(1).uppercase()
    if (initial.isNotBlank()) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold
            ),
            color = contentColor
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
