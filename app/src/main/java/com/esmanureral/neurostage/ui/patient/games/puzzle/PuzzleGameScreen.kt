package com.esmanureral.neurostage.ui.patient.games.puzzle

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.PuzzleFlowViewModel
import com.esmanureral.neurostage.ui.patient.games.puzzleGridForStage
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGameScreen(
    stageIndex: Int?,
    onBack: () -> Unit,
    onBackToHome: () -> Unit = onBack,
    flowViewModel: PuzzleFlowViewModel = hiltViewModel(),
) {
    val progress by flowViewModel.puzzleProgress.collectAsStateWithLifecycle()
    val session = flowViewModel.sessionConfig(stageIndex, progress)
    val puzzleStep = session.step
    val gridRows = session.rows
    val gridCols = session.cols
    val slotCount = gridRows * gridCols
    val fallbackGrid = puzzleGridForStage(stageIndex)

    val vm: PuzzleGameViewModel = viewModel(
        key = "puzzle_${gridRows}_${gridCols}_${puzzleStep.name}",
        factory = PuzzleGameViewModelFactory(gridRows, gridCols),
    )

    val pieces by vm.pieces.collectAsStateWithLifecycle()
    val trayOrder by vm.trayOrder.collectAsStateWithLifecycle()
    val isCompleted by vm.isCompleted.collectAsStateWithLifecycle()

    var showSuccess by remember(puzzleStep) { mutableStateOf(false) }

    LaunchedEffect(puzzleStep) {
        vm.reset()
        showSuccess = false
    }

    LaunchedEffect(isCompleted) {
        if (isCompleted) showSuccess = true
    }

    val context = LocalContext.current
    val fullImage = remember(puzzleStep, gridRows, gridCols) {
        loadPuzzleImage(context, puzzleStep.drawableRes, gridRows, gridCols)
    }
    val stepLabel = stringResource(puzzleStep.nameRes)

    val slotCenters = remember { mutableStateMapOf<Int, Offset>() }
    val pieceRoots = remember { mutableStateMapOf<Int, Offset>() }
    var cellWidthPx by remember { mutableFloatStateOf(0f) }
    var cellHeightPx by remember { mutableFloatStateOf(0f) }

    var draggingId by remember { mutableIntStateOf(-1) }
    var dragFingerRoot by remember { mutableStateOf(Offset.Zero) }
    var dragGrabOffset by remember { mutableStateOf(Offset.Zero) }
    var nearCorrectSlot by remember { mutableStateOf(false) }
    var dragAreaOrigin by remember { mutableStateOf(Offset.Zero) }
    var boardRoot by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        containerColor = PatientColors.puzzleBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.puzzle_title),
                            fontSize = PatientDimens.puzzleTopBarTitleSize,
                            fontWeight = FontWeight.SemiBold,
                            color = PatientColors.puzzleTextPrimary,
                        )
                        Text(
                            text = if (session.usesCatalogPath) {
                                stringResource(R.string.puzzle_subtitle_mild, stepLabel)
                            } else {
                                stringResource(
                                    R.string.puzzle_subtitle,
                                    fallbackGrid,
                                    fallbackGrid,
                                    slotCount
                                )
                            },
                            fontSize = PatientDimens.puzzleTopBarSubtitleSize,
                            color = PatientColors.puzzleTextSecondary,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.puzzle_cd_back),
                            tint = PatientColors.puzzleTextPrimary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        vm.reset()
                    }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.puzzle_cd_reset),
                            tint = PatientColors.puzzleAccent,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PatientColors.surface,
                    titleContentColor = PatientColors.puzzleTextPrimary,
                    navigationIconContentColor = PatientColors.puzzleTextPrimary,
                ),
            )
        },
    ) { pad ->
        if (showSuccess) {
            SuccessView(
                imageRes = puzzleStep.drawableRes,
                boardAspectRatio = gridCols.toFloat() / gridRows,
                hasNextLevel = session.hasNextStep,
                onNextLevel = {
                    flowViewModel.advanceToNextPuzzle()
                },
                onPlayAgain = {
                    vm.reset()
                },
                onBack = onBackToHome,
                modifier = Modifier.padding(pad),
            )
            return@Scaffold
        }

        val snapCellPx = min(cellWidthPx, cellHeightPx)
        val snapRadiusPx = snapCellPx * PuzzleLayoutConfig.SNAP_RADIUS_FRACTION

        fun updateMagnetHint(pieceId: Int) {
            if (pieceId < 0 || snapCellPx <= 0f) {
                nearCorrectSlot = false
                return
            }
            val correct = pieces.firstOrNull { it.id == pieceId }?.correctSlot ?: return
            val center = slotCenters[correct] ?: return
            val d = sqrt(
                (dragFingerRoot.x - center.x) * (dragFingerRoot.x - center.x) +
                        (dragFingerRoot.y - center.y) * (dragFingerRoot.y - center.y),
            )
            nearCorrectSlot = d <= snapRadiusPx
        }

        fun onDragEnd(pieceId: Int) {
            val topLeft = dragFingerRoot - dragGrabOffset
            val pieceCenter = Offset(
                topLeft.x + cellWidthPx / 2f,
                topLeft.y + cellHeightPx / 2f,
            )
            vm.tryMagneticSnap(
                pieceId = pieceId,
                fingerRoot = dragFingerRoot,
                pieceCenterRoot = pieceCenter,
                slotCenters = slotCenters,
                snapRadiusPx = snapRadiusPx,
            )
            draggingId = -1
            nearCorrectSlot = false
        }

        fun beginDrag(
            pieceId: Int,
            touchInPiece: Offset,
            visualWidthPx: Float,
            visualHeightPx: Float,
        ) {
            draggingId = pieceId
            val topLeft = pieceRoots[pieceId] ?: Offset.Zero
            val scaleX =
                if (visualWidthPx > 0f && cellWidthPx > 0f) cellWidthPx / visualWidthPx else 1f
            val scaleY =
                if (visualHeightPx > 0f && cellHeightPx > 0f) cellHeightPx / visualHeightPx else 1f
            dragGrabOffset = Offset(touchInPiece.x * scaleX, touchInPiece.y * scaleY)
            dragFingerRoot = topLeft + dragGrabOffset
        }

        fun moveDrag(dragAmount: Offset, pieceId: Int) {
            dragFingerRoot += dragAmount
            updateMagnetHint(pieceId)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .onGloballyPositioned { coordinates ->
                    dragAreaOrigin = coordinates.positionInRoot()
                },
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = PatientDimens.puzzleScreenPaddingH,
                        vertical = PatientDimens.puzzleScreenPaddingV
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(gridCols.toFloat() / gridRows)
                        .background(
                            PatientColors.puzzleBoardBackground,
                            RoundedCornerShape(PatientDimens.puzzleBoardCorner)
                        )
                        .border(
                            PatientDimens.puzzleBoardBorder,
                            PatientColors.puzzleSlot,
                            RoundedCornerShape(PatientDimens.puzzleBoardCorner)
                        )
                        .onGloballyPositioned { coordinates ->
                            boardRoot = coordinates.positionInRoot()
                        }
                        .onSizeChanged { size ->
                            cellWidthPx = size.width.toFloat() / gridCols
                            cellHeightPx = size.height.toFloat() / gridRows
                        },
                ) {
                    if (cellWidthPx <= 0f || cellHeightPx <= 0f) return@Box

                    val density = LocalDensity.current
                    val pieceWidthDp = with(density) { cellWidthPx.toDp() }
                    val pieceHeightDp = with(density) { cellHeightPx.toDp() }

                    for (slot in 0 until slotCount) {
                        val sRow = slot / gridCols
                        val sCol = slot % gridCols
                        slotCenters[slot] = Offset(
                            boardRoot.x + (sCol + 0.5f) * cellWidthPx,
                            boardRoot.y + (sRow + 0.5f) * cellHeightPx,
                        )
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(PatientDimens.puzzleBoardCorner)),
                    ) {
                        PuzzleBoardGhost(
                            fullImage = fullImage,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(PuzzleLayoutConfig.GHOST_IMAGE_ALPHA),
                        )
                    }

                    val slotStrokeColor = PatientColors.puzzleSlot.copy(alpha = 0.75f)
                    val magnetSlotStrokeColor = PatientColors.puzzleSuccess.copy(alpha = 0.85f)

                    repeat(slotCount) { slot ->
                        val sRow = slot / gridCols
                        val sCol = slot % gridCols
                        val ox = with(density) { (sCol * cellWidthPx).toDp() }
                        val oy = with(density) { (sRow * cellHeightPx).toDp() }
                        val draggingPiece = pieces.firstOrNull { it.id == draggingId }
                        val isMagnetTarget = draggingId >= 0 &&
                                draggingPiece?.correctSlot == slot &&
                                nearCorrectSlot

                        Box(
                            modifier = Modifier
                                .size(pieceWidthDp, pieceHeightDp)
                                .offset(x = ox, y = oy),
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val path =
                                    piecePath(size.width, size.height, slot, gridRows, gridCols)
                                drawPath(
                                    path = path,
                                    color = if (isMagnetTarget) magnetSlotStrokeColor else slotStrokeColor,
                                    style = Stroke(
                                        width = if (isMagnetTarget) 5f else 3f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round,
                                    ),
                                )
                            }
                        }
                    }

                    pieces.filter { it.currentSlot >= 0 && it.id != draggingId }.forEach { piece ->
                        val sRow = piece.currentSlot / gridCols
                        val sCol = piece.currentSlot % gridCols
                        val ox = with(density) { (sCol * cellWidthPx).toDp() }
                        val oy = with(density) { (sRow * cellHeightPx).toDp() }

                        val borderCol by animateColorAsState(
                            if (piece.isPlaced) PatientColors.puzzleSuccess else PatientColors.surface,
                            tween(300), label = "bc${piece.id}"
                        )

                        Box(
                            Modifier
                                .size(pieceWidthDp, pieceHeightDp)
                                .offset(x = ox, y = oy)
                                .graphicsLayer { clip = false }
                                .zIndex(2f),
                        ) {
                            PuzzlePieceView(
                                fullImage = fullImage,
                                pieceId = piece.id,
                                rows = gridRows,
                                cols = gridCols,
                                borderColor = borderCol,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(PatientDimens.puzzleTraySectionGap))

                Text(
                    stringResource(R.string.puzzle_tray_label),
                    fontSize = PatientDimens.puzzleStatusTextSize,
                    color = PatientColors.puzzleTextSecondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(PatientDimens.puzzleTrayLabelBottomGap))

                val density = LocalDensity.current
                val trayPieces = trayOrder.mapNotNull { pieceId ->
                    pieces.firstOrNull { it.id == pieceId && it.currentSlot < 0 }
                }
                val trayColumns = if (gridCols >= 3) 3 else 2
                val trayGap = PatientDimens.puzzleTrayGap
                val puzzleTrayFallbackDp = dimensionResource(R.dimen.puzzle_tray_fallback_size)
                val puzzleTrayMinWidthFour = PatientDimens.puzzleTrayMinWidthFour
                val puzzleTrayMinWidthSixPlus = PatientDimens.puzzleTrayMinWidthSixPlus
                val puzzleTrayMaxHeight = PatientDimens.puzzleTrayMaxHeight
                val trayPieceBorderColor = PatientColors.surface

                SubcomposeLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(PatientDimens.puzzleTrayCorner))
                        .background(PatientColors.puzzleBoardBackground.copy(alpha = 0.55f))
                        .padding(
                            horizontal = PatientDimens.puzzleTrayPaddingH,
                            vertical = PatientDimens.puzzleTrayPaddingV,
                        )
                        .heightIn(
                            min = when {
                                slotCount <= 4 -> PatientDimens.puzzleTrayMinHeightFourPieces
                                slotCount <= 6 -> PatientDimens.puzzleTrayMinHeightSixPieces
                                else -> PatientDimens.puzzleTrayMinHeightNinePieces
                            },
                        ),
                ) { constraints ->
                    val maxRowWidth = constraints.maxWidth.coerceAtLeast(1).toDp()
                    val traySize = trayPieceSizeFor(
                        maxRowWidth = maxRowWidth,
                        cellWidthPx = cellWidthPx,
                        cellHeightPx = cellHeightPx,
                        columns = trayColumns,
                        pieceCount = slotCount,
                        gap = trayGap,
                        minWidthFourPieces = puzzleTrayMinWidthFour,
                        minWidthSixPlusPieces = puzzleTrayMinWidthSixPlus,
                        maxHeight = puzzleTrayMaxHeight,
                        fallbackSize = puzzleTrayFallbackDp,
                    )
                    val placeable = subcompose("puzzle_tray") {
                        val trayPieceWidthDp = traySize.width
                        val trayPieceHeightDp = traySize.height
                        val trayPieceWidthPx = with(density) { trayPieceWidthDp.toPx() }
                        val trayPieceHeightPx = with(density) { trayPieceHeightDp.toPx() }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(trayGap),
                        ) {
                            trayPieces.chunked(trayColumns).forEach { rowPieces ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        trayGap,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    rowPieces.forEach { piece ->
                                        val isDragging = draggingId == piece.id
                                        val tilts = PuzzleLayoutConfig.trayTiltDegrees
                                        val tilt = tilts.getOrElse(piece.id % tilts.size) { 0f }

                                        Box(
                                            modifier = Modifier
                                                .size(
                                                    trayPieceWidthDp * 1.06f,
                                                    trayPieceHeightDp * 1.06f
                                                )
                                                .graphicsLayer { rotationZ = tilt },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(trayPieceWidthDp, trayPieceHeightDp)
                                                    .alpha(if (isDragging) 0f else 1f)
                                                    .onGloballyPositioned { coordinates ->
                                                        if (!isDragging) {
                                                            pieceRoots[piece.id] =
                                                                coordinates.positionInRoot()
                                                        }
                                                    }
                                                    .pointerInput(piece.id) {
                                                        detectDragGestures(
                                                            onDragStart = { touch ->
                                                                beginDrag(
                                                                    piece.id,
                                                                    touch,
                                                                    visualWidthPx = trayPieceWidthPx,
                                                                    visualHeightPx = trayPieceHeightPx,
                                                                )
                                                            },
                                                            onDrag = { c, d ->
                                                                c.consume()
                                                                moveDrag(d, piece.id)
                                                            },
                                                            onDragEnd = { onDragEnd(piece.id) },
                                                            onDragCancel = {
                                                                draggingId = -1
                                                                nearCorrectSlot = false
                                                            },
                                                        )
                                                    },
                                            ) {
                                                PuzzlePieceView(
                                                    fullImage = fullImage,
                                                    pieceId = piece.id,
                                                    rows = gridRows,
                                                    cols = gridCols,
                                                    borderColor = trayPieceBorderColor,
                                                    modifier = Modifier.fillMaxSize(),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.first().measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
            }

            if (draggingId >= 0) {
                val density = LocalDensity.current
                val dragWidthDp = if (cellWidthPx > 0f) {
                    with(density) { cellWidthPx.toDp() }
                } else {
                    PatientDimens.puzzleDragFallbackSize
                }
                val dragHeightDp = if (cellHeightPx > 0f) {
                    with(density) { cellHeightPx.toDp() }
                } else {
                    PatientDimens.puzzleDragFallbackSize
                }
                val topLeft = dragFingerRoot - dragGrabOffset - dragAreaOrigin
                Box(
                    Modifier
                        .size(dragWidthDp, dragHeightDp)
                        .offset {
                            IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt())
                        }
                        .graphicsLayer { clip = false }
                        .zIndex(20f),
                ) {
                    PuzzlePieceView(
                        fullImage = fullImage,
                        pieceId = draggingId,
                        rows = gridRows,
                        cols = gridCols,
                        borderColor = if (nearCorrectSlot) PatientColors.puzzleSuccess else PatientColors.puzzleAccent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}


@Composable
private fun SuccessView(
    @DrawableRes imageRes: Int,
    boardAspectRatio: Float,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(PatientDimens.puzzleSuccessPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .aspectRatio(boardAspectRatio)
                .clip(RoundedCornerShape(PatientDimens.puzzleSuccessCorner))
                .border(
                    PatientDimens.puzzleSuccessBorder,
                    PatientColors.puzzleSuccess,
                    RoundedCornerShape(PatientDimens.puzzleSuccessCorner),
                ),
        )
        Spacer(Modifier.height(PatientDimens.puzzleSuccessTitleGap))
        Text(
            stringResource(R.string.puzzle_success_title),
            fontSize = PatientDimens.puzzleSuccessTitleSize,
            fontWeight = FontWeight.Bold,
            color = PatientColors.puzzleSuccess,
        )
        Spacer(Modifier.height(PatientDimens.puzzleSuccessButtonGap))
        if (hasNextLevel) {
            Button(
                onClick = onNextLevel,
                colors = ButtonDefaults.buttonColors(containerColor = PatientColors.puzzleSuccess),
                shape = RoundedCornerShape(PatientDimens.puzzleSuccessButtonCorner),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(PatientDimens.puzzleSuccessButtonHeight),
            ) {
                Text(
                    stringResource(R.string.puzzle_success_next),
                    fontSize = PatientDimens.puzzleSuccessPrimaryButtonTextSize,
                    fontWeight = FontWeight.Black,
                    color = PatientColors.surface,
                )
            }
        } else {
            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = PatientColors.puzzleSuccess),
                shape = RoundedCornerShape(PatientDimens.puzzleSuccessButtonCorner),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(PatientDimens.puzzleSuccessButtonHeight),
            ) {
                Text(
                    stringResource(R.string.puzzle_success_play_again),
                    fontSize = PatientDimens.puzzleSuccessPrimaryButtonTextSize,
                    fontWeight = FontWeight.Black,
                    color = PatientColors.surface,
                )
            }
            Spacer(Modifier.height(PatientDimens.puzzleSuccessSecondaryGap))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = PatientColors.puzzleTextSecondary),
                shape = RoundedCornerShape(PatientDimens.puzzleSuccessButtonCorner),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(PatientDimens.puzzleSuccessButtonHeight),
            ) {
                Text(
                    stringResource(R.string.puzzle_success_back),
                    fontSize = PatientDimens.puzzleSuccessSecondaryButtonTextSize,
                    fontWeight = FontWeight.Bold,
                    color = PatientColors.surface,
                )
            }
        }
    }
}