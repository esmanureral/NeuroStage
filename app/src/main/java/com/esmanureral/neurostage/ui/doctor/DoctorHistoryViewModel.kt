package com.esmanureral.neurostage.ui.doctor

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.MrScanRecord
import com.esmanureral.neurostage.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DoctorHistoryViewModel @Inject constructor(
    repo: UserRepository,
) : ViewModel() {

    val history: StateFlow<List<MrScanRecord>> = repo.scanHistory
}
