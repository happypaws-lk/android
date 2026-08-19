package lk.happypaws.app.ui.profile.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min

@Composable
fun ImageCropDialog(
    rawBitmap: Bitmap,
    onDismissRequest: () -> Unit,
    onCropApplied: (croppedBitmap: Bitmap, bytes: ByteArray) -> Unit
) {
    val density = LocalDensity.current
    val viewportSizeDp = 260.dp
    val viewportSizePx = with(density) { viewportSizeDp.toPx() }

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF121212)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Text(
                    text = "Crop Profile Photo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Interactive Crop Viewport
                Box(
                    modifier = Modifier
                        .size(viewportSizeDp)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 3.5f)
                                val maxPan = (viewportSizePx * (scale - 0.8f)).coerceAtLeast(0f)
                                offsetX = (offsetX + pan.x).coerceIn(-maxPan, maxPan)
                                offsetY = (offsetY + pan.y).coerceIn(-maxPan, maxPan)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Image with Pan/Zoom transforms
                    Image(
                        bitmap = rawBitmap.asImageBitmap(),
                        contentDescription = "Image to Crop",
                        modifier = Modifier
                            .size(viewportSizeDp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            }
                    )

                    // Circular Stencil Mask & Border Overlay
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        val circlePath = Path().apply {
                            addOval(Rect(center = center, radius = radius))
                        }

                        // Dark mask outside circle
                        clipPath(circlePath, clipOp = ClipOp.Difference) {
                            drawRect(color = Color.Black.copy(alpha = 0.65f))
                        }

                        // Circular white outline
                        drawCircle(
                            color = Color.White.copy(alpha = 0.85f),
                            radius = radius - 1.5f,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                }

                // Zoom Slider & Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Slider(
                            value = scale,
                            onValueChange = { scale = it },
                            valueRange = 1f..3.5f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                val (cropped, bytes) = cropBitmapToSquare(
                                    sourceBitmap = rawBitmap,
                                    scale = scale,
                                    panX = offsetX,
                                    panY = offsetY,
                                    viewportSizePx = viewportSizePx
                                )
                                onCropApplied(cropped, bytes)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apply Crop", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

private fun cropBitmapToSquare(
    sourceBitmap: Bitmap,
    scale: Float,
    panX: Float,
    panY: Float,
    viewportSizePx: Float
): Pair<Bitmap, ByteArray> {
    val bitmapWidth = sourceBitmap.width.toFloat()
    val bitmapHeight = sourceBitmap.height.toFloat()

    val baseScale = max(viewportSizePx / bitmapWidth, viewportSizePx / bitmapHeight)
    val totalScale = baseScale * scale

    val displayedWidth = bitmapWidth * totalScale
    val displayedHeight = bitmapHeight * totalScale

    val centerDisplayX = displayedWidth / 2f - panX
    val centerDisplayY = displayedHeight / 2f - panY

    val viewLeftInDisplay = centerDisplayX - viewportSizePx / 2f
    val viewTopInDisplay = centerDisplayY - viewportSizePx / 2f

    val cropX = (viewLeftInDisplay / totalScale).toInt().coerceIn(0, sourceBitmap.width - 1)
    val cropY = (viewTopInDisplay / totalScale).toInt().coerceIn(0, sourceBitmap.height - 1)

    val cropWidth = (viewportSizePx / totalScale).toInt().coerceIn(1, sourceBitmap.width - cropX)
    val cropHeight = (viewportSizePx / totalScale).toInt().coerceIn(1, sourceBitmap.height - cropY)
    val finalDim = min(cropWidth, cropHeight).coerceAtLeast(1)

    val cropped = Bitmap.createBitmap(sourceBitmap, cropX, cropY, finalDim, finalDim)
    val finalBitmap = Bitmap.createScaledBitmap(cropped, 512, 512, true)

    val stream = ByteArrayOutputStream()
    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 92, stream)
    val bytes = stream.toByteArray()

    return Pair(finalBitmap, bytes)
}
