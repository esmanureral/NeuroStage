package com.esmanureral.neurostage.ui.patient.games.puzzle

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

@Composable
fun PuzzleGameTray(
    puzzleBitmap: ImageBitmap,
    pieces: List<PuzzlePiece>,
    trayPieces: List<PuzzlePiece>,
    gridRows: Int,
    gridCols: Int,
    trayColumns: Int,
    slotCount: Int,
    knobFraction: Float,
    trayScaleOfSlot: Float,
    trayHitScale: Float,
    trayBackgroundAlpha: Float,
    dragState: PuzzleDragStateHolder,
    snapRadiusFraction: Float,
    onPickupSound: () -> Unit,
    onSnapSound: () -> Unit,
    viewModel: PuzzleGameViewModel,
    modifier: Modifier = Modifier,
) {
    val trayGap = PatientDimens.puzzleTrayGap
    val trayFallbackDp = dimensionResource(R.dimen.puzzle_tray_fallback_size)
    val trayMinWidthFour = PatientDimens.puzzleTrayMinWidthFour
    val trayMinWidthSixPlus = PatientDimens.puzzleTrayMinWidthSixPlus
    val trayMaxHeight = PatientDimens.puzzleTrayMaxHeight
    val trayMinHeight = puzzleTrayMinHeight(slotCount)
    val trayTilts = PuzzleLayoutMetrics.trayTiltDegrees()
    val density = LocalDensity.current

    SubcomposeLayout(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PatientDimens.puzzleTrayCorner))
            .background(PatientColors.puzzleBoardBackground.copy(alpha = trayBackgroundAlpha))
            .padding(
                horizontal = PatientDimens.puzzleTrayPaddingH,
                vertical = PatientDimens.puzzleTrayPaddingV,
            )
            .heightIn(min = trayMinHeight),
    ) { constraints ->
        val maxRowWidth = with(density) {
            constraints.maxWidth.coerceAtLeast(1).toDp()
        }
        val traySize = trayPieceSizeFor(
            maxRowWidth = maxRowWidth,
            cellWidthPx = dragState.cellWidthPx,
            cellHeightPx = dragState.cellHeightPx,
            columns = trayColumns,
            pieceCount = trayPieces.size.coerceAtLeast(1),
            gap = trayGap,
            minWidthFourPieces = trayMinWidthFour,
            minWidthSixPlusPieces = trayMinWidthSixPlus,
            maxHeight = trayMaxHeight,
            fallbackSize = trayFallbackDp,
            trayScaleOfSlot = trayScaleOfSlot,
        )
        val placeable = subcompose(PUZZLE_TRAY_SLOT_ID) {
            PuzzleTrayContent(
                allPieces = pieces,
                trayPieces = trayPieces,
                trayColumns = trayColumns,
                traySize = traySize,
                trayGap = trayGap,
                trayHitScale = trayHitScale,
                trayTilts = trayTilts,
                puzzleBitmap = puzzleBitmap,
                gridRows = gridRows,
                gridCols = gridCols,
                knobFraction = knobFraction,
                dragState = dragState,
                snapRadiusFraction = snapRadiusFraction,
                onPickupSound = onPickupSound,
                onSnapSound = onSnapSound,
                viewModel = viewModel,
            )
        }.first().measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

@Composable
private fun PuzzleTrayContent(
    allPieces: List<PuzzlePiece>,
    trayPieces: List<PuzzlePiece>,
    trayColumns: Int,
    traySize: TrayPieceSize,
    trayGap: Dp,
    trayHitScale: Float,
    trayTilts: List<Float>,
    puzzleBitmap: ImageBitmap,
    gridRows: Int,
    gridCols: Int,
    knobFraction: Float,
    dragState: PuzzleDragStateHolder,
    snapRadiusFraction: Float,
    onPickupSound: () -> Unit,
    onSnapSound: () -> Unit,
    viewModel: PuzzleGameViewModel,
) {
    val density = LocalDensity.current
    val trayPieceWidthDp = traySize.width
    val trayPieceHeightDp = traySize.height
    val trayPieceWidthPx = with(density) { trayPieceWidthDp.toPx() }
    val trayPieceHeightPx = with(density) { trayPieceHeightDp.toPx() }
    val trayPieceBorderColor = PatientColors.surface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(trayGap),
    ) {
        for (rowPieces in trayPieces.chunked(trayColumns)) {
            PuzzleTrayRow(
                allPieces = allPieces,
                rowPieces = rowPieces,
                trayGap = trayGap,
                trayPieceWidthDp = trayPieceWidthDp,
                trayPieceHeightDp = trayPieceHeightDp,
                trayHitScale = trayHitScale,
                trayTilts = trayTilts,
                puzzleBitmap = puzzleBitmap,
                gridRows = gridRows,
                gridCols = gridCols,
                knobFraction = knobFraction,
                dragState = dragState,
                snapRadiusFraction = snapRadiusFraction,
                trayPieceWidthPx = trayPieceWidthPx,
                trayPieceHeightPx = trayPieceHeightPx,
                trayPieceBorderColor = trayPieceBorderColor,
                onPickupSound = onPickupSound,
                onSnapSound = onSnapSound,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
private fun PuzzleTrayRow(
    allPieces: List<PuzzlePiece>,
    rowPieces: List<PuzzlePiece>,
    trayGap: Dp,
    trayPieceWidthDp: Dp,
    trayPieceHeightDp: Dp,
    trayHitScale: Float,
    trayTilts: List<Float>,
    puzzleBitmap: ImageBitmap,
    gridRows: Int,
    gridCols: Int,
    knobFraction: Float,
    dragState: PuzzleDragStateHolder,
    snapRadiusFraction: Float,
    trayPieceWidthPx: Float,
    trayPieceHeightPx: Float,
    trayPieceBorderColor: androidx.compose.ui.graphics.Color,
    onPickupSound: () -> Unit,
    onSnapSound: () -> Unit,
    viewModel: PuzzleGameViewModel,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(trayGap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (piece in rowPieces) {
            PuzzleTrayPiece(
                allPieces = allPieces,
                piece = piece,
                isDragging = dragState.draggingId == piece.id,
                tilt = trayTilts.getOrElse(piece.id % trayTilts.size) { 0f },
                trayPieceWidthDp = trayPieceWidthDp,
                trayPieceHeightDp = trayPieceHeightDp,
                trayHitScale = trayHitScale,
                puzzleBitmap = puzzleBitmap,
                gridRows = gridRows,
                gridCols = gridCols,
                knobFraction = knobFraction,
                dragState = dragState,
                snapRadiusFraction = snapRadiusFraction,
                trayPieceWidthPx = trayPieceWidthPx,
                trayPieceHeightPx = trayPieceHeightPx,
                borderColor = trayPieceBorderColor,
                onPickupSound = onPickupSound,
                onSnapSound = onSnapSound,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
private fun PuzzleTrayPiece(
    allPieces: List<PuzzlePiece>,
    piece: PuzzlePiece,
    isDragging: Boolean,
    tilt: Float,
    trayPieceWidthDp: Dp,
    trayPieceHeightDp: Dp,
    trayHitScale: Float,
    puzzleBitmap: ImageBitmap,
    gridRows: Int,
    gridCols: Int,
    knobFraction: Float,
    dragState: PuzzleDragStateHolder,
    snapRadiusFraction: Float,
    trayPieceWidthPx: Float,
    trayPieceHeightPx: Float,
    borderColor: androidx.compose.ui.graphics.Color,
    onPickupSound: () -> Unit,
    onSnapSound: () -> Unit,
    viewModel: PuzzleGameViewModel,
) {
    Box(
        modifier = Modifier
            .size(trayPieceWidthDp * trayHitScale, trayPieceHeightDp * trayHitScale)
            .graphicsLayer { rotationZ = tilt },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(trayPieceWidthDp, trayPieceHeightDp)
                .alpha(if (isDragging) 0f else 1f)
                .onGloballyPositioned { coordinates ->
                    if (!isDragging) {
                        dragState.pieceRoots[piece.id] = coordinates.positionInRoot()
                    }
                }
                .pointerInput(piece.id) {
                    detectDragGestures(
                        onDragStart = { touch ->
                            onPickupSound()
                            dragState.beginDrag(
                                pieceId = piece.id,
                                touchInPiece = touch,
                                visualWidthPx = trayPieceWidthPx,
                                visualHeightPx = trayPieceHeightPx,
                            )
                        },
                        onDrag = { change, delta ->
                            change.consume()
                            dragState.moveDrag(
                                dragAmount = delta,
                                pieceId = piece.id,
                                pieces = allPieces,
                                snapRadiusFraction = snapRadiusFraction,
                            )
                        },
                        onDragEnd = {
                            dragState.endDrag(
                                pieceId = piece.id,
                                viewModel = viewModel,
                                snapRadiusFraction = snapRadiusFraction,
                                onSnapped = onSnapSound,
                            )
                        },
                        onDragCancel = { dragState.cancelDrag() },
                    )
                },
        ) {
            PuzzlePieceView(
                fullImage = puzzleBitmap,
                pieceId = piece.id,
                rows = gridRows,
                cols = gridCols,
                knobFraction = knobFraction,
                borderColor = borderColor,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private const val PUZZLE_TRAY_SLOT_ID = "puzzle_tray"
