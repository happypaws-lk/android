package lk.happypaws.app.util

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Returns true if a software keyboard (IME) consuming substantial vertical screen height
 * (greater than [minHeightThresholdDp]) is currently visible.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun rememberIsKeyboardVisible(minHeightThresholdDp: Dp = 100.dp): Boolean {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val minHeightPx = with(density) { minHeightThresholdDp.roundToPx() }
    return WindowInsets.isImeVisible && imeBottom > minHeightPx
}
