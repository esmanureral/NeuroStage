package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.games.GameBackBottomBar
import com.esmanureral.neurostage.ui.patient.games.PrimaryGameButton
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MISMATCH_HIDE_MS = 1_500L
private const val MATCH_SUCCESS_MS = 750L
private const val LEVEL_ADVANCE_MS = 400L
private const val FLIP_ANIM_MS = 320

private enum class MatchPhase { PLAYING, WON }

private enum class CardHighlight { None, Success, Failure }

@Composable
fun MemoryMatchGameScreen(
    onBack: () -> Unit,
    viewModel: MemoryMatchViewModel = hiltViewModel(),
) {
    val sound = rememberMemoryMatchSound()
    val scope = rememberCoroutineScope()

    var levelIndex by remember { mutableIntStateOf(0) }
    var restored by remember { mutableStateOf(false) }
    val level = memoryMatchAllLevels[levelIndex]

    var deck by remember { mutableStateOf(buildShuffledDeck(level)) }
    var matchedPairIds by remember { mutableStateOf(setOf<Int>()) }
    var removedInstanceIds by remember { mutableStateOf(setOf<Int>()) }
    var revealedIds by remember { mutableStateOf(listOf<Int>()) }
    var highlightIds by remember { mutableStateOf(setOf<Int>()) }
    var highlightKind by remember { mutableStateOf(CardHighlight.None) }
    var locked by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf(MatchPhase.PLAYING) }

    val visibleDeck = remember(deck, removedInstanceIds) {
        deck.filter { it.instanceId !in removedInstanceIds }
    }

    val cardEmojiSize = when {
        level.pairMode == MemoryMatchPairMode.ImageAndWord -> PatientDimens.memoryMatchCardEmojiSizeDense
        level.pairs.size >= 8 -> PatientDimens.memoryMatchCardEmojiSizeDense
        else -> PatientDimens.memoryMatchCardEmojiSize
    }
    val cardWordSize = PatientDimens.memoryMatchCardWordSize

    fun loadLevel(index: Int, persist: Boolean = true) {
        val nextLevel = memoryMatchAllLevels[index]
        levelIndex = index
        deck = buildShuffledDeck(nextLevel)
        matchedPairIds = emptySet()
        removedInstanceIds = emptySet()
        revealedIds = emptyList()
        highlightIds = emptySet()
        highlightKind = CardHighlight.None
        locked = false
        phase = MatchPhase.PLAYING
        if (persist) {
            viewModel.saveLevel(index)
        }
    }

    fun restartFromBeginning() {
        viewModel.resetProgress()
        loadLevel(0)
    }

    LaunchedEffect(Unit) {
        if (restored) return@LaunchedEffect
        if (viewModel.isAllComplete()) {
            phase = MatchPhase.WON
            levelIndex = memoryMatchAllLevels.lastIndex
        } else {
            loadLevel(viewModel.currentLevelIndex(), persist = false)
        }
    }

    val levelIndexState = rememberUpdatedState(levelIndex)
    val phaseState = rememberUpdatedState(phase)
    DisposableEffect(Unit) {
        onDispose {
            if (phaseState.value == MatchPhase.PLAYING) {
                viewModel.saveLevel(levelIndexState.value)
            }
        }
    }

    fun isFaceUp(card: MemoryMatchCard): Boolean =
        card.instanceId in revealedIds || card.instanceId in highlightIds

    fun cardHighlight(card: MemoryMatchCard): CardHighlight =
        if (card.instanceId in highlightIds) highlightKind else CardHighlight.None

    fun onLevelCleared() {
        sound.playShine()
        val completedIndex = levelIndex
        viewModel.onLevelCompleted(completedIndex)
        if (completedIndex < memoryMatchAllLevels.lastIndex) {
            scope.launch {
                delay(LEVEL_ADVANCE_MS)
                loadLevel(completedIndex + 1)
            }
        } else {
            phase = MatchPhase.WON
        }
    }

    fun onCardClick(card: MemoryMatchCard) {
        if (locked || phase != MatchPhase.PLAYING) return
        if (card.instanceId in removedInstanceIds) return
        if (isFaceUp(card)) return
        if (revealedIds.size >= 2) return

        sound.playCardClick()

        val newRevealed = revealedIds + card.instanceId
        revealedIds = newRevealed

        if (newRevealed.size == 2) {
            val first = deck.first { it.instanceId == newRevealed[0] }
            val second = deck.first { it.instanceId == newRevealed[1] }
            locked = true

            if (first.pairId == second.pairId) {
                scope.launch {
                    highlightIds = newRevealed.toSet()
                    highlightKind = CardHighlight.Success
                    delay(MATCH_SUCCESS_MS)
                    removedInstanceIds = removedInstanceIds + newRevealed.toSet()
                    matchedPairIds = matchedPairIds + first.pairId
                    highlightIds = emptySet()
                    highlightKind = CardHighlight.None
                    revealedIds = emptyList()
                    if (matchedPairIds.size == level.pairs.size) {
                        onLevelCleared()
                    } else {
                        locked = false
                    }
                }
            } else {
                scope.launch {
                    highlightIds = newRevealed.toSet()
                    highlightKind = CardHighlight.Failure
                    delay(MISMATCH_HIDE_MS)
                    highlightIds = emptySet()
                    highlightKind = CardHighlight.None
                    revealedIds = emptyList()
                    locked = false
                }
            }
        }
    }

    Scaffold(
        containerColor = PatientColors.gameBackgroundCream,
        bottomBar = { GameBackBottomBar(onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = PatientDimens.gameScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (phase) {
                MatchPhase.PLAYING -> {
                    Text(
                        text = stringResource(level.titleRes),
                        fontSize = PatientDimens.gameRecallTitleSize,
                        fontWeight = FontWeight.Bold,
                        color = PatientColors.matchAccent,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapS))
                    Text(
                        text = stringResource(
                            R.string.memory_match_pairs_progress,
                            matchedPairIds.size,
                            level.pairs.size,
                        ),
                        fontSize = PatientDimens.gameCountTextSize,
                        color = PatientColors.gameTextMuted,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapS))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(PatientDimens.memoryMatchBoardCorner))
                            .background(PatientColors.matchBoardBackground)
                            .padding(PatientDimens.memoryMatchBoardPadding),
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(level.gridColumns),
                            horizontalArrangement = Arrangement.spacedBy(PatientDimens.gameGridGapS),
                            verticalArrangement = Arrangement.spacedBy(PatientDimens.gameGridGapS),
                            modifier = Modifier.fillMaxWidth(),
                            userScrollEnabled = level.gridColumns >= 4 ||
                                    level.pairMode == MemoryMatchPairMode.ImageAndWord,
                        ) {
                            items(
                                items = visibleDeck,
                                key = { it.instanceId },
                            ) { card ->
                                MemoryMatchCardView(
                                    card = card,
                                    faceUp = isFaceUp(card),
                                    highlight = cardHighlight(card),
                                    emojiSize = cardEmojiSize,
                                    wordSize = cardWordSize,
                                    enabled = !locked && !isFaceUp(card),
                                    onClick = { onCardClick(card) },
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(FLIP_ANIM_MS),
                                        fadeOutSpec = tween(FLIP_ANIM_MS),
                                        placementSpec = tween(FLIP_ANIM_MS),
                                    ),
                                )
                            }
                        }
                    }
                }

                MatchPhase.WON -> {
                    Text(
                        text = stringResource(R.string.memory_match_success_emoji),
                        fontSize = PatientDimens.gameSuccessEmojiSize,
                    )
                    Spacer(Modifier.height(PatientDimens.gameBlockGapXl))
                    PrimaryGameButton(
                        text = stringResource(R.string.memory_match_btn_play_again),
                        containerColor = PatientColors.matchAccent,
                        onClick = { restartFromBeginning() },
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryMatchCardView(
    card: MemoryMatchCard,
    faceUp: Boolean,
    highlight: CardHighlight,
    emojiSize: TextUnit,
    wordSize: TextUnit,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(PatientDimens.memoryMatchCardCorner)
    val closedDesc = stringResource(R.string.memory_match_cd_card_closed)
    val openDesc = stringResource(
        R.string.memory_match_cd_card_open,
        stringResource(card.labelRes),
    )

    val rotation by animateFloatAsState(
        targetValue = if (faceUp) 180f else 0f,
        animationSpec = tween(FLIP_ANIM_MS),
        label = "cardFlipY",
    )

    val clickMod = if (enabled) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = PatientColors.matchAccent),
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics {
                contentDescription = if (faceUp) openDesc else closedDesc
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(shape)
            .then(clickMod),
        contentAlignment = Alignment.Center,
    ) {
        if (rotation <= 90f) {
            MatchCardBack(shape = shape)
        } else {
            MatchCardFront(
                face = card.face,
                emojiSize = emojiSize,
                wordSize = wordSize,
                highlight = highlight,
                shape = shape,
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            )
        }
    }
}

@Composable
private fun MatchCardBack(shape: RoundedCornerShape) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .background(PatientColors.matchCardBack)
            .border(
                width = PatientDimens.memoryMatchCardBorder,
                color = PatientColors.matchCardBackBorder,
                shape = shape,
            ),
    )
}

@Composable
private fun MatchCardFront(
    face: MemoryMatchCardFace,
    emojiSize: TextUnit,
    wordSize: TextUnit,
    highlight: CardHighlight,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, borderColor) = when (highlight) {
        CardHighlight.Success -> PatientColors.matchMatchedBackground to PatientColors.matchMatchedBorder
        CardHighlight.Failure -> PatientColors.matchMismatchBackground to PatientColors.matchMismatchBorder
        CardHighlight.None -> PatientColors.matchCardFace to PatientColors.matchCardFace.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = PatientDimens.memoryMatchCardBorder,
                color = borderColor,
                shape = shape,
            )
            .padding(horizontal = PatientDimens.gameGridGapS),
        contentAlignment = Alignment.Center,
    ) {
        when (face) {
            is MemoryMatchCardFace.Emoji -> Text(
                text = stringResource(face.res),
                fontSize = emojiSize,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            is MemoryMatchCardFace.Word -> Text(
                text = stringResource(face.res),
                fontSize = wordSize,
                fontWeight = FontWeight.Bold,
                color = PatientColors.matchAccent,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}
