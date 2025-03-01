package com.example.hackgteeny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hackgteeny.ui.theme.HackGTeenyTheme
import com.example.hackgteeny.ui.theme.PomodoroBlue
import com.example.hackgteeny.ui.theme.PomodoroLightBlue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackGTeenyTheme {
                PomodoroApp()
            }
        }
    }
}

@Composable
fun PomodoroApp() {
    var selectedItem by remember { mutableStateOf(0) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Navigation Rail (Sidebar)
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Logo/Title
                    Text(
                        text = "Pomodoro",
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = PomodoroBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Timer Navigation Item
                    NavigationRailItem(
                        selected = selectedItem == 0,
                        onClick = { selectedItem = 0 },
                        icon = { Icon(Icons.Default.Timer, contentDescription = "Timer") },
                        label = { Text("Timer") }
                    )
                    
                    // Settings Navigation Item
                    NavigationRailItem(
                        selected = selectedItem == 1,
                        onClick = { selectedItem = 1 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
            
            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedItem) {
                    0 -> TimerScreen(modifier = Modifier.padding(16.dp))
                    1 -> Text("Settings Screen Coming Soon")
                }
            }
        }
    }
}

@Composable
fun TimerScreen(modifier: Modifier = Modifier) {
    val timerState = remember { TimerState() }
    
    // Timer logic
    LaunchedEffect(timerState.isRunning) {
        while (timerState.isRunning && timerState.currentTimeInSeconds > 0) {
            delay(1.seconds)
            timerState.currentTimeInSeconds--
            timerState.progress = timerState.currentTimeInSeconds.toFloat() / when(timerState.currentMode) {
                TimerMode.FOCUS -> TimerState.FOCUS_TIME_IN_SECONDS
                TimerMode.SHORT_BREAK -> TimerState.SHORT_BREAK_TIME_IN_SECONDS
                TimerMode.LONG_BREAK -> TimerState.LONG_BREAK_TIME_IN_SECONDS
            }
        }
        
        if (timerState.currentTimeInSeconds == 0) {
            handleSessionComplete(timerState)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Timer Circle
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .aspectRatio(1f)
                    .fillMaxWidth(0.8f),
                contentAlignment = Alignment.Center
            ) {
                // Progress Circle
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                ) {
                    val strokeWidth = size.width * 0.08f
                    
                    // Background circle
                    drawArc(
                        color = PomodoroLightBlue,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width, size.width)
                    )
                    
                    // Progress circle
                    drawArc(
                        color = PomodoroBlue,
                        startAngle = -90f,
                        sweepAngle = 360f * timerState.progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width, size.width)
                    )
                }
                
                // Timer Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(timerState.currentTimeInSeconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = timerState.currentMode.name,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = { resetTimer(timerState) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Play/Pause Button
                FilledIconButton(
                    onClick = { timerState.isRunning = !timerState.isRunning },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (timerState.isRunning) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Skip Button
                IconButton(
                    onClick = { skipSession(timerState) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Skip",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Session Counter
            Text(
                text = "${timerState.currentSession} of ${TimerState.TOTAL_SESSIONS}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "sessions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(timeInSeconds: Int): String {
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun resetTimer(timerState: TimerState) {
    timerState.isRunning = false
    // Just reset the current timer without changing mode
    timerState.currentTimeInSeconds = when(timerState.currentMode) {
        TimerMode.FOCUS -> TimerState.FOCUS_TIME_IN_SECONDS
        TimerMode.SHORT_BREAK -> TimerState.SHORT_BREAK_TIME_IN_SECONDS
        TimerMode.LONG_BREAK -> TimerState.LONG_BREAK_TIME_IN_SECONDS
    }
    timerState.progress = 1f
}

private fun skipSession(timerState: TimerState) {
    // Allow skipping to next mode at any time
    when (timerState.currentMode) {
        TimerMode.FOCUS -> {
            if (timerState.currentSession == TimerState.TOTAL_SESSIONS) {
                timerState.currentMode = TimerMode.LONG_BREAK
            } else {
                timerState.currentMode = TimerMode.SHORT_BREAK
                timerState.currentSession++
            }
        }
        TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK -> {
            timerState.currentMode = TimerMode.FOCUS
            if (timerState.currentMode == TimerMode.LONG_BREAK) {
                timerState.currentSession = 1
            }
        }
    }
    
    // Reset timer for new mode
    timerState.isRunning = false
    timerState.currentTimeInSeconds = when(timerState.currentMode) {
        TimerMode.FOCUS -> TimerState.FOCUS_TIME_IN_SECONDS
        TimerMode.SHORT_BREAK -> TimerState.SHORT_BREAK_TIME_IN_SECONDS
        TimerMode.LONG_BREAK -> TimerState.LONG_BREAK_TIME_IN_SECONDS
    }
    timerState.progress = 1f
}

private fun handleSessionComplete(timerState: TimerState) {
    timerState.isRunning = false
    
    when (timerState.currentMode) {
        TimerMode.FOCUS -> {
            if (timerState.currentSession == TimerState.TOTAL_SESSIONS) {
                timerState.currentMode = TimerMode.LONG_BREAK
            } else {
                timerState.currentMode = TimerMode.SHORT_BREAK
                timerState.currentSession++
            }
        }
        TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK -> {
            timerState.currentMode = TimerMode.FOCUS
            if (timerState.currentMode == TimerMode.LONG_BREAK) {
                timerState.currentSession = 1
            }
        }
    }
    
    timerState.currentTimeInSeconds = when(timerState.currentMode) {
        TimerMode.FOCUS -> TimerState.FOCUS_TIME_IN_SECONDS
        TimerMode.SHORT_BREAK -> TimerState.SHORT_BREAK_TIME_IN_SECONDS
        TimerMode.LONG_BREAK -> TimerState.LONG_BREAK_TIME_IN_SECONDS
    }
    timerState.progress = 1f
}