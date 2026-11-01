package com.dynaverse.wfhzscorecalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dynaverse.wfhzscorecalculator.model.NutritionalStatus
import com.dynaverse.wfhzscorecalculator.model.WfhResult
import com.dynaverse.wfhzscorecalculator.ui.components.WfhSpectrumBar
import com.dynaverse.wfhzscorecalculator.ui.components.formatShortSigned
import com.dynaverse.wfhzscorecalculator.ui.components.formatSigned
import com.dynaverse.wfhzscorecalculator.ui.components.formatRecordTimestamp
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOnSurface
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOnSurfaceVariant
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOutline
import com.dynaverse.wfhzscorecalculator.ui.theme.AppSurfaceContainerLow
import com.dynaverse.wfhzscorecalculator.ui.theme.Primary
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryContainer
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryContainer as PrimaryContainerColor
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryDark
import com.dynaverse.wfhzscorecalculator.ui.theme.SamColor
import com.dynaverse.wfhzscorecalculator.ui.theme.MamColor
import com.dynaverse.wfhzscorecalculator.ui.theme.NormalColor
import com.dynaverse.wfhzscorecalculator.ui.theme.AboveMedianColor
import java.util.Locale

private fun NutritionalStatus.takeawayText(): String = when (this) {
    NutritionalStatus.SAM ->
        "Weight is severely low for height (< -3 SD). Immediate clinical assessment and " +
            "therapeutic feeding support are recommended."
    NutritionalStatus.MAM ->
        "Weight is moderately low for height (-3 to -2 SD). Supplemental nutrition support " +
            "and close routine follow-up are advised."
    NutritionalStatus.AT_RISK ->
        "Weight is slightly below the expected range (-2 to -1 SD). Continue monitoring " +
            "growth closely and reinforce balanced nutrition."
    NutritionalStatus.NORMAL ->
        "Child's weight is proportional to height according to WHO Child Growth Standards. " +
            "Growth trajectory remains within safe pediatric percentiles."
    NutritionalStatus.ABOVE_MEDIAN ->
        "Weight is above the WHO median for height. Maintain balanced nutrition and " +
            "monitor the growth trend at regular visits."
}

private fun spectrumBadgeFor(z: Double): Int = when {
    z < -3 -> 0
    z < -2 -> 1
    z < 1 -> 2
    z < 2 -> 3
    else -> 4
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WfhResultBottomSheet(
    result: WfhResult,
    onDismiss: () -> Unit,
    onSave: (() -> Unit)? = null
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC8D1C5))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SheetHeader(onDismiss = onDismiss)
            Spacer(Modifier.height(2.dp))
            StatusBanner(result)
            ZScoreCard(result)
            TakeawayCard(result)
            SheetActions(onDismiss = onDismiss, onSave = onSave)        }
    }
}

@Composable
private fun SheetHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Assessment,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "WFH Z-Score Result",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppOnSurface
            )
            Text(
                text = "Weight-for-Height · WHO Standard",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AppOutline
            )
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFEFEF))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = AppOnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun StatusBanner(result: WfhResult) {
    val status = result.status
    val bannerColor = when (status) {
        NutritionalStatus.SAM -> SamColor
        NutritionalStatus.MAM -> MamColor
        NutritionalStatus.AT_RISK -> MamColor
        NutritionalStatus.NORMAL -> NormalColor
        NutritionalStatus.ABOVE_MEDIAN -> AboveMedianColor
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = bannerColor.copy(alpha = 0.10f),
        border = androidx.compose.foundation.BorderStroke(1.dp, bannerColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(bannerColor)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = shortStatusLabel(status),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = bannerColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Current Age: ${result.ageMonths} month",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppOnSurfaceVariant,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, bannerColor.copy(alpha = 0.25f), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun ZScoreCard(result: WfhResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = AppSurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0D0F381E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Z-SCORE VALUE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = AppOutline
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatSigned(result.zScore),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryDark
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "SD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E4D2B),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDDEFDD))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                                text = (result.childName.takeIf { it.isNotBlank() }?.let { "$it • " } ?: "") +
                                    result.gender.label + " • " + result.ageMonths + " Months • " +
                            String.format(Locale.US, "%.1f", result.heightCm) + " cm • " +
                            String.format(Locale.US, "%.1f", result.weightKg) + " kg",
                        fontSize = 11.sp,
                        color = AppOnSurfaceVariant
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Acute Malnutrition Scale (WHO)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppOnSurfaceVariant
                    )
                    Text(
                        text = formatShortSigned(result.zScore) + " SD (" +
                            shortStatusLabel(result.status) + ")",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFEAF4EB))
                            .border(1.dp, Color(0xFFDDEFDD), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
                WfhSpectrumBar(zScore = result.zScore)
                ThresholdBadges(result.zScore)
            }
            DiagnosticCriteriaCard(result)
        }
    }
}

private fun shortStatusLabel(status: NutritionalStatus): String = when (status) {
    NutritionalStatus.SAM -> "SAM"
    NutritionalStatus.MAM -> "MAM"
    NutritionalStatus.AT_RISK -> "At Risk"
    NutritionalStatus.NORMAL -> "Normal"
    NutritionalStatus.ABOVE_MEDIAN -> "Above Median"
}

@Composable
private fun ThresholdBadges(zScore: Double) {
    val activeIndex = spectrumBadgeFor(zScore)
    val badges = listOf(
        Triple("SAM", "< -3 SD", SamColor),
        Triple("MAM", "-3 to -2", MamColor),
        Triple("NORMAL", "-2 to +1", NormalColor),
        Triple("OVERWT", "> +2 SD", SamColor)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        badges.forEachIndexed { index, (title, subtitle, color) ->
            val active = index == when (activeIndex) {
                0, 1 -> index
                2 -> 2
                else -> 3
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (active) color.copy(alpha = 0.18f) else color.copy(alpha = 0.08f)
                    )
                    .border(
                        1.dp,
                        if (active) color.copy(alpha = 0.5f) else color.copy(alpha = 0.2f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (active) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(Modifier.width(3.dp))
                    }
                    Text(
                        text = title,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (title == "NORMAL") NormalColor else color
                    )
                }
                Text(
                    text = subtitle,
                    fontSize = 8.sp,
                    color = AppOutline
                )
            }
        }
    }
}

@Composable
private fun DiagnosticCriteriaCard(result: WfhResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x140F381E))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "ACUTE MALNUTRITION STATUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = AppOutline
                )
                Text(
                    text = shortStatusLabel(result.status) + " (" + statusDetail(result.status) + ")",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NormalColor,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFDDEFDD), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Diagnostic Criteria",
                    fontSize = 10.sp,
                    color = AppOutline
                )
                Text(
                    text = "SAM: <-3 SD | MAM: -3 to -2 SD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDark,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

private fun statusDetail(status: NutritionalStatus): String = when (status) {
    NutritionalStatus.SAM -> "Severe Wasting"
    NutritionalStatus.MAM -> "Moderate Wasting"
    NutritionalStatus.AT_RISK -> "Mild Wasting"
    NutritionalStatus.NORMAL -> "No Wasting"
    NutritionalStatus.ABOVE_MEDIAN -> "Check Trend"
}

@Composable
private fun TakeawayCard(result: WfhResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppSurfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.HealthAndSafety,
                    contentDescription = null,
                    tint = Color(0xFF0B4A4A),
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Acute Malnutrition Status: " + statusDetail(result.status),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppOnSurface
                )
                Text(
                    text = result.status.takeawayText(),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = AppOnSurfaceVariant
                )
                Text(
                    text = "Reference median for " +
                        result.referenceRow.lengthHeightCm.toString() + " cm: " +
                        String.format(Locale.US, "%.1f", result.referenceRow.median) + " kg",
                    fontSize = 10.sp,
                    color = AppOutline
                )
            }
        }
    }
}

@Composable
private fun SheetActions(
    onDismiss: () -> Unit,
    onSave: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (onSave != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0F1F0))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Done",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppOnSurface
                )
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(listOf(PrimaryDark, Color(0xFF2A5C3B)))
                    )
                    .clickable(onClick = onSave),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Save to Records",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0F1F0))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppOnSurface
                )
            }
        }
    }
}
