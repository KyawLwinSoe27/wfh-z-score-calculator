package com.dynaverse.wfhzscorecalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dynaverse.wfhzscorecalculator.model.WfhRecord
import com.dynaverse.wfhzscorecalculator.model.WfhUiState
import com.dynaverse.wfhzscorecalculator.ui.components.AgeStepper
import com.dynaverse.wfhzscorecalculator.ui.components.EditValueDialog
import com.dynaverse.wfhzscorecalculator.ui.components.GenderSegmentedStrip
import com.dynaverse.wfhzscorecalculator.ui.components.HistoryRecordRow
import com.dynaverse.wfhzscorecalculator.ui.components.RecentRecordRow
import com.dynaverse.wfhzscorecalculator.ui.components.WfhBottomNavBar
import com.dynaverse.wfhzscorecalculator.ui.components.WfhMetricCard
import com.dynaverse.wfhzscorecalculator.ui.components.WfhTab
import com.dynaverse.wfhzscorecalculator.ui.components.WfhTopAppBar
import com.dynaverse.wfhzscorecalculator.ui.theme.AppBackground
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOnSurface
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOnSurfaceVariant
import com.dynaverse.wfhzscorecalculator.ui.theme.AppOutline
import com.dynaverse.wfhzscorecalculator.ui.theme.AppSurfaceContainer
import com.dynaverse.wfhzscorecalculator.ui.theme.Primary
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryContainer
import com.dynaverse.wfhzscorecalculator.ui.theme.PrimaryDark
import com.dynaverse.wfhzscorecalculator.viewmodel.WfhViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WfhHomeScreen(viewModel: WfhViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var editingHeight by remember { mutableStateOf(false) }
    var editingWeight by remember { mutableStateOf(false) }
    var editingAge by remember { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(WfhTab.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        topBar = { WfhTopAppBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            WfhBottomNavBar(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it }
            )
        }
    ) { padding ->
        when (selectedTab) {
            WfhTab.HOME -> HomeContent(
                state = state,
                viewModel = viewModel,
                padding = padding,
                onViewAll = { selectedTab = WfhTab.HISTORY },
                onEditHeight = { editingHeight = true },
                onEditWeight = { editingWeight = true },
                onEditAge = { editingAge = true },
                onChildNameChanged = viewModel::onChildNameEdited,
                onRecordClick = viewModel::onRecordSelected
            )
            WfhTab.HISTORY -> HistoryContent(
                records = state.records,
                padding = padding,
                onRecordClick = viewModel::onRecordSelected
            )
        }
    }

    if (editingHeight) {
        EditValueDialog(
            title = "Height",
            unit = "cm",
            currentValue = state.heightCm,
            onDismiss = { editingHeight = false },
            onConfirm = { value ->
                viewModel.onHeightEdited(value)
                editingHeight = false
            }
        )
    }
    if (editingWeight) {
        EditValueDialog(
            title = "Weight",
            unit = "kg",
            currentValue = state.weightKg,
            onDismiss = { editingWeight = false },
            onConfirm = { value ->
                viewModel.onWeightEdited(value)
                editingWeight = false
            }
        )
    }
    if (editingAge) {
        EditValueDialog(
            title = "Age",
            unit = "month",
            currentValue = state.ageMonths.toDouble(),
            onDismiss = { editingAge = false },
            onConfirm = { value ->
                viewModel.onAgeEdited(value)
                editingAge = false
            },
            asInteger = true
        )
    }

    state.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::onErrorMessageShown,
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(text = "Check measurement", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = { Text(text = message, fontSize = 13.sp, lineHeight = 18.sp) },
            confirmButton = {
                TextButton(onClick = viewModel::onErrorMessageShown) { Text("OK") }
            }
        )
    }

    val result = state.result
    if (state.showResultSheet && result != null) {
        WfhResultBottomSheet(
            result = result,
            onDismiss = viewModel::onDismissResult,
            onSave = if (state.isViewingHistory) null else viewModel::onSaveResult
        )
    }

    LaunchedEffect(state.confirmationId) {
        if (state.confirmationId > 0) {
            snackbarHostState.showSnackbar(
                message = "Record saved successfully",
                withDismissAction = true
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: WfhUiState,
    viewModel: WfhViewModel,
    padding: PaddingValues,
    onViewAll: () -> Unit,
    onEditHeight: () -> Unit,
    onEditWeight: () -> Unit,
    onEditAge: () -> Unit,
    onChildNameChanged: (String) -> Unit,
    onRecordClick: (WfhRecord) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        MeasurementCard(
            state = state,
            onGenderSelected = viewModel::onGenderSelected,
            onAgeIncrement = viewModel::onAgeIncrement,
            onAgeDecrement = viewModel::onAgeDecrement,
            onHeightIncrement = viewModel::onHeightIncrement,
            onHeightDecrement = viewModel::onHeightDecrement,
            onWeightIncrement = viewModel::onWeightIncrement,
            onWeightDecrement = viewModel::onWeightDecrement,
            onEditHeight = onEditHeight,
            onEditWeight = onEditWeight,
            onEditAge = onEditAge,
            onChildNameChanged = onChildNameChanged,
            onCalculate = viewModel::onCalculate
        )
        RecentRecordsCard(
            records = state.records,
            onViewAll = onViewAll,
            onRecordClick = onRecordClick
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun HistoryContent(
    records: List<WfhRecord>,
    padding: PaddingValues,
    onRecordClick: (WfhRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0D0F381E)),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFECF4EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Saved Records",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppOnSurface
                    )
                    Spacer(Modifier.weight(1f))
                    if (records.isNotEmpty()) {
                        Text(
                            text = records.size.toString() + " saved",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppOutline,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFF1F4F0))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                if (records.isEmpty()) {
                    Text(
                        text = "No saved records yet. Calculate a measurement on the Home tab and tap \"Save to Records\".",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = AppOutline
                    )
                } else {
                    Column {
                        records.forEachIndexed { index, record ->
                            HistoryRecordRow(
                                record = record,
                                onClick = { onRecordClick(record) }
                            )
                            if (index != records.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x0A0F381E))
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MeasurementCard(
    state: WfhUiState,
    onGenderSelected: (Boolean) -> Unit,
    onAgeIncrement: () -> Unit,
    onAgeDecrement: () -> Unit,
    onHeightIncrement: () -> Unit,
    onHeightDecrement: () -> Unit,
    onWeightIncrement: () -> Unit,
    onWeightDecrement: () -> Unit,
    onEditHeight: () -> Unit,
    onEditWeight: () -> Unit,
    onEditAge: () -> Unit,
    onChildNameChanged: (String) -> Unit,
    onCalculate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0D0F381E)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFECF4EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.EditNote,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Today's Measurement",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppOnSurface
                        )
                        Text(
                            text = "Rapid Pediatric Entry",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppOutline
                        )
                    }
                }
                Text(
                    text = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date()),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF276749),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFEAF5EC))
                        .border(1.dp, Color(0xFFDDEFDD), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            OutlinedTextField(
                value = state.childName,
                onValueChange = onChildNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Child name (optional)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppSurfaceContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0D0F381E))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GenderSegmentedStrip(
                        isBoy = state.isBoy,
                        onSelected = onGenderSelected,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0x140F381E))
                    )
                    Spacer(Modifier.width(12.dp))
                    AgeStepper(
                        ageMonths = state.ageMonths,
                        onDecrement = onAgeDecrement,
                        onIncrement = onAgeIncrement,
                        onEditRequested = onEditAge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WfhMetricCard(
                    title = "Height",
                    unit = "cm",
                    stepLabel = "±0.5",
                    icon = Icons.Filled.Straighten,
                    value = state.heightCm,
                    formatDecimals = 1,
                    onIncrement = onHeightIncrement,
                    onDecrement = onHeightDecrement,
                    onEditRequested = onEditHeight,
                    modifier = Modifier.weight(1f)
                )
                WfhMetricCard(
                    title = "Weight",
                    unit = "kg",
                    stepLabel = "±0.1",
                    icon = Icons.Filled.Scale,
                    value = state.weightKg,
                    formatDecimals = 1,
                    onIncrement = onWeightIncrement,
                    onDecrement = onWeightDecrement,
                    onEditRequested = onEditWeight,
                    modifier = Modifier.weight(1f)
                )
            }
            CalculateButton(onClick = onCalculate)
        }
    }
}

@Composable
private fun CalculateButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(listOf(PrimaryDark, Color(0xFF2A5C3B)))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Calculate",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RecentRecordsCard(
    records: List<WfhRecord>,
    onViewAll: () -> Unit,
    onRecordClick: (WfhRecord) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0D0F381E)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFECF4EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Recent Records",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppOnSurface
                    )
                }
                if (records.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFEAF5EC))
                                .border(1.dp, Color(0xFFDDEFDD), CircleShape)
                                .clickable(onClick = onViewAll)
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF276749)
                            )
                        Spacer(Modifier.width(4.dp))
                        androidx.compose.material3.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF276749),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            if (records.isEmpty()) {
                Text(
                    text = "No records yet. Calculate a measurement and save it to see it here.",
                    fontSize = 12.sp,
                    color = AppOutline
                )
            } else {
                    Column {
                        val shown = records.take(4)
                        shown.forEachIndexed { index, record ->
                            RecentRecordRow(
                                record = record,
                                onClick = { onRecordClick(record) }
                            )
                        if (index != shown.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0x0A0F381E))
                            )
                        }
                    }
                }
            }
        }
    }
}
