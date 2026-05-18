package com.esmanureral.neurostage.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.ScanDimens

private val scanLineColor = Color(0xFF38BDF8)
private val scanGlowColor = Color(0x6638BDF8)
private val frameColor = NeurostageBrandBlue.copy(alpha = 0.55f)
@Composable
fun MriScanningImage(
    bitmap: Bitmap,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = ScanDimens.previewImageCorner,
) {
    val infinite = rememberInfiniteTransition(label = "mriScan")
    val scanFraction by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scanLine",
    )
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 2.dp,
                color = frameColor.copy(alpha = pulseAlpha),
                shape = RoundedCornerShape(cornerRadius),
            )
            .background(Color(0xFF0F172A)),
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
            contentScale = ContentScale.Fit,
            alpha = 0.92f,
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val y = h * scanFraction
            val bandHeight = h * 0.14f
            val edgeGlow = 32.dp.toPx()

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(scanLineColor.copy(alpha = 0.28f), Color.Transparent),
                    startY = 0f,
                    endY = edgeGlow,
                ),
                size = Size(w, edgeGlow),
            )
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, scanLineColor.copy(alpha = 0.28f)),
                    startY = h - edgeGlow,
                    endY = h,
                ),
                topLeft = Offset(0f, h - edgeGlow),
                size = Size(w, edgeGlow),
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(scanLineColor.copy(alpha = 0.22f), Color.Transparent),
                    startX = 0f,
                    endX = edgeGlow,
                ),
                size = Size(edgeGlow, h),
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, scanLineColor.copy(alpha = 0.22f)),
                    startX = w - edgeGlow,
                    endX = w,
                ),
                topLeft = Offset(w - edgeGlow, 0f),
                size = Size(edgeGlow, h),
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        scanGlowColor.copy(alpha = 0.15f),
                        scanGlowColor,
                        scanGlowColor.copy(alpha = 0.15f),
                        Color.Transparent,
                    ),
                    startY = y - bandHeight,
                    endY = y + bandHeight,
                ),
                topLeft = Offset(0f, (y - bandHeight).coerceAtLeast(0f)),
                size = Size(w, bandHeight * 2f),
            )

            drawLine(
                color = scanLineColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 3.dp.toPx(),
            )

            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(0f, y - 1.dp.toPx()),
                end = Offset(w, y - 1.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
            )

            val bracket = 24.dp.toPx()
            val stroke = 2.5.dp.toPx()
            val inset = 12.dp.toPx()
            val bracketColor = scanLineColor.copy(alpha = 0.8f)
            listOf(
                Offset(inset, inset) to Offset(inset + bracket, inset),
                Offset(inset, inset) to Offset(inset, inset + bracket),
                Offset(w - inset, inset) to Offset(w - inset - bracket, inset),
                Offset(w - inset, inset) to Offset(w - inset, inset + bracket),
                Offset(inset, h - inset) to Offset(inset + bracket, h - inset),
                Offset(inset, h - inset) to Offset(inset, h - inset - bracket),
                Offset(w - inset, h - inset) to Offset(w - inset - bracket, h - inset),
                Offset(w - inset, h - inset) to Offset(w - inset, h - inset - bracket),
            ).forEach { (from, to) ->
                drawLine(
                    color = bracketColor,
                    start = from,
                    end = to,
                    strokeWidth = stroke,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
            }
        }
    }
}
