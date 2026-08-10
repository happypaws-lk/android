package lk.happypaws.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lk.happypaws.app.util.rememberIsKeyboardVisible

@Composable
fun CollapsibleHeader(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isKeyboardVisible = rememberIsKeyboardVisible()

    AnimatedVisibility(
        visible = !isKeyboardVisible,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}
