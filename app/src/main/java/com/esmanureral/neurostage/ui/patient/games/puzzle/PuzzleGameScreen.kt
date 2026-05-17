package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.PuzzleFlowViewModel
import com.esmanureral.neurostage.ui.patient.games.puzzleGridForStage
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleProgressTrack
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGameScreen(
    stageIndex: Int?,
    onBack: () -> Unit,
    onBackToHome: () -> Unit = onBack,
    progressTrack: PuzzleProgressTrack = PuzzleProgressTrack.MildHomeCatalog,
    flowViewModel: PuzzleFlowViewModel = hiltViewModel(),
) {
    val screenSession = rememberPuzzleScreenSession(flowViewModel, stageIndex, progressTrack)
    val gameViewModel = rememberPuzzleGameViewModel(screenSession)
    val clickSound = rememberPuzzleClickSound()
    val dragState = remember { PuzzleDragStateHolder() }

    var showSuccess by remember(screenSession.config.viewModelKey) { mutableStateOf(false) }
    var isAdvancingToNext by remember { mutableStateOf(false) }
    var successSnapshot by remember(screenSession.config.viewModelKey) {
        mutableStateOf<PuzzleSuccessSnapshot?>(null)
    }

    val pieces by gameViewModel.pieces.collectAsStateWithLifecycle()
    val trayOrder by gameViewModel.trayOrder.collectAsStateWithLifecycle()
    val isCompleted by gameViewModel.isCompleted.collectAsStateWithLifecycle()

    val layout = rememberPuzzleLayoutResources(screenSession.config.cols)
    val context = LocalContext.current
    val stepLabel = stringResource(screenSession.config.nameRes)
    val puzzleBitmap = rememberSessionPuzzleBitmap(
        context = context,
        sessionKey = screenSession.config.viewModelKey,
        drawableRes = screenSession.config.drawableRes,
        rows = screenSession.config.rows,
        cols = screenSession.config.cols,
    )

    val allPiecesPlaced = pieces.all { it.isPlaced }
    val showSuccessScreen = (showSuccess || isCompleted || allPiecesPlaced) && !isAdvancingToNext

    fun setShowSuccess(visible: Boolean) {
        showSuccess = visible
    }

    fun clearAdvancingFlag() {
        isAdvancingToNext = false
    }

    fun beginNextLevel() {
        isAdvancingToNext = true
        showSuccess = false
        successSnapshot = null
        flowViewModel.advanceToNextPuzzle(progressTrack)
    }

    fun restartCurrentLevel() {
        showSuccess = false
        successSnapshot = null
        gameViewModel.reset()
    }

    fun ensureSuccessSnapshot(): PuzzleSuccessSnapshot? {
        successSnapshot?.let { return it }
        val bitmap = puzzleBitmap ?: return null
        if (!showSuccessScreen) return null
        val created = PuzzleSuccessSnapshot(
            bitmap = bitmap,
            boardAspectRatio = screenSession.boardAspectRatio,
        )
        successSnapshot = created
        return created
    }

    LaunchedEffect(allPiecesPlaced) {
        if (allPiecesPlaced) {
            dragState.cancelDrag()
        }
    }

    LaunchedEffect(screenSession.config.viewModelKey, puzzleBitmap) {
        if (puzzleBitmap != null) {
            clearAdvancingFlag()
        }
    }

    BindPuzzleGameEffects(
        viewModelKey = screenSession.config.viewModelKey,
        isCompleted = isCompleted,
        gameViewModel = gameViewModel,
        clickSound = clickSound,
        onShowSuccess = ::setShowSuccess,
    )

    Scaffold(
        containerColor = PatientColors.puzzleBackground,
        topBar = {
            PuzzleGameTopBar(
                session = screenSession.config,
                stepLabel = stepLabel,
                fallbackGrid = screenSession.fallbackGrid,
                slotCount = screenSession.slotCount,
                onBack = onBack,
                onReset = gameViewModel::reset,
            )
        },
    ) { padding ->
        if (isAdvancingToNext) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = PatientColors.puzzleAccent)
            }
            return@Scaffold
        }

        if (showSuccessScreen) {
            val snapshot = ensureSuccessSnapshot()
            if (snapshot == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PatientColors.puzzleAccent)
                }
                return@Scaffold
            }
            PuzzleSuccessRoute(
                padding = padding,
                snapshot = snapshot,
                contentWidthFraction = layout.successContentWidthFraction,
                hasNextLevel = screenSession.config.hasNextStep,
                onNextLevel = ::beginNextLevel,
                onPlayAgain = ::restartCurrentLevel,
                onBackToHome = onBackToHome,
            )
            return@Scaffold
        }

        if (puzzleBitmap == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = PatientColors.puzzleAccent)
            }
            return@Scaffold
        }

        val uiState = PuzzleGameUiState(
            pieces = pieces,
            trayOrder = trayOrder,
            isCompleted = isCompleted,
            showSuccess = showSuccess,
            stepLabel = stepLabel,
            puzzleBitmap = puzzleBitmap,
            clickSound = clickSound,
            dragState = dragState,
            ghostAlpha = layout.ghostAlpha,
            knobFraction = layout.knobFraction,
            snapRadiusFraction = layout.snapRadiusFraction,
            trayScaleOfSlot = layout.trayScaleOfSlot,
            trayHitScale = layout.trayHitScale,
            trayBackgroundAlpha = layout.trayBackgroundAlpha,
            borderAnimDurationMs = layout.borderAnimDurationMs,
            trayColumns = layout.trayColumns,
            successContentWidthFraction = layout.successContentWidthFraction,
            boardPieceZIndex = layout.boardPieceZIndex,
            dragOverlayZIndex = layout.dragOverlayZIndex,
            slotStrokeNormalPx = layout.slotStrokeNormalPx,
            slotStrokeMagnetPx = layout.slotStrokeMagnetPx,
        )

        PuzzlePlayRoute(
            padding = padding,
            screenSession = screenSession,
            uiState = uiState,
            gameViewModel = gameViewModel,
        )
    }
}

@Composable
private fun PuzzleSuccessRoute(
    padding: PaddingValues,
    snapshot: PuzzleSuccessSnapshot,
    contentWidthFraction: Float,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onBackToHome: () -> Unit,
) {
    PuzzleSuccessView(
        modifier = Modifier.padding(padding),
        puzzleBitmap = snapshot.bitmap,
        boardAspectRatio = snapshot.boardAspectRatio,
        contentWidthFraction = contentWidthFraction,
        hasNextLevel = hasNextLevel,
        onNextLevel = onNextLevel,
        onPlayAgain = onPlayAgain,
        onBack = onBackToHome,
    )
}

@Composable
private fun PuzzlePlayRoute(
    padding: PaddingValues,
    screenSession: PuzzleScreenSession,
    uiState: PuzzleGameUiState,
    gameViewModel: PuzzleGameViewModel,
) {
    val trayPieces = rememberVisibleTrayPieces(
        gameViewModel = gameViewModel,
        trayOrder = uiState.trayOrder,
        pieces = uiState.pieces,
        session = screenSession.config,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .onGloballyPositioned { coordinates ->
                uiState.dragState.dragAreaOrigin = coordinates.positionInRoot()
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = PatientDimens.puzzleScreenPaddingH,
                    vertical = PatientDimens.puzzleScreenPaddingV,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PuzzleGameBoard(
                puzzleBitmap = uiState.puzzleBitmap,
                pieces = uiState.pieces,
                gridRows = screenSession.config.rows,
                gridCols = screenSession.config.cols,
                slotCount = screenSession.slotCount,
                boardAspectRatio = screenSession.boardAspectRatio,
                dragState = uiState.dragState,
                ghostAlpha = uiState.ghostAlpha,
                showGhost = uiState.pieces.any { !it.isPlaced },
                knobFraction = uiState.knobFraction,
                borderAnimDurationMs = uiState.borderAnimDurationMs,
                boardPieceZIndex = uiState.boardPieceZIndex,
                slotStrokeNormalPx = uiState.slotStrokeNormalPx,
                slotStrokeMagnetPx = uiState.slotStrokeMagnetPx,
            )
            PuzzleTraySection(
                trayPieces = trayPieces,
                screenSession = screenSession,
                uiState = uiState,
                gameViewModel = gameViewModel,
            )
        }

        PuzzleDragOverlay(
            puzzleBitmap = uiState.puzzleBitmap,
            gridRows = screenSession.config.rows,
            gridCols = screenSession.config.cols,
            knobFraction = uiState.knobFraction,
            dragState = uiState.dragState,
            dragOverlayZIndex = uiState.dragOverlayZIndex,
        )
    }
}

@Composable
private fun PuzzleTraySection(
    trayPieces: List<PuzzlePiece>,
    screenSession: PuzzleScreenSession,
    uiState: PuzzleGameUiState,
    gameViewModel: PuzzleGameViewModel,
) {
    Spacer(Modifier.height(PatientDimens.puzzleTraySectionGap))
    Text(
        stringResource(R.string.puzzle_tray_label),
        fontSize = PatientDimens.puzzleStatusTextSize,
        color = PatientColors.puzzleTextSecondary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(PatientDimens.puzzleTrayLabelBottomGap))
    PuzzleGameTray(
        puzzleBitmap = uiState.puzzleBitmap,
        pieces = uiState.pieces,
        trayPieces = trayPieces,
        gridRows = screenSession.config.rows,
        gridCols = screenSession.config.cols,
        trayColumns = uiState.trayColumns,
        slotCount = screenSession.slotCount,
        knobFraction = uiState.knobFraction,
        trayScaleOfSlot = uiState.trayScaleOfSlot,
        trayHitScale = uiState.trayHitScale,
        trayBackgroundAlpha = uiState.trayBackgroundAlpha,
        dragState = uiState.dragState,
        snapRadiusFraction = uiState.snapRadiusFraction,
        onPickupSound = uiState.clickSound::playPickup,
        onSnapSound = uiState.clickSound::playSnap,
        viewModel = gameViewModel,
    )
}

@Composable
private fun rememberPuzzleScreenSession(
    flowViewModel: PuzzleFlowViewModel,
    stageIndex: Int?,
    progressTrack: PuzzleProgressTrack,
): PuzzleScreenSession {
    val progress by flowViewModel.puzzleProgressFor(progressTrack).collectAsStateWithLifecycle()
    val config = flowViewModel.sessionConfig(stageIndex, progress, progressTrack)
    return remember(config.viewModelKey, stageIndex) {
        PuzzleScreenSession(
            config = config,
            fallbackGrid = puzzleGridForStage(stageIndex),
            slotCount = config.rows * config.cols,
            boardAspectRatio = config.cols.toFloat() / config.rows,
        )
    }
}

@Composable
private fun rememberPuzzleGameViewModel(screenSession: PuzzleScreenSession): PuzzleGameViewModel {
    val config = screenSession.config
    return viewModel(
        key = config.viewModelKey,
        factory = PuzzleGameViewModelFactory(
            rows = config.rows,
            cols = config.cols,
            sequentialRevealMode = config.sequentialRevealMode,
        ),
    )
}

@Composable
private fun rememberVisibleTrayPieces(
    gameViewModel: PuzzleGameViewModel,
    trayOrder: List<Int>,
    pieces: List<PuzzlePiece>,
    session: com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleSessionConfig,
): List<PuzzlePiece> {
    val inTray = gameViewModel.piecesInTray(trayOrder, pieces)
    return remember(trayOrder, pieces, session.sequentialRevealMode, inTray) {
        PuzzleTrayRevealFilter.visibleTrayPieces(
            mode = session.sequentialRevealMode,
            gridRows = session.rows,
            gridCols = session.cols,
            trayPieces = inTray,
            allPieces = pieces,
        )
    }
}

@Composable
private fun BindPuzzleGameEffects(
    viewModelKey: String,
    isCompleted: Boolean,
    gameViewModel: PuzzleGameViewModel,
    clickSound: PuzzleClickSound,
    onShowSuccess: (Boolean) -> Unit,
) {
    LaunchedEffect(viewModelKey) {
        gameViewModel.reset()
        onShowSuccess(false)
    }
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            onShowSuccess(true)
            clickSound.playShine()
        }
    }
}

@Stable
private data class PuzzleLayoutResources(
    val ghostAlpha: Float,
    val knobFraction: Float,
    val snapRadiusFraction: Float,
    val trayScaleOfSlot: Float,
    val trayHitScale: Float,
    val trayBackgroundAlpha: Float,
    val borderAnimDurationMs: Int,
    val trayColumns: Int,
    val successContentWidthFraction: Float,
    val boardPieceZIndex: Float,
    val dragOverlayZIndex: Float,
    val slotStrokeNormalPx: Float,
    val slotStrokeMagnetPx: Float,
)

@Composable
private fun rememberPuzzleLayoutResources(gridCols: Int): PuzzleLayoutResources {
    val ghostAlpha = PuzzleLayoutMetrics.ghostImageAlpha()
    val knobFraction = PuzzleLayoutMetrics.jigsawKnobFraction()
    val snapRadiusFraction = PuzzleLayoutMetrics.snapRadiusFraction()
    val trayScaleOfSlot = PuzzleLayoutMetrics.trayScaleOfSlot()
    val trayHitScale = PuzzleLayoutMetrics.trayHitScale()
    val trayBackgroundAlpha = PuzzleLayoutMetrics.trayBackgroundAlpha()
    val borderAnimDurationMs = PuzzleLayoutMetrics.borderAnimDurationMs()
    val trayColumns = PuzzleLayoutMetrics.trayColumnsFor(gridCols)
    val successContentWidthFraction = PuzzleLayoutMetrics.successContentWidthFraction()
    val boardPieceZIndex = PuzzleLayoutMetrics.boardPieceZIndex()
    val dragOverlayZIndex = PuzzleLayoutMetrics.dragOverlayZIndex()
    val (slotStrokeNormalPx, slotStrokeMagnetPx) = PuzzleLayoutMetrics.slotStrokeWidthPx(
        normal = PatientDimens.puzzleSlotStrokeNormal,
        magnet = PatientDimens.puzzleSlotStrokeMagnet,
    )
    return remember(
        gridCols,
        ghostAlpha,
        knobFraction,
        snapRadiusFraction,
        trayScaleOfSlot,
        trayHitScale,
        trayBackgroundAlpha,
        borderAnimDurationMs,
        trayColumns,
        successContentWidthFraction,
        boardPieceZIndex,
        dragOverlayZIndex,
        slotStrokeNormalPx,
        slotStrokeMagnetPx,
    ) {
        PuzzleLayoutResources(
            ghostAlpha = ghostAlpha,
            knobFraction = knobFraction,
            snapRadiusFraction = snapRadiusFraction,
            trayScaleOfSlot = trayScaleOfSlot,
            trayHitScale = trayHitScale,
            trayBackgroundAlpha = trayBackgroundAlpha,
            borderAnimDurationMs = borderAnimDurationMs,
            trayColumns = trayColumns,
            successContentWidthFraction = successContentWidthFraction,
            boardPieceZIndex = boardPieceZIndex,
            dragOverlayZIndex = dragOverlayZIndex,
            slotStrokeNormalPx = slotStrokeNormalPx,
            slotStrokeMagnetPx = slotStrokeMagnetPx,
        )
    }
}
