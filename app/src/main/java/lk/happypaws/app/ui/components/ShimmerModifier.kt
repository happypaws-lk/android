package lk.happypaws.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(
    targetValue: Float = 1200f,
    colors: List<Color> = listOf(
        Color(0xFFE5E7EB),
        Color(0xFFF3F4F6),
        Color(0xFFE5E7EB)
    )
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = colors,
        start = Offset(x = translateAnimation - 600f, y = translateAnimation - 600f),
        end = Offset(x = translateAnimation, y = translateAnimation)
    )
}

fun Modifier.shimmerEffect(
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = composed {
    val brush = shimmerBrush()
    this
        .clip(shape)
        .background(brush)
}
