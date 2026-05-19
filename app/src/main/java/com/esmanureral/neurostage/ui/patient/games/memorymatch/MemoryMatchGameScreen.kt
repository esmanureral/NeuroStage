package com.esmanureral.neurostage.ui.patient.games.memorymatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.patient.games.GameScreenTopBar
import com.esmanureral.neurostage.ui.patient.games.PrimaryGameButton
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MATCH_SUCCESS_MS = 750L
private const val LEVEL_ADVANCE_MS = 400L

private enum class MatchPhase { PLAYING, WON }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchGameScreen(
    stageIndex: Int?,
    onBack: () -> Unit,
    viewModel: MemoryMatchViewModel = hiltViewModel(),
) {
    val screenTitle = stringResource(R.string.patient_game_memory_match_short)
    val sound = rememberMemoryMatchSound()
    val scope = rememberCoroutineScope()
    val allLevels = remember(stageIndex) { memoryMatchLevelsForStage(stageIndex) }

    if (allLevels.isEmpty()) {
        Scaffold(
            containerColor = PatientColors.gameBackgroundCream,
            topBar = { GameScreenTopBar(title = screenTitle, onBack = onBack) },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.memory_match_level_1_title),
                    color = PatientColors.gameTextMuted,
                )
            }
        }
        return
    }

    var levelIndex by remember(stageIndex) {
        mutableIntStateOf(viewModel.coerceLevelIndex(stageIndex, 0))
    }
    var phase by remember(stageIndex) {
        mutableStateOf(
            if (viewModel.isAllComplete(stageIndex)) MatchPhase.WON else MatchPhase.PLAYING,
        )
    }
    var deck by remember(stageIndex) { mutableStateOf(emptyList<MemoryMatchCard>()) }
    var openClosedBoard by remember(stageIndex) { mutableStateOf<OpenClosedMatchBoard?>(null) }
    var matchedPairIds by remember(stageIndex) { mutableStateOf(setOf<Int>()) }
    var removedInstanceIds by remember(stageIndex) { mutableStateOf(setOf<Int>()) }
    var matchedClosedIds by remember(stageIndex) { mutableStateOf(setOf<Int>()) }
    var revealedIds by remember(stageIndex) { mutableStateOf(listOf<Int>()) }
    var revealedClosedId by remember(stageIndex) { mutableStateOf<Int?>(null) }
    var highlightIds by remember(stageIndex) { mutableStateOf(setOf<Int>()) }
    var highlightClosedId by remember(stageIndex) { mutableStateOf<Int?>(null) }
    var highlightOpenId by remember(stageIndex) { mutableStateOf<Int?>(null) }
    var highlightKind by remember(stageIndex) { mutableStateOf(MemoryMatchHighlight.None) }
    var locked by remember(stageIndex) { mutableStateOf(false) }
    var sessionReady by remember(stageIndex) { mutableStateOf(false) }

    val safeLevelIndex = viewModel.coerceLevelIndex(stageIndex, levelIndex)
    val level = allLevels[safeLevelIndex]
    val minimalUi = level.uiStyle == MemoryMatchUiStyle.Minimal
    val isOpenClosed = level.layoutMode == MemoryMatchLayoutMode.OpenClosed
    val cleanBoard = minimalUi || level.profile != MemoryMatchLevelProfile.Standard

    val visibleDeck = remember(deck, removedInstanceIds) {
        deck.filter { it.instanceId !in removedInstanceIds }
    }

    val cardEmojiSize = when (level.profile) {
        MemoryMatchLevelProfile.OpenClosedAnchors -> PatientDimens.memoryMatchCardEmojiSizeDense
        MemoryMatchLevelProfile.ModerateGridCompact -> PatientDimens.memoryMatchCardEmojiSizeDense
        MemoryMatchLevelProfile.PrimaryColorsSpacious -> PatientDimens.memoryMatchModerateCardEmojiSize
        MemoryMatchLevelProfile.HighContrastBasic -> PatientDimens.memoryMatchCardEmojiSize
        MemoryMatchLevelProfile.Standard -> when {
            level.pairMode == MemoryMatchPairMode.ImageAndWord -> PatientDimens.memoryMatchCardEmojiSizeDense
            level.pairs.size >= 8 -> PatientDimens.memoryMatchCardEmojiSizeDense
            else -> PatientDimens.memoryMatchCardEmojiSize
        }
    }
    val anchorEmojiSize = PatientDimens.memoryMatchModerateCardEmojiSize
    val boardPadding = when (level.profile) {
        MemoryMatchLevelProfile.PrimaryColorsSpacious,
        MemoryMatchLevelProfile.HighContrastBasic,
        MemoryMatchLevelProfile.ModerateGridCompact,
        MemoryMatchLevelProfile.OpenClosedAnchors,
            -> PatientDimens.memoryMatchModerateBoardPadding

        MemoryMatchLevelProfile.Standard -> PatientDimens.memoryMatchBoardPadding
    }
    val gridGap = when (level.profile) {
        MemoryMatchLevelProfile.Standard -> PatientDimens.gameGridGapS
        else -> PatientDimens.memoryMatchModerateGridGap
    }
    val mismatchHideMs = level.profile.mismatchHideMs()
    val screenBackground = if (cleanBoard) Color.White else PatientColors.gameBackgroundCream
    val boardBackground = if (cleanBoard) Color.White else PatientColors.matchBoardBackground

    fun resetRoundState() {
        matchedPairIds = emptySet()
        removedInstanceIds = emptySet()
        matchedClosedIds = emptySet()
        revealedIds = emptyList()
        revealedClosedId = null
        highlightIds = emptySet()
        highlightClosedId = null
        highlightOpenId = null
        highlightKind = MemoryMatchHighlight.None
        locked = false
        phase = MatchPhase.PLAYING
    }

    fun loadLevel(index: Int, persist: Boolean = true) {
        val safeIndex = viewModel.coerceLevelIndex(stageIndex, index)
        val nextLevel = allLevels[safeIndex]
        levelIndex = safeIndex
        resetRoundState()
        when (nextLevel.layoutMode) {
            MemoryMatchLayoutMode.StandardGrid -> {
                openClosedBoard = null
                deck = buildShuffledDeck(nextLevel)
            }

            MemoryMatchLayoutMode.OpenClosed -> {
                deck = emptyList()
                openClosedBoard = buildOpenClosedBoard(nextLevel)
            }
        }
        if (persist) {
            viewModel.saveLevel(stageIndex, safeIndex)
        }
    }

    fun restartFromBeginning() {
        viewModel.resetProgress(stageIndex)
        loadLevel(0)
    }

    LaunchedEffect(stageIndex) {
        if (sessionReady) return@LaunchedEffect
        if (viewModel.isAllComplete(stageIndex)) {
            phase = MatchPhase.WON
            levelIndex = allLevels.lastIndex
            val last = allLevels.last()
            if (last.layoutMode == MemoryMatchLayoutMode.OpenClosed) {
                openClosedBoard = buildOpenClosedBoard(last)
                deck = emptyList()
            } else {
                openClosedBoard = null
                deck = buildShuffledDeck(last)
            }
        } else {
            loadLevel(viewModel.currentLevelIndex(stageIndex), persist = false)
        }
    }

    val levelIndexState = rememberUpdatedState(levelIndex)
    val phaseState = rememberUpdatedState(phase)
    val stageIndexState = rememberUpdatedState(stageIndex)
    DisposableEffect(stageIndex) {
        onDispose {
            if (phaseState.value == MatchPhase.PLAYING) {
                viewModel.saveLevel(
                    stageIndexState.value,
                    viewModel.coerceLevelIndex(stageIndexState.value, levelIndexState.value),
                )
            }
        }
    }

    fun isFaceUp(card: MemoryMatchCard): Boolean =
        card.instanceId in revealedIds || card.instanceId in highlightIds

    fun cardHighlight(card: MemoryMatchCard): MemoryMatchHighlight =
        if (card.instanceId in highlightIds) highlightKind else MemoryMatchHighlight.None

    fun onLevelCleared() {
        sound.playShine()
        val completedIndex = levelIndex
        viewModel.onLevelCompleted(stageIndex, completedIndex)
        if (completedIndex < allLevels.lastIndex) {
            scope.launch {
                delay(LEVEL_ADVANCE_MS)
                loadLevel(completedIndex + 1)
            }
        } else {
            phase = MatchPhase.WON
        }
    }

    fun onStandardCardClick(card: MemoryMatchCard) {
        if (locked || phase != MatchPhase.PLAYING) return
        if (card.instanceId in removedInstanceIds) return
        if (isFaceUp(card)) return
        if (revealedIds.size >= 2) return

        sound.playCardClick()
        val newRevealed = revealedIds + card.instanceId
        revealedIds = newRevealed

        if (newRevealed.size == 2) {
            val first = deck.firstOrNull { it.instanceId == newRevealed[0] } ?: return
            val second = deck.firstOrNull { it.instanceId == newRevealed[1] } ?: return
            locked = true

            if (first.pairId == second.pairId) {
                scope.launch {
                    highlightIds = newRevealed.toSet()
                    highlightKind = MemoryMatchHighlight.Success
                    delay(MATCH_SUCCESS_MS)
                    removedInstanceIds = removedInstanceIds + newRevealed.toSet()
                    matchedPairIds = matchedPairIds + first.pairId
                    highlightIds = emptySet()
                    highlightKind = MemoryMatchHighlight.None
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
                    highlightKind = MemoryMatchHighlight.Failure
                    delay(mismatchHideMs)
                    highlightIds = emptySet()
                    highlightKind = MemoryMatchHighlight.None
                    revealedIds = emptyList()
                    locked = false
                }
            }
        }
    }

    fun onClosedPoolClick(card: MemoryMatchCard) {
        if (locked || phase != MatchPhase.PLAYING) return
        if (card.instanceId in matchedClosedIds) return
        if (revealedClosedId == card.instanceId) return
        sound.playCardClick()
        revealedClosedId = card.instanceId
    }

    fun onOpenAnchorClick(anchor: MemoryMatchCard) {
        val board = openClosedBoard ?: return
        val revealedId = revealedClosedId ?: return
        if (locked) return
        val closed = board.closedPool.firstOrNull { it.instanceId == revealedId } ?: return
        locked = true
        sound.playCardClick()

        if (closed.pairId == anchor.pairId) {
            scope.launch {
                highlightClosedId = closed.instanceId
                highlightOpenId = anchor.instanceId
                highlightKind = MemoryMatchHighlight.Success
                delay(MATCH_SUCCESS_MS)
                matchedClosedIds = matchedClosedIds + closed.instanceId
                revealedClosedId = null
                highlightClosedId = null
                highlightOpenId = null
                highlightKind = MemoryMatchHighlight.None
                locked = false
                if (matchedClosedIds.size >= board.closedPool.size) {
                    onLevelCleared()
                }
            }
        } else {
            scope.launch {
                highlightClosedId = closed.instanceId
                highlightOpenId = anchor.instanceId
                highlightKind = MemoryMatchHighlight.Failure
                delay(mismatchHideMs)
                revealedClosedId = null
                highlightClosedId = null
                highlightOpenId = null
                highlightKind = MemoryMatchHighlight.None
                locked = false
            }
        }
    }

    Scaffold(
        containerColor = screenBackground,
        topBar = { GameScreenTopBar(title = screenTitle, onBack = onBack) },
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
                    if (minimalUi) {
                        Text(
                            text = stringResource(level.titleRes),
                            fontSize = PatientDimens.gameCountTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = PatientColors.matchAccent,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(PatientDimens.gameBlockGapS))
                    } else {
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
                    }

                    if (isOpenClosed) {
                        openClosedBoard?.let { board ->
                            MemoryMatchOpenClosedLayout(
                                board = board,
                                matchedClosedIds = matchedClosedIds,
                                revealedClosedId = revealedClosedId,
                                highlightClosedId = highlightClosedId,
                                highlightOpenId = highlightOpenId,
                                highlight = highlightKind,
                                locked = locked,
                                emojiSize = cardEmojiSize,
                                anchorEmojiSize = anchorEmojiSize,
                                boardPadding = boardPadding,
                                gridGap = gridGap,
                                highContrast = cleanBoard,
                                onClosedClick = ::onClosedPoolClick,
                                onOpenClick = ::onOpenAnchorClick,
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(PatientDimens.memoryMatchBoardCorner))
                                .background(boardBackground)
                                .padding(boardPadding),
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(level.gridColumns),
                                horizontalArrangement = Arrangement.spacedBy(gridGap),
                                verticalArrangement = Arrangement.spacedBy(gridGap),
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
                                        wordSize = PatientDimens.memoryMatchCardWordSize,
                                        highContrast = cleanBoard,
                                        enabled = !locked && !isFaceUp(card),
                                        onClick = { onStandardCardClick(card) },
                                    )
                                }
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
