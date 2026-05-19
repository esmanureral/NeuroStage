package com.esmanureral.neurostage.ui.patient.games.colormatch

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.util.lerp
import androidx.compose.ui.layout.boundsInRoot

@Stable
class ColorMatchDragHolder {
    val slotBounds = mutableStateMapOf<Int, Rect>()

    var draggingTokenId by mutableIntStateOf(NO_DRAG)
    var dragDiscTopLeftRoot by mutableStateOf(Offset.Zero)
    private var grabOffsetRoot by mutableStateOf(Offset.Zero)

    fun beginDrag(
        tokenId: Int,
        touchLocal: Offset,
        discCoordinates: LayoutCoordinates,
    ) {
        draggingTokenId = tokenId
        val fingerRoot = discCoordinates.localToRoot(touchLocal)
        val topLeft = discCoordinates.boundsInRoot().topLeft
        grabOffsetRoot = fingerRoot - topLeft
        dragDiscTopLeftRoot = topLeft
    }

    fun updateFingerRoot(fingerRoot: Offset) {
        dragDiscTopLeftRoot = fingerRoot - grabOffsetRoot
    }

    fun discCenterRoot(discRadiusPx: Float): Offset =
        dragDiscTopLeftRoot + Offset(discRadiusPx, discRadiusPx)

    fun findSlotUnderFinger(discRadiusPx: Float, hitSlopPx: Float = 32f): Int? {
        if (draggingTokenId == NO_DRAG) return null
        val center = discCenterRoot(discRadiusPx)
        return slotBounds.entries
            .filter { (_, rect) -> rect.expand(hitSlopPx).contains(center) }
            .minByOrNull { (_, rect) ->
                val c = rect.center
                val dx = c.x - center.x
                val dy = c.y - center.y
                dx * dx + dy * dy
            }
            ?.key
    }

    suspend fun animateDiscBackTo(homeTopLeftRoot: Offset) {
        val start = dragDiscTopLeftRoot
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        ) { progress, _ ->
            dragDiscTopLeftRoot = Offset(
                x = lerp(start.x, homeTopLeftRoot.x, progress),
                y = lerp(start.y, homeTopLeftRoot.y, progress),
            )
        }
    }

    fun clearDrag() {
        draggingTokenId = NO_DRAG
    }

    private fun Rect.expand(padding: Float): Rect = Rect(
        left = left - padding,
        top = top - padding,
        right = right + padding,
        bottom = bottom + padding,
    )

    companion object {
        const val NO_DRAG = -1
    }
}
