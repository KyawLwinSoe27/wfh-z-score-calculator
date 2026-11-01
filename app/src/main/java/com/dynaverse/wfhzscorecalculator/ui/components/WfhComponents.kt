package com.dynaverse.wfhzscorecalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dynaverse.wfhzscorecalculator.model.Gender
import com.dynaverse.wfhzscorecalculator.model.NutritionalStatus
import com.dynaverse.wfhzscorecalculator.model.WfhRecord
import com.dynaverse.wfhzscorecalculator.model.spectrumFraction
import com.dynaverse.wfhzscorecalculator.ui.theme.Primary
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryContainer
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryDark
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOnSurface
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOnSurfaceVariant
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOutline
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOutlineVariant
import com.dynaverse.wfhzscorecalculator.ui.theme.AppSurfaceContainer
import com.dynaverse.wfhzscorecalculator.ui.theme.AppSurfaceContainerLow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private val statusColorOf = mapOf(
    NutritionalStatus.SAM to com.dynaverse.wfhzscorecalculator.ui.theme.SamColor,
    NutritionalStatus.MAM to com.dynaverse.wfhzscorecalculator.ui.theme.MamColor,
    NutritionalStatus.AT_RISK to com.dynaverse.wfhzscorecalculator.ui.theme.AtRiskColor,
    NutritionalStatus.NORMAL to com.dynaverse.wfhzscorecalculator.ui.theme.NormalColor,
    NutritionalStatus.ABOVE_MEDIAN to com.dynaverse.wfhzscorecalculator.ui.theme.AboveMedianColor
)

fun NutritionalStatus.color(): Color = statusColorOf.getValue(this)

@Composable
fun WfhTopAppBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFFAFBF9)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "WFH Z-Score Calculator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppOnSurface
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x0F0F381E))
            )
        }
    }
}

@Composable
fun GenderSegmentedStrip(
    isBoy: Boolean,
    onSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Wc,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "CHILD GENDER",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = AppOnSurfaceVariant
            )
        }
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0F0F381E))
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                GenderOption(
                    selected = isBoy,
                    label = "Boy",
                    icon = Icons.Filled.Male,
                    onClick = { onSelected(true) },
                    modifier = Modifier.weight(1f)
                )
                GenderOption(
                    selected = !isBoy,
                    label = "Girl",
                    icon = Icons.Filled.Female,
                    onClick = { onSelected(false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GenderOption(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) Primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else AppOnSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Color.White else AppOnSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun AgeStepper(
    ageMonths: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    onEditRequested: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Cake,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "AGE",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = AppOnSurfaceVariant
            )
        }
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0F0F381E))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.Filled.Remove,
                    contentColor = AppOnSurface,
                    containerColor = Color.Transparent,
                    onClick = onDecrement,
                    size = 28
                )
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .then(
                            if (onEditRequested != null) {
                                Modifier.clickable(onClick = onEditRequested)
                            } else Modifier
                        )
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ageMonths",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppOnSurface
                    )
                    Text(
                        text = "month",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppOutline
                    )
                }
                Spacer(Modifier.width(6.dp))
                CircleIconButton(
                    icon = Icons.Filled.Add,
                    contentColor = Color(0xFF1E4D2B),
                    containerColor = PrimaryContainer,
                    onClick = onIncrement,
                    size = 28
                )
            }
        }
    }
}

@Composable
fun WfhMetricCard(
    title: String,
    unit: String,
    stepLabel: String,
    icon: ImageVector,
    value: Double,
    formatDecimals: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onEditRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AppSurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0D0F381E))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppOnSurfaceVariant
                    )
                }
                Text(
                    text = unit,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E4D2B),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDDEFDD))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEditRequested)
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = String.format(Locale.US, "%.${formatDecimals}f", value),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppOnSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppOutline,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.Filled.Remove,
                    contentColor = AppOnSurface,
                    containerColor = Color.White,
                    borderColor = Color(0x140F381E),
                    onClick = onDecrement,
                    size = 36
                )
                Text(
                    text = stepLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppOutline,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(1.dp, Color(0x0A0F381E), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                CircleIconButton(
                    icon = Icons.Filled.Add,
                    contentColor = Color.White,
                    containerColor = PrimaryDark,
                    onClick = onIncrement,
                    size = 36
                )
            }
        }
    }
}

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    size: Int,
    modifier: Modifier = Modifier,
    borderColor: Color? = null
) {
    val shape = CircleShape
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(shape)
            .background(containerColor)
            .then(
                if (borderColor != null) {
                    Modifier.border(1.dp, borderColor, shape)
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size((size / 2.2).dp)
        )
    }
}

@Composable
fun EditValueDialog(
    title: String,
    unit: String,
    currentValue: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    asInteger: Boolean = false
) {
    var text by remember {
        mutableStateOf(
            if (asInteger) currentValue.toInt().toString()
            else String.format(Locale.US, "%.1f", currentValue)
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(text = "Edit $title ($unit)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column {
                Text(
                    text = "Tap to type an exact value.",
                    fontSize = 12.sp,
                    color = AppOutline
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (asInteger) KeyboardType.Number else KeyboardType.Decimal
                    ),
                    suffix = { Text(text = unit, fontSize = 12.sp, color = AppOutline) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun StatusBadge(
    status: NutritionalStatus,
    modifier: Modifier = Modifier
) {
    val color = status.color()
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Text(
            text = status.label.split(" ").first().let {
                when (status) {
                    NutritionalStatus.SAM -> "SAM"
                    NutritionalStatus.MAM -> "MAM"
                    NutritionalStatus.AT_RISK -> "At Risk"
                    NutritionalStatus.NORMAL -> "Normal"
                    NutritionalStatus.ABOVE_MEDIAN -> "Above Median"
                }
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = when (status) {
                NutritionalStatus.AT_RISK -> Color(0xFF8A700F)
                else -> color
            },
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun WfhSpectrumBar(
    zScore: Double,
    modifier: Modifier = Modifier
) {
    val fraction = spectrumFraction(zScore)
    val segments = listOf(
        SpectrumSegment(label = "SAM", weight = 1f, color = com.dynaverse.wfhzscorecalculator.ui.theme.SamColor),
        SpectrumSegment(label = "MAM", weight = 1f, color = com.dynaverse.wfhzscorecalculator.ui.theme.MamColor),
        SpectrumSegment(label = "Normal", weight = 3f, color = com.dynaverse.wfhzscorecalculator.ui.theme.NormalColor),
        SpectrumSegment(label = "Risk", weight = 1f, color = com.dynaverse.wfhzscorecalculator.ui.theme.MamColor),
        SpectrumSegment(label = ">+2", weight = 1f, color = com.dynaverse.wfhzscorecalculator.ui.theme.SamColor)
    )
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val pointerX = maxWidth * fraction
            Column(
                modifier = Modifier.offset(x = pointerX - 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format(Locale.US, "%+.1f", zScore),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Primary)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFFE3E3DD))
            ) {
                segments.forEach { segment ->
                    Box(
                        modifier = Modifier
                            .weight(segment.weight)
                            .fillMaxHeight()
                            .background(segment.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = segment.label,
                            fontSize = 8.sp,
                            lineHeight = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

private data class SpectrumSegment(
    val label: String,
    val weight: Float,
    val color: Color
)

@Composable
fun RecentRecordRow(
    record: WfhRecord,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onClick)
                } else Modifier
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotColor = if (isToday(record.timestamp)) {
            com.dynaverse.wfhzscorecalculator.ui.theme.NormalColor
        } else {
            Color(0xFFB7C3BB)
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = record.childName.ifBlank { formatRecordTimestamp(record.timestamp) },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = (if (record.childName.isBlank()) "" else
                    formatRecordTimestamp(record.timestamp) + " · ") +
                    "Ht: ${String.format(Locale.US, "%.1f", record.heightCm)} cm · " +
                    "Wt: ${String.format(Locale.US, "%.1f", record.weightKg)} kg",
                fontSize = 11.sp,
                color = AppOnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        StatusBadge(status = record.status)
    }
}

enum class WfhTab(val label: String) {
    HOME("Home"),
    HISTORY("History")
}

@Composable
fun HistoryRecordRow(
    record: WfhRecord,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onClick)
                } else Modifier
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(record.status.color())
        )
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = record.childName.ifBlank { formatRecordTimestamp(record.timestamp) },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppOnSurface
            )
            Text(
                text = record.gender.label + " • " + record.ageMonths + " mo • " +
                    formatRecordTimestamp(record.timestamp),
                fontSize = 11.sp,
                color = AppOutline
            )
            Text(
                text = "Ht: " + String.format(Locale.US, "%.1f", record.heightCm) + " cm · " +
                    "Wt: " + String.format(Locale.US, "%.1f", record.weightKg) + " kg · " +
                    "Z: " + String.format(Locale.US, "%+.2f", record.zScore),
                fontSize = 12.sp,
                color = AppOnSurfaceVariant
            )
        }
        Spacer(Modifier.weight(1f))
        StatusBadge(status = record.status)
    }
}

@Composable
fun WfhBottomNavBar(
    selectedTab: WfhTab,
    onSelect: (WfhTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Filled.Home,
                    label = WfhTab.HOME.label,
                    selected = selectedTab == WfhTab.HOME,
                    onClick = { onSelect(WfhTab.HOME) }
                )
                BottomNavItem(
                    icon = Icons.Filled.History,
                    label = WfhTab.HISTORY.label,
                    selected = selectedTab == WfhTab.HISTORY,
                    onClick = { onSelect(WfhTab.HISTORY) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 30.dp)
                .clip(CircleShape)
                .background(if (selected) PrimaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Primary else AppOnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Primary else AppOnSurfaceVariant
        )
    }
}

fun formatRecordTimestamp(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    val nowDay = calendar.get(Calendar.DAY_OF_YEAR)
    val nowYear = calendar.get(Calendar.YEAR)
    calendar.timeInMillis = timestamp
    val sameDay = calendar.get(Calendar.DAY_OF_YEAR) == nowDay &&
        calendar.get(Calendar.YEAR) == nowYear
    val dateLabel = if (sameDay) {
        "Today, " + SimpleDateFormat("MMM d", Locale.US).format(Date(timestamp))
    } else {
        SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(timestamp))
    }
    val timeLabel = SimpleDateFormat("h:mm a", Locale.US).format(Date(timestamp))
    return "$dateLabel · $timeLabel"
}

private fun isToday(timestamp: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun formatSigned(value: Double): String =
    String.format(Locale.US, "%+.2f", value)

fun formatShortSigned(value: Double): String =
    String.format(Locale.US, "%+.1f", formatSignedDouble(value))

private fun formatSignedDouble(value: Double): Double {
    val rounded = (abs(value) * 10).roundToInt() / 10.0
    return if (value < 0) -rounded else rounded
}
