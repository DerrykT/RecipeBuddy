package com.recipebuddy.util

import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.recipebuddy.database.Recipe_Info
import kotlinx.coroutines.Job
import java.time.LocalDateTime
import java.time.ZoneId

// Returns the total minutes contained in an hour:minute format
fun hourMinuteToMinute(hour: Int, minutes: Int): Int
    = hour * 60 + minutes

// Returns a (Hour,Minute) formatted pair given only minutes
fun minuteToHourMinute(minutes: Int): Pair<Int, Int>
    = Pair(minutes % 60, minutes / 60)

// Returns a hh:mm formatted string given only minutes
fun minuteToString(minutes: Int) = String.format("%02d:%02d", minutes % 60, minutes / 60)

// Returns a hh:mm:ss formatted string given only seconds
fun secondsToString(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

object Timer {
    var recipeName: MutableState<String> = mutableStateOf("")
    var timeLeft = mutableStateOf(0)
    var totalTime: Int = 0
    var instructionIndex = 0
    var thread: Thread? = null
    var timeStringState = mutableStateOf("")
    var isPaused = false
    var refreshState: MutableState<Int> = mutableStateOf(0)

    fun startTimer(totalSeconds: Int, name: String, index: Int, state: MutableState<String>, refreshState: MutableState<Int>) {
        destroy()

        timeStringState = state
        recipeName = mutableStateOf(name)
        timeLeft = mutableStateOf(totalSeconds)
        totalTime = totalSeconds
        instructionIndex = index
        this.refreshState = refreshState

        thread = Thread(Runnable {
            try {
                while(true) {
                    if(!isPaused) {
                        Log.d("Debugging", "time: $timeLeft")
                        Thread.sleep(1000)
                        timeLeft.value -= 1
                        timeStringState.value = secondsToString(timeLeft.value)
                        if(this.refreshState.value <= 0) this.refreshState.value--
                        else this.refreshState.value++
                    }
                    if(timeLeft.value < 0) {
                        Thread.sleep(5000)
                        break
                    }
                }
            } catch(_: java.lang.Exception) {}
        })

        thread?.start()
    }

    fun pauseTimer() {
        isPaused = true
    }

    fun resumeTimer() {
        isPaused = false
    }

    fun destroy() {
        recipeName = mutableStateOf("")
        timeLeft = mutableStateOf(0)
        totalTime = 0
        instructionIndex = 0
        timeStringState = mutableStateOf("")
        try {
            thread?.interrupt()
        } catch (_: java.lang.Exception) {}
        isPaused = false
    }
}