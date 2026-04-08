package com.duongnd.kytucxa.core.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionEvent @Inject constructor() {
    private val _events = MutableSharedFlow<Unit>()
    val events = _events.asSharedFlow()

    suspend fun emitSessionExpired() {
        _events.emit(Unit)
    }
}
