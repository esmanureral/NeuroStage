package com.esmanureral.neurostage

import androidx.lifecycle.ViewModel
import com.esmanureral.neurostage.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    /** Son MR tarama sonucu (null = hiç taranmadı) */
    val lastMrStageIndex: StateFlow<Int?> = userRepository.lastMrStageIndex
}