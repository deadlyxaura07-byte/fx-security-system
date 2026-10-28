package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityCyan
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXSecurityYellow

@Composable
fun FXCircularGauge(
    score: Int,
    overallStatus: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 14.dp,
    isScanning: Boolean = false
) {
    val targetFraction = (score.coerceIn(0, 100)) / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gaugeProgress"
    )

    val primaryColor = when {
        score >= 85 -> FXEmerald
        score >= 65 -> FXSecurityYellow
        else -> FXSecurityRed
    }

    val secondaryColor = when {
        score >= 85 -> FXSecurityCyan
        score >= 65 -> Color(0xFFEA580C)
        else -> Color(0xFFBE123C)
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track Arc (260 degrees from 140 to 400)
            drawArc(
                color = trackColor,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress Arc
            val sweep = 260f * animatedProgress
            if (sweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(primaryColor, secondaryColor, primaryColor),
                        center = center
                    ),
                    startAngle = 140f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isScanning) "SCAN" else "$score",
                fontSize = if (isScanning) 28.sp else 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = if (isScanning) "DIAGNOSTIC IN PROGRESS" else overallStatus,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "FX INTEGRITY SCORE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
        }
    }
}
