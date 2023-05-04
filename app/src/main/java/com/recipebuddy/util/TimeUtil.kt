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

// Returns a hh:mm:ss formatted string given only seconds
fun secondsToString(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

object Timer {

}