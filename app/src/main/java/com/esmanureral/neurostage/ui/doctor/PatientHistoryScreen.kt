package com.esmanureral.neurostage.ui.doctor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size as GeometrySize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.scans.ScanRecord
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsCompareGold
import com.esmanureral.neurostage.ui.theme.NsDoctorAccentBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorScaffoldBg
import com.esmanureral.neurostage.ui.theme.NsGray400
import com.esmanureral.neurostage.ui.theme.NsGray600
import com.esmanureral.neurostage.ui.theme.NsGray700
import com.esmanureral.neurostage.ui.theme.NsGray800
import com.esmanureral.neurostage.ui.theme.NsGray900
import com.esmanureral.neurostage.ui.theme.NsGray300
import com.esmanureral.neurostage.ui.theme.NsGraySlateBar
import com.esmanureral.neurostage.ui.theme.NsPatientStageBadge
import com.esmanureral.neurostage.ui.theme.NsRose50
import com.esmanureral.neurostage.ui.theme.NsSlate50
import com.esmanureral.neurostage.ui.theme.NsSlate100
import com.esmanureral.neurostage.ui.theme.NsStatusError
import com.esmanureral.neurostage.ui.theme.NsWhite
import com.esmanureral.neurostage.util.Constants
import com.esmanureral.neurostage.xai.parseAiReportBlocks
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.esmanureral.neurostage.ui.theme.NsChipIndigoBg

private val waveBottomShape: Shape = object : Shape {
    override fun createOutline(
        size: GeometrySize,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveDepth = with(density) { 20.dp.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - waveDepth)
            quadraticTo(size.width / 2f, size.height + waveDepth, 0f, size.height - waveDepth)
            close()
        }
        return Outline.Generic(path)
    }
}

private val df = SimpleDateFormat(
    Constants.Format.SCAN_DATETIME,
    Locale(Constants.LocaleConfig.LANG_TR, Constants.LocaleConfig.REGION_TR),
)

private fun stageBadgeColor(stageIndex: Int, storedLabel: String, labels: Array<String>): Pair<Color, Color> =
    when (stageIndex) {
        2 -> NsPatientStageBadge.healthy
        3 -> NsPatientStageBadge.veryMild
        0 -> NsPatientStageBadge.mild
        1 -> NsPatientStageBadge.moderateOrUnknown
        else -> legacyStageBadgeColor(labels.getOrNull(stageIndex) ?: storedLabel)
    }

/** Eski kayıtlardaki metin etiketleri için geriye dönük uyumluluk. */
private fun legacyStageBadgeColor(label: String): Pair<Color, Color> = when (label) {
    Constants.MriStageLabel.HEALTHY, "Sağlıklı", "Demanssız" -> NsPatientStageBadge.healthy
    Constants.MriStageLabel.VERY_MILD, "Çok hafif evre", "Çok Hafif Alzheimer" -> NsPatientStageBadge.veryMild
    Constants.MriStageLabel.MILD, "Hafif evre", "Hafif Alzheimer" -> NsPatientStageBadge.mild
    Constants.MriStageLabel.MODERATE, "Orta evre", "Orta Evre Alzheimer" -> NsPatientStageBadge.moderateOrUnknown
    else -> NsPatientStageBadge.moderateOrUnknown
}

private fun stageDisplayLabel(stageIndex: Int, storedLabel: String, labels: Array<String>): String =
    labels.getOrNull(stageIndex)?.takeIf { it.isNotEmpty() } ?: storedLabel

@Composable
fun PatientHistoryScreen(
    onBack: () -> Unit,
    onStartScan: (patientId: String) -> Unit,
    viewModel: PatientHistoryViewModel = hiltViewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    var showCompareDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteConfirmPending by remember { mutableStateOf(false) }
    var detailScan by remember { mutableStateOf<ScanRecord?>(null) }

    val patientNameFallback = stringResource(R.string.patient_history_fallback_name)

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(ui.isDeleting, ui.deleteError) {
        if (!deleteConfirmPending || ui.isDeleting) return@LaunchedEffect
        deleteConfirmPending = false
        showDeleteDialog = false
    }

    val canCompare = ui.compareMode && ui.selectedForCompare.size == 2

    val handleBack: () -> Unit = {
        when {
            showCompareDialog -> showCompareDialog = false
            showDeleteDialog && !ui.isDeleting -> showDeleteDialog = false
            detailScan != null -> detailScan = null
            ui.selectionMode -> viewModel.setSelectionMode(false)
            ui.compareMode -> viewModel.toggleCompareMode()
            else -> onBack()
        }
    }

    BackHandler(onBack = handleBack)

    if (showDeleteDialog) {
        ScanDeleteConfirmDialog(
            count = ui.selectedScanIds.size,
            isDeleting = ui.isDeleting,
            onDismiss = {
                if (!ui.isDeleting) showDeleteDialog = false
            },
            onConfirm = {
                deleteConfirmPending = true
                viewModel.deleteSelectedScans()
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = NsDoctorScaffoldBg,
        floatingActionButton = {
            if (canCompare) {
                ExtendedFloatingActionButton(
                    onClick = { showCompareDialog = true },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Outlined.CompareArrows,
                            contentDescription = null,
                        )
                    },
                    text = { Text(stringResource(R.string.patient_history_compare)) },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(waveBottomShape)
                    .background(NeurostageBrandBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 20.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = handleBack) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.doctor_history_cd_back),
                                tint = NsWhite,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                        ) {
                            Text(
                                text = if (ui.selectionMode) {
                                    stringResource(
                                        R.string.patient_list_selected_count,
                                        ui.selectedScanIds.size,
                                    )
                                } else {
                                    ui.patient?.fullName ?: patientNameFallback
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NsWhite,
                            )
                            if (!ui.selectionMode) {
                                ui.patient?.let { p ->
                                    val meta = listOfNotNull(
                                        p.age?.let {
                                            stringResource(R.string.patient_age_years_format, it)
                                        },
                                        p.gender,
                                    ).joinToString(stringResource(R.string.patient_history_meta_separator))
                                    if (meta.isNotEmpty()) {
                                        Text(
                                            meta,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NsWhite.copy(alpha = 0.75f),
                                        )
                                    }
                                }
                            }
                        }

                        if (ui.selectionMode) {
                            IconButton(
                                onClick = { viewModel.selectAllScans() },
                                enabled = !ui.isDeleting,
                            ) {
                                Icon(
                                    Icons.Outlined.SelectAll,
                                    contentDescription = stringResource(R.string.patient_list_select_all),
                                    tint = NsWhite,
                                )
                            }
                            IconButton(
                                onClick = { showDeleteDialog = true },
                                enabled = ui.selectedScanIds.isNotEmpty() && !ui.isDeleting,
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.patient_list_cd_delete_selected),
                                    tint = if (ui.selectedScanIds.isNotEmpty()) {
                                        NsWhite
                                    } else {
                                        NsWhite.copy(alpha = 0.4f)
                                    },
                                )
                            }
                            TextButton(
                                onClick = { viewModel.setSelectionMode(false) },
                                enabled = !ui.isDeleting,
                            ) {
                                Text(
                                    stringResource(R.string.patient_list_cancel_selection),
                                    color = NsWhite,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        } else {
                            IconButton(onClick = { viewModel.load() }) {
                                Icon(
                                    Icons.Outlined.Refresh,
                                    contentDescription = stringResource(R.string.patient_history_cd_refresh),
                                    tint = NsWhite.copy(alpha = 0.8f),
                                )
                            }
                            if (ui.scans.isNotEmpty()) {
                                TextButton(onClick = { viewModel.setSelectionMode(true) }) {
                                    Text(
                                        stringResource(R.string.patient_list_select),
                                        color = NsWhite,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                IconButton(onClick = viewModel::toggleCompareMode) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                                        contentDescription = stringResource(R.string.patient_history_cd_compare),
                                        tint = if (ui.compareMode) NsCompareGold else NsWhite.copy(alpha = 0.8f),
                                    )
                                }
                            }
                        }
                        ui.patient?.id?.let { patientId ->
                            IconButton(
                                onClick = { onStartScan(patientId) },
                                enabled = !ui.isDeleting,
                            ) {
                                Icon(
                                    Icons.Outlined.Upload,
                                    contentDescription = stringResource(R.string.patient_history_cd_new_mr),
                                    tint = NsWhite.copy(alpha = 0.8f),
                                )
                            }
                        }
                    }

                    if (ui.compareMode) {
                        SelectionHintBanner(
                            text = if (ui.selectedForCompare.size < 2) {
                                stringResource(
                                    R.string.patient_history_compare_select_two,
                                    ui.selectedForCompare.size,
                                )
                            } else {
                                stringResource(R.string.patient_history_compare_ready)
                            },
                        )
                    }
                }
            }

            if (ui.scans.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatPill(
                        label = stringResource(R.string.patient_history_stat_total),
                        value = "${ui.scans.size}",
                        modifier = Modifier.weight(1f),
                    )
                    ui.scans.firstOrNull()?.let { latest ->
                        val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
                        val latestLabel = stageDisplayLabel(latest.stageIndex, latest.label, stageLabels)
                        val (bg, fg) = stageBadgeColor(latest.stageIndex, latest.label, stageLabels)
                        StatPill(
                            label = stringResource(R.string.patient_history_stat_last),
                            value = latestLabel,
                            valueColor = fg,
                            bgColor = bg,
                            modifier = Modifier.weight(2f),
                        )
                    }
                }
            }

            if (ui.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NsDoctorAccentBlue)
                }
            }

            ui.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ui.deleteError?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            ui.scanError?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = NsRose50),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            stringResource(R.string.patient_history_scan_error_icon),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Column {
                            Text(
                                stringResource(R.string.patient_history_scan_error_title),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NsStatusError,
                            )
                            Text(it, style = MaterialTheme.typography.bodySmall, color = NsGray400)
                        }
                    }
                }
            }

            if (ui.scans.isEmpty() && !ui.isLoading) {
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        stringResource(R.string.patient_history_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        stringResource(R.string.patient_history_empty_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = NsGray400,
                    )
                }
                Spacer(Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 100.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(ui.scans, key = { it.id }) { scan ->
                        val isSelected = when {
                            ui.selectionMode -> scan.id in ui.selectedScanIds
                            ui.compareMode -> scan.id in ui.selectedForCompare
                            else -> false
                        }
                        TimelineRow(
                            scan = scan,
                            isLast = scan == ui.scans.last(),
                            selectionMode = ui.selectionMode,
                            compareMode = ui.compareMode,
                            isSelected = isSelected,
                            onClick = {
                                when {
                                    ui.selectionMode -> viewModel.toggleScanSelection(scan.id)
                                    ui.compareMode -> viewModel.toggleCompareSelection(scan.id)
                                    else -> detailScan = scan
                                }
                            },
                            onLongClick = {
                                if (!ui.compareMode) {
                                    if (!ui.selectionMode) {
                                        viewModel.setSelectionMode(true)
                                    }
                                    viewModel.toggleScanSelection(scan.id)
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (showCompareDialog) {
        val pair = viewModel.getSelectedScans()
        if (pair != null) {
            CompareDialog(
                a = pair.first,
                b = pair.second,
                onDismiss = { showCompareDialog = false },
            )
        }
    }

    detailScan?.let { scan ->
        ScanDetailBottomSheet(
            scan = scan,
            onDismiss = { detailScan = null },
        )
    }

        DoctorLoadingOverlay(visible = ui.isLoading && ui.patient == null)
    }
}

@Composable
private fun SelectionHintBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(NsWhite.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = NsWhite,
        )
    }
}

@Composable
private fun ScanDeleteConfirmDialog(
    count: Int,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.patient_history_delete_dialog_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(stringResource(R.string.patient_history_delete_dialog_message, count))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isDeleting,
            ) {
                Text(
                    stringResource(R.string.patient_list_delete_dialog_confirm),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) {
                Text(stringResource(R.string.patient_list_delete_dialog_cancel))
            }
        },
    )
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = NeurostageBrandBlue,
    bgColor: Color = NsWhite,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = NsGray400)
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimelineRow(
    scan: ScanRecord,
    isLast: Boolean,
    selectionMode: Boolean,
    compareMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val selectionActive = selectionMode || compareMode
    val confidencePct = (scan.confidence * 100).toInt().coerceIn(0, 100)
    val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
    val stageLabel = stageDisplayLabel(scan.stageIndex, scan.label, stageLabels)
    val (badgeBg, badgeFg) = stageBadgeColor(scan.stageIndex, scan.label, stageLabels)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(28.dp)
                .padding(top = 6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else badgeFg),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(NsGray300),
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, bottom = if (isLast) 4.dp else 14.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected && selectionActive) {
                    NsChipIndigoBg
                } else {
                    NsWhite
                },
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 2.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = stageLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeFg,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = df.format(Date(scan.timestampMs)),
                        style = MaterialTheme.typography.bodySmall,
                        color = NsGray400,
                    )
                    Text(
                        text = stringResource(R.string.doctor_history_confidence, confidencePct),
                        style = MaterialTheme.typography.bodySmall,
                        color = NsGray600,
                        fontWeight = FontWeight.Medium,
                    )
                }
                if (selectionActive) {
                    Checkbox(checked = isSelected, onCheckedChange = { onClick() })
                }
            }
        }
    }
}

@Composable
private fun CompareDialog(a: ScanRecord, b: ScanRecord, onDismiss: () -> Unit) {
    val older = if (a.timestampMs < b.timestampMs) a else b
    val newer = if (a.timestampMs > b.timestampMs) a else b

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.patient_history_close))
            }
        },
        title = {
            Text(
                stringResource(R.string.patient_history_compare_dialog_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CompareSide(
                    label = stringResource(R.string.patient_history_compare_previous),
                    scan = older,
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier
                    .width(1.dp)
                    .height(160.dp)
                    .background(NsGray300))
                CompareSide(
                    label = stringResource(R.string.patient_history_compare_next),
                    scan = newer,
                    modifier = Modifier.weight(1f),
                )
            }
        },
    )
}

@Composable
private fun CompareSide(label: String, scan: ScanRecord, modifier: Modifier = Modifier) {
    val pct = (scan.confidence * 100).toInt().coerceIn(0, 100)
    val stageLabels = stringArrayResource(R.array.dementia_stage_labels)
    val stageLabel = stageDisplayLabel(scan.stageIndex, scan.label, stageLabels)
    val (bg, fg) = stageBadgeColor(scan.stageIndex, scan.label, stageLabels)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = NsGray600)
        Text(
            df.format(Date(scan.timestampMs)),
            style = MaterialTheme.typography.bodySmall,
            color = NsGray400
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(bg)
                .padding(horizontal = 6.dp, vertical = 3.dp),
        ) {
            Text(
                stageLabel,
                style = MaterialTheme.typography.labelSmall,
                color = fg,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            stringResource(R.string.doctor_history_confidence, pct),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


@Composable
private fun scanUrgencyBadge(stageIndex: Int): Pair<String, Color> {
    val lines = stringArrayResource(
        when (stageIndex) {
            2 -> R.array.patient_hist_clinic_idx2
            3 -> R.array.patient_hist_clinic_idx3
            0 -> R.array.patient_hist_clinic_idx0
            else -> R.array.patient_hist_clinic_else
        },
    )
    require(lines.size >= 7)
    val urgencyColor = when (stageIndex) {
        2 -> NsPatientStageBadge.healthy.second
        3 -> NsPatientStageBadge.veryMild.second
        0 -> NsPatientStageBadge.mild.second
        else -> NsPatientStageBadge.moderateOrUnknown.second
    }
    return lines[6] to urgencyColor
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanDetailBottomSheet(
    scan: ScanRecord,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confidence = (scan.confidence * 100).toInt().coerceIn(0, 100)
    val scoreLabels = stringArrayResource(R.array.dementia_stage_labels)
    val stageLabel = stageDisplayLabel(scan.stageIndex, scan.label, scoreLabels)
    val (badgeBg, badgeFg) = stageBadgeColor(scan.stageIndex, scan.label, scoreLabels)
    val (urgencyLabel, urgencyColor) = scanUrgencyBadge(scan.stageIndex)
    val knownHeadings = stringArrayResource(R.array.xai_report_headings).toList()

    val aiBlocks = scan.aiReport?.let { parseAiReportBlocks(it, knownHeadings) }
    val summaryHint = stringResource(R.string.home_screen_xai_summary_keyword)
    val summaryBlock =
        aiBlocks?.firstOrNull { it.first?.contains(summaryHint, ignoreCase = true) == true }
            ?: aiBlocks?.firstOrNull()
    val otherBlocks = aiBlocks?.filter { it != summaryBlock }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NsSlate50,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.patient_history_sheet_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NsGray900,
                    )
                    Text(
                        df.format(Date(scan.timestampMs)),
                        style = MaterialTheme.typography.bodySmall,
                        color = NsGray400,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(urgencyColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        urgencyLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = urgencyColor,
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NsWhite),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                progress = { scan.confidence.coerceIn(0f, 1f) },
                                modifier = Modifier.size(72.dp),
                                strokeWidth = 7.dp,
                                color = badgeFg,
                                trackColor = badgeBg,
                                strokeCap = StrokeCap.Round,
                            )
                            Text(
                                stringResource(R.string.doctor_result_score_percent, confidence),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = badgeFg,
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    stageLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = badgeFg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = NsSlate100)

                    if (summaryBlock != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = NeurostageBrandBlue,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                summaryBlock.first
                                    ?: stringResource(R.string.patient_history_sheet_ai_fallback),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeurostageBrandBlue,
                            )
                        }
                        Text(
                            summaryBlock.second.replace(
                                stringResource(R.string.patient_history_markdown_bold),
                                ""
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = NsGray700,
                            lineHeight = 22.sp,
                        )
                    }
                }
            }

            if (scan.scores.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NsWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.patient_history_sheet_model_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = NsGray600,
                            fontWeight = FontWeight.SemiBold,
                        )
                        scan.scores.forEachIndexed { i, score ->
                            val isSelected = i == scan.stageIndex
                            val barColor = if (isSelected) {
                                NeurostageBrandBlue
                            } else {
                                NsGraySlateBar
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(barColor),
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        scoreLabels.getOrNull(i)
                                            ?: stringResource(
                                                R.string.patient_history_model_class_fallback,
                                                i
                                            ),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) NsGray900 else NsGray600,
                                    )
                                    Text(
                                        stringResource(R.string.patient_history_score_decimal).format(
                                            score * 100
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) barColor else NsGray400,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(NsSlate100),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(score.coerceIn(0f, 1f))
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(99.dp))
                                            .background(barColor),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (otherBlocks != null && otherBlocks.isNotEmpty()) {
                otherBlocks.forEach { (title, content) ->
                    val markdownBold = stringResource(R.string.patient_history_markdown_bold)
                    val cleanContent =
                        content.replace(markdownBold, "").replace(Regex("(?m)^- "), "• ")
                            .replace(Regex("(?m)^\\* "), "• ")
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NsSlate50),
                        elevation = CardDefaults.cardElevation(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                title?.uppercase()
                                    ?: stringResource(R.string.patient_history_sheet_block_fallback).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = NeurostageBrandBlue,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            if (cleanContent.isNotBlank()) {
                                Text(
                                    cleanContent,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NsGray800,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            Text(
                stringResource(R.string.patient_history_sheet_footer_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = NsGray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 18.sp,
            )
        }
    }
}
