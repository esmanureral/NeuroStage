package com.esmanureral.neurostage.ui.doctor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.data.UserRepository
import com.esmanureral.neurostage.navigation.RouteArgs
import com.esmanureral.neurostage.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DoctorResultDetailViewModel @Inject constructor(
    private val repo: UserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val scanTs: Long? = savedStateHandle.get<Long>(RouteArgs.SCAN_TS)

    val selected: StateFlow<MrScanRecord?> =
        repo.scanHistory
            .map { history -> resolveRecord(history, scanTs) }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(Constants.Ui.STATE_FLOW_STOP_TIMEOUT_MS),
                initialValue = null,
            )

    companion object {
        private fun resolveRecord(history: List<MrScanRecord>, timestamp: Long?): MrScanRecord? {
            val ts = timestamp ?: return null
            return history.firstOrNull { it.timestamp == ts }
        }
    }
}