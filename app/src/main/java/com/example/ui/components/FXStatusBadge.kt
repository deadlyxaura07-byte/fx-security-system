package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThreatLevel
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXSecurityYellow
import com.example.ui.theme.FXTitanium

@Composable
fun FXStatusBadge(
    threatLevel: ThreatLevel,
    modifier: Modifier = Modifier,
    customText: String? = null
) {
    val (bgColor, textColor, borderColor, label) = when (threatLevel) {
        ThreatLevel.SAFE -> Quadruple(
            FXEmerald.copy(alpha = 0.15f),
            FXEmerald,
            FXEmerald.copy(alpha = 0.4f),
            customText ?: "SECURE"
        )
        ThreatLevel.LOW -> Quadruple(
            FXTitanium.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.onSurface,
            FXTitanium.copy(alpha = 0.5f),
            customText ?: "LOW RISK"
        )
        ThreatLevel.MODERATE -> Quadruple(
            FXSecurityYellow.copy(alpha = 0.18f),
            FXSecurityYellow,
            FXSecurityYellow.copy(alpha = 0.5f),
            customText ?: "ATTENTION"
        )
        ThreatLevel.CRITICAL -> Quadruple(
            FXSecurityRed.copy(alpha = 0.2f),
            FXSecurityRed,
            FXSecurityRed.copy(alpha = 0.6f),
            customText ?: "CRITICAL"
        )
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
