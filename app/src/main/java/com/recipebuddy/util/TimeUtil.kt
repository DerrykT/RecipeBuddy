package com.recipebuddy.util

import android.os.CountDownTimer
import com.recipebuddy.database.Recipe_Info

// Returns the total minutes contained in an hour:minute format
fun hourMinuteToMinute(hour: Int, minutes: Int): Int
    = hour * 60 + minutes

// Returns a (Hour,Minute) formatted pair given only minutes
fun minuteToHourMinute(minutes: Int): Pair<Int, Int>
    = Pair(minutes % 60, minutes / 60)

// Returns a hh:mm formatted string given only minutes
fun minuteToString(minutes: Int) = String.format("%02d:%02d", minutes % 60, minutes / 60)

object Timer {
    private var recipeId: Int? = null
    private var instructionId: Int? = null
    private var time: Int? = 0
    private var onFinishFunction: (() -> Unit)? = null

    fun cancelTimer() {
        time = null
        instructionId = null
        time = null
    }

    fun startTimer(minutes: Int, recipeId: Int, instructionId: Int, onFinishFunction: () -> Unit) {
        this.recipeId = recipeId
        this.instructionId = instructionId
        this.time = minutes * 60 // Convert minutes to seconds
        this.onFinishFunction = onFinishFunction

        if(time != null) {
            val timer = object : CountDownTimer(time!!.toLong() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    time = time!!.dec()
                    val minutesUntilFinished = time!! / 60
                    val secondsUntilFinished = time!! % 60
                    val timeRemaining = String.format("%02d:%02d", minutesUntilFinished, secondsUntilFinished)
                    // Update the UI with the remaining time
                    // For example, you could update a TextView with the timeRemaining string
                }

                override fun onFinish() {
                    // The timer has finished, so perform any necessary actions
                }
            }
            timer.start()
        }
    }

    fun isTimerSet(): Boolean = (recipeId == null || instructionId == null || time == null)

    fun getTime() = time

    fun setOnFinishFunction(newOnFinishFunction: () -> Unit) {
        this.onFinishFunction = newOnFinishFunction
    }
}