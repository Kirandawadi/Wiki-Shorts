package com.example.hackgteeny

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TimerState {
    var currentTimeInSeconds by mutableStateOf(FOCUS_TIME_IN_SECONDS)
    var isRunning by mutableStateOf(false)
    var currentSession by mutableStateOf(1)
    var currentMode by mutableStateOf(TimerMode.FOCUS)
    var progress by mutableStateOf(1f)

    companion object {
        const val FOCUS_TIME_IN_SECONDS = 25 * 60 // 25 minutes
        const val SHORT_BREAK_TIME_IN_SECONDS = 5 * 60 // 5 minutes
        const val LONG_BREAK_TIME_IN_SECONDS = 15 * 60 // 15 minutes
        const val TOTAL_SESSIONS = 4
    }
}

enum class TimerMode {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
} 