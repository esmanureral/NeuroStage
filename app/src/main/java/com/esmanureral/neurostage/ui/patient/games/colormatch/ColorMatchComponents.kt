package com.esmanureral.neurostage.ui.patient.games.colormatch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchColor
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchLevelDef
import com.esmanureral.neurostage.domain.patient.colormatch.ColorMatchPoolTouchPaddingDp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class ColorPoolToken(
    val id: Int,
    val color: ColorMatchColor,
    val inPool: Boolean,
)

@Composable
fun ColorMatchWoodenBoard(
    level: ColorMatchLevelDef,
    template: List<ColorMatchColor>,
    lockedSlots: Map<Int, ColorMatchColor>,
    poolTokens: List<ColorPoolToken>,
    dragHolder: ColorMatchDragHolder,
    cellSize: Dp,
    poolDiscSize: Dp,
    onSnap: () -> Unit,
    onTryPlace: (tokenId: Int, slotIndex: Int?) -> ColorMatchPlaceResult,
    modifier: Modifier = Modifier,
) {
    val wood = colorResource(R.color.color_match_board_wood)
    val slotBorder = colorResource(R.color.color_match_slot_border)
    val templateCard = colorResource(R.color.color_match_template_card)
    val pad = if (level.compact) 10.dp else 14.dp
    val gap = if (level.compact) 6.dp else 8.dp
    val labelSize = if (level.compact) 12.sp else 13.sp
    val poolRadiusPx = with(LocalDensity.current) { (poolDiscSize / 2).toPx() }
    var dragAreaOrigin by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(wood)
            .onGloballyPositioned { coords ->
                dragAreaOrigin = coords.boundsInRoot().topLeft
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad),
        ) {
            if (!level.compact) {
                Text(
                    text = stringResource(R.string.color_match_template_label),
                    color = colorResource(R.color.color_match_board_label),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = labelSize,
                )
                Spacer(modifier = Modifier.height(gap))
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = templateCard,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ColorMatchFilledGrid(
                    colors = template,
                    gridSize = level.gridSize,
                    cellSize = cellSize,
                    gap = gap,
                    modifier = Modifier.padding(if (level.compact) 8.dp else 12.dp),
                )
            }

            Spacer(modifier = Modifier.weight(0.42f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                Text(
                    text = stringResource(R.string.color_match_work_label),
                    color = colorResource(R.color.color_match_board_label),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = labelSize,
                )
                ColorMatchWorkGrid(
                    gridSize = level.gridSize,
                    template = template,
                    lockedSlots = lockedSlots,
                    cellSize = cellSize,
                    gap = gap,
                    slotBorder = slotBorder,
                    showSlotHint = level.showSlotHint,
                    dragHolder = dragHolder,
                )
            }

            Spacer(modifier = Modifier.weight(0.58f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                Text(
                    text = stringResource(R.string.color_match_pool_label),
                    color = colorResource(R.color.color_match_board_label),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = labelSize,
                )
                ColorMatchPool(
                    poolTokens = poolTokens,
                    poolDiscSize = poolDiscSize,
                    poolRadiusPx = poolRadiusPx,
                    dragHolder = dragHolder,
                    dragAreaOrigin = dragAreaOrigin,
                    compact = level.compact,
                    onSnap = onSnap,
                    onTryPlace = onTryPlace,
                )
            }
        }

        val draggingId = dragHolder.draggingTokenId
        if (draggingId != ColorMatchDragHolder.NO_DRAG) {
            val token = poolTokens.firstOrNull { it.id == draggingId }
            if (token != null) {
                val topLeft = dragHolder.dragDiscTopLeftRoot - dragAreaOrigin
                ColorDisc(
                    color = token.color,
                    size = poolDiscSize,
                    modifier = Modifier
                        .zIndex(20f)
                        .offset {
                            IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt())
                        },
                )
            }
        }
    }
}

enum class ColorMatchPlaceResult {
    ACCEPTED,
    WRONG_SLOT,
    NO_SLOT,
}

@Composable
private fun ColorMatchFilledGrid(
    colors: List<ColorMatchColor>,
    gridSize: Int,
    cellSize: Dp,
    gap: Dp,
    modifier: Modifier = Modifier,
) {
    ColorMatchGridLayout(
        gridSize = gridSize,
        cellSize = cellSize,
        gap = gap,
        modifier = modifier,
    ) { index ->
        FilledColorCell(color = colors[index], size = cellSize)
    }
}

@Composable
private fun ColorMatchWorkGrid(
    gridSize: Int,
    template: List<ColorMatchColor>,
    lockedSlots: Map<Int, ColorMatchColor>,
    cellSize: Dp,
    gap: Dp,
    slotBorder: Color,
    showSlotHint: Boolean,
    dragHolder: ColorMatchDragHolder,
) {
    val emptyBg = colorResource(R.color.color_match_slot_empty)
    ColorMatchGridLayout(gridSize = gridSize, cellSize = cellSize, gap = gap) { index ->
        val placed = lockedSlots[index]
        val hint = if (showSlotHint && placed == null) template[index] else null
        val slotBackground = when {
            placed != null -> null
            hint != null -> hint.displayColor().copy(alpha = 0.18f)
            else -> emptyBg
        }
        Box(
            modifier = Modifier
                .size(cellSize)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (slotBackground != null) {
                        Modifier
                            .background(slotBackground)
                            .border(1.5.dp, slotBorder, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    },
                )
                .onGloballyPositioned { coords ->
                    dragHolder.slotBounds[index] = coords.boundsInRoot()
                },
            contentAlignment = Alignment.Center,
        ) {
            if (placed != null) {
                FilledColorCell(color = placed, size = cellSize)
            }
        }
    }
}

@Composable
private fun ColorMatchGridLayout(
    gridSize: Int,
    cellSize: Dp,
    gap: Dp,
    modifier: Modifier = Modifier,
    cellContent: @Composable (index: Int) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        for (row in 0 until gridSize) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
            ) {
                for (col in 0 until gridSize) {
                    val index = row * gridSize + col
                    Box(modifier = Modifier.size(cellSize), contentAlignment = Alignment.Center) {
                        cellContent(index)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorMatchPool(
    poolTokens: List<ColorPoolToken>,
    poolDiscSize: Dp,
    poolRadiusPx: Float,
    dragHolder: ColorMatchDragHolder,
    dragAreaOrigin: Offset,
    compact: Boolean,
    onSnap: () -> Unit,
    onTryPlace: (tokenId: Int, slotIndex: Int?) -> ColorMatchPlaceResult,
) {
    val poolPad = if (compact) 12.dp else 14.dp
    val touchPad = ColorMatchPoolTouchPaddingDp.dp
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.color_match_pool_track))
            .padding(poolPad),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        poolTokens.filter { it.inPool }.forEach { token ->
            DraggableColorDisc(
                tokenId = token.id,
                color = token.color,
                discSize = poolDiscSize,
                touchPadding = touchPad,
                discRadiusPx = poolRadiusPx,
                dragHolder = dragHolder,
                dragAreaOrigin = dragAreaOrigin,
                enabled = dragHolder.draggingTokenId == ColorMatchDragHolder.NO_DRAG ||
                    dragHolder.draggingTokenId == token.id,
                onSnap = onSnap,
                onTryPlace = onTryPlace,
            )
        }
    }
}

@Composable
private fun DraggableColorDisc(
    tokenId: Int,
    color: ColorMatchColor,
    discSize: Dp,
    touchPadding: Dp,
    discRadiusPx: Float,
    dragHolder: ColorMatchDragHolder,
    dragAreaOrigin: Offset,
    enabled: Boolean,
    onSnap: () -> Unit,
    onTryPlace: (tokenId: Int, slotIndex: Int?) -> ColorMatchPlaceResult,
) {
    val scope = rememberCoroutineScope()
    var discCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val isDraggingThis = dragHolder.draggingTokenId == tokenId

    Box(
        modifier = Modifier.size(discSize + touchPadding * 2),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(discSize)
                .alpha(if (isDraggingThis) 0f else 1f)
                .onGloballyPositioned { discCoordinates = it }
                .then(
                    if (enabled) {
                        Modifier.pointerInput(tokenId) {
                            detectDragGestures(
                                onDragStart = { touch ->
                                    val coords = discCoordinates ?: return@detectDragGestures
                                    dragHolder.beginDrag(tokenId, touch, coords)
                                },
                                onDrag = { change, _ ->
                                    val coords = discCoordinates ?: return@detectDragGestures
                                    change.consume()
                                    dragHolder.updateFingerRoot(coords.localToRoot(change.position))
                                },
                                onDragEnd = {
                                    val coords = discCoordinates
                                    val slot = dragHolder.findSlotUnderFinger(discRadiusPx)
                                    when (onTryPlace(tokenId, slot)) {
                                        ColorMatchPlaceResult.ACCEPTED -> {
                                            onSnap()
                                            dragHolder.clearDrag()
                                        }
                                        ColorMatchPlaceResult.WRONG_SLOT,
                                        ColorMatchPlaceResult.NO_SLOT,
                                        -> {
                                            scope.launch {
                                                val home = coords?.boundsInRoot()?.topLeft
                                                    ?: dragHolder.dragDiscTopLeftRoot
                                                dragHolder.animateDiscBackTo(home)
                                                dragHolder.clearDrag()
                                            }
                                        }
                                    }
                                },
                                onDragCancel = { dragHolder.clearDrag() },
                            )
                        }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            ColorDisc(color = color, size = discSize)
        }
    }
}

@Composable
fun FilledColorCell(
    color: ColorMatchColor,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(color.displayColor())
            .border(2.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(12.dp)),
    )
}

@Composable
fun ColorDisc(
    color: ColorMatchColor,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.displayColor())
            .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
    )
}
