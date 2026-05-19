package com.esmanureral.neurostage.ui.patient.games.colormatch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchCatalog
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchColor
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchLevelDef
import com.esmanureral.neurostage.ui.patient.games.GameScreenTopBar
import com.esmanureral.neurostage.ui.patient.games.InstructionBox
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorMatchGameScreen(
    stageIndex: Int?,
    onBack: () -> Unit,
    viewModel: ColorMatchViewModel = hiltViewModel(),
) {
    val sound = rememberColorMatchSound()
    val dragHolder = remember { ColorMatchDragHolder() }
    val accent = colorResource(R.color.color_match_accent)

    val level = viewModel.currentLevel
    var roundSeed by remember { mutableIntStateOf(0) }
    var round by remember(level, roundSeed) {
        mutableStateOf(ColorMatchCatalog.buildRound(level, roundSeed))
    }
    val lockedSlots = remember { mutableStateMapOf<Int, ColorMatchColor>() }
    var poolTokens by remember(round) {
        mutableStateOf(
            round.poolTokens.map { ColorPoolToken(it.id, it.color, inPool = true) },
        )
    }
    var showLevelDialog by remember { mutableStateOf(false) }

    fun loadRound(
        targetLevel: ColorMatchLevelDef = viewModel.currentLevel,
        newSeed: Boolean = true
    ) {
        if (newSeed) roundSeed++
        val built = ColorMatchCatalog.buildRound(targetLevel, roundSeed)
        round = built
        lockedSlots.clear()
        poolTokens = built.poolTokens.map { ColorPoolToken(it.id, it.color, inPool = true) }
        showLevelDialog = false
        dragHolder.clearDrag()
        dragHolder.slotBounds.clear()
    }

    LaunchedEffect(stageIndex) {
        if (stageIndex == PatientStage.MODERATE_DEMENTIA) {
            viewModel.restartFromLevelOne()
        }
    }

    LaunchedEffect(viewModel.levelIndex) {
        loadRound(viewModel.currentLevel, newSeed = true)
    }

    fun onRoundComplete() {
        sound.playShine()
        showLevelDialog = true
    }

    fun tryPlace(tokenId: Int, slotIndex: Int?): ColorMatchPlaceResult {
        if (slotIndex == null) return ColorMatchPlaceResult.NO_SLOT
        if (slotIndex in lockedSlots) return ColorMatchPlaceResult.WRONG_SLOT
        val token = poolTokens.firstOrNull { it.id == tokenId && it.inPool }
            ?: return ColorMatchPlaceResult.NO_SLOT
        if (round.template[slotIndex] != token.color) return ColorMatchPlaceResult.WRONG_SLOT
        lockedSlots[slotIndex] = token.color
        poolTokens = poolTokens.map {
            if (it.id == tokenId) it.copy(inPool = false) else it
        }
        if (lockedSlots.size == round.slotCount) {
            onRoundComplete()
        }
        return ColorMatchPlaceResult.ACCEPTED
    }

    Scaffold(
        containerColor = PatientColors.gameBackgroundCream,
        topBar = {
            GameScreenTopBar(
                title = stringResource(R.string.patient_game_color_match_short),
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PatientDimens.gameHubScreenPadding),
            ) {
                Spacer(modifier = Modifier.height(if (level.compact) 6.dp else PatientDimens.gameHubSectionGap))
                Text(
                    text = stringResource(R.string.color_match_level_title, level.displayLevel),
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = PatientDimens.gameHubGreetingSubtitleSize,
                )
                if (!level.compact) {
                    Spacer(modifier = Modifier.height(6.dp))
                    InstructionBox(
                        text = stringResource(level.instructionRes),
                        accentColor = accent,
                        backgroundColor = colorResource(R.color.color_match_instruction_bg),
                    )
                }
                Spacer(modifier = Modifier.height(if (level.compact) 6.dp else PatientDimens.gameHubCardGap))

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    val sizes = remember(
                        maxWidth,
                        level.gridSize,
                        level.discScale,
                        level.poolDiscScale,
                        level.compact,
                    ) {
                        val gap = if (level.compact) 6.dp else 8.dp
                        val gridBlock = maxWidth - gap * (level.gridSize - 1)
                        val cellSize = minOf(
                            gridBlock / level.gridSize,
                            if (level.gridSize == 2) 72.dp else 56.dp,
                        )
                        ColorMatchCellSizes(
                            cellSize = cellSize,
                            poolDiscSize = cellSize * level.poolDiscScale,
                        )
                    }

                    ColorMatchWoodenBoard(
                        modifier = Modifier.fillMaxSize(),
                        level = level,
                        template = round.template,
                        lockedSlots = lockedSlots,
                        poolTokens = poolTokens,
                        dragHolder = dragHolder,
                        cellSize = sizes.cellSize,
                        poolDiscSize = sizes.poolDiscSize,
                        onSnap = { sound.playSnap() },
                        onTryPlace = { tokenId, slot -> tryPlace(tokenId, slot) },
                    )
                }
            }

            ColorMatchLevelCompleteDialog(
                visible = showLevelDialog,
                onContinue = {
                    showLevelDialog = false
                    viewModel.restartFromLevelOne()
                    loadRound(viewModel.currentLevel, newSeed = true)
                },
                onReplay = {
                    showLevelDialog = false
                    loadRound(viewModel.currentLevel, newSeed = true)
                },
                onDismiss = { showLevelDialog = false },
            )
        }
    }
}

private data class ColorMatchCellSizes(
    val cellSize: Dp,
    val poolDiscSize: Dp,
)
