package a.second.kotlin.story

import java.text.DecimalFormat

class DecimalToStringConversions {

    fun timeWithMillis(millis: Long): String {
        val dfOneZero = DecimalFormat("0")
        val dfTwoZeros = DecimalFormat("00")

        var ms = millis / 10
        if (ms > 99) {
            ms = ms % 100
        }

        var seconds: Long = 0
        seconds = millis / 1000
        var minutes: Long = 0
        var hours: Long = 0

        if (seconds >= 60) {
            minutes = seconds / 60
            seconds = seconds % 60
        }
        if (minutes >= 60) {
            hours = minutes / 60
            minutes = minutes % 60
        }

        return if (hours == 0L) {
            dfOneZero.format(minutes) + ":" + dfTwoZeros.format(seconds) + "." + dfTwoZeros.format(
                ms
            )
        } else {
            dfOneZero.format(hours) + ":" + dfTwoZeros.format(minutes) + ":" + dfTwoZeros.format(
                seconds
            ) + "." + dfTwoZeros.format(ms)
        }
    }
}