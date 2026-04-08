package com.duongnd.kytucxa.feature

import androidx.lifecycle.ViewModel
import com.duongnd.kytucxa.core.utils.SessionEvent
import com.duongnd.kytucxa.core.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val sessionEvent: SessionEvent,
    val sessionManager: SessionManager
) : ViewModel()
