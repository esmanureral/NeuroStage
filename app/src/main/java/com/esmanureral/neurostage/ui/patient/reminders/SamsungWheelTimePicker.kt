package com.esmanureral.neurostage.ui.patient.reminders

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

private val WheelItemHeight: Dp = 52.dp
private const val VisibleRows = 5

@Composable
fun SamsungWheelTimePicker(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = colorResource(R.color.patient_alarm_text_primary)
    val faded = colorResource(R.color.patient_alarm_text_faded)
    val wheelHeight = WheelItemHeight * VisibleRows

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            SamsungWheelColumn(
                count = 24,
                selected = hour,
                onSelected = onHourChange,
                formatter = { String.format("%02d", it) },
                primaryColor = primary,
                fadedColor = faded,
                wheelHeight = wheelHeight,
            )
        }
        Text(
            text = ":",
            fontSize = 46.sp,
            fontWeight = FontWeight.Light,
            color = primary,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            SamsungWheelColumn(
                count = 60,
                selected = minute,
                onSelected = onMinuteChange,
                formatter = { String.format("%02d", it) },
                primaryColor = primary,
                fadedColor = faded,
                wheelHeight = wheelHeight,
            )
        }
    }
}

@Composable
private fun SamsungWheelColumn(
    count: Int,
    selected: Int,
    onSelected: (Int) -> Unit,
    formatter: (Int) -> String,
    primaryColor: Color,
    fadedColor: Color,
    wheelHeight: Dp,
) {
    val itemHeightPx = with(LocalDensity.current) { WheelItemHeight.roundToPx() }
    val centerPadItems = (VisibleRows - 1) / 2
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selected - centerPadItems).coerceAtLeast(0),
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var centeredIndex by remember(selected) { mutableIntStateOf(selected) }

    LaunchedEffect(listState, count) {
        snapshotFlow {
            val first = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val index = first + centerPadItems + if (offset > itemHeightPx / 2) 1 else 0
            index.coerceIn(0, count - 1)
        }
            .distinctUntilChanged()
            .collect { index ->
                if (index != centeredIndex) {
                    centeredIndex = index
                    onSelected(index)
                }
            }
    }

    LaunchedEffect(selected) {
        if (selected != centeredIndex) {
            centeredIndex = selected
            listState.scrollToItem((selected - centerPadItems).coerceAtLeast(0))
        }
    }

    Box(
        modifier = Modifier
            .height(wheelHeight)
            .width(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = WheelItemHeight * centerPadItems),
            modifier = Modifier.height(wheelHeight),
        ) {
            items(count) { index ->
                val isCentered = index == centeredIndex
                Box(
                    modifier = Modifier
                        .height(WheelItemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = formatter(index),
                        fontSize = if (isCentered) 48.sp else 26.sp,
                        fontWeight = FontWeight.Light,
                        color = if (isCentered) primaryColor else fadedColor,
                    )
                }
            }
        }
    }
}
