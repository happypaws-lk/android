package lk.happypaws.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Neutral20,
    onSurface = Neutral20,
    /* Other default colors to override
    secondary = PurpleGrey40,
    tertiary = Pink40
    */
)

@Composable
fun HappyPawsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}