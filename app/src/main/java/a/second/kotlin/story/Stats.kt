package a.second.kotlin.story

import android.util.Log

class Stats {

    var statOneValue : Int = 0
    var statTwoValue: Int = 0
    var statThreeValue : Int = 0
    var statFourValue : Int = 0

    var remainder : Int = 0
    var statToAlter : Int = 1
    var statsAreInExcess : Boolean = false

    fun setInitialRandomValuesForStats() {
        statOneValue = (30..70).random()
        statTwoValue = (30..70).random()
        statThreeValue = (30..70).random()
        statFourValue = (30..70).random()

        var tempTotalValue = statOneValue + statTwoValue + statThreeValue + statFourValue

        var statArray = ArrayList<Int>()
        statArray.add(statOneValue)
        statArray.add(statTwoValue)
        statArray.add(statThreeValue)
        statArray.add(statFourValue)

        if (tempTotalValue > 200) {
            remainder = tempTotalValue - 200
            statsAreInExcess = true
        } else remainder = (200 - tempTotalValue)

        Log.i("testStat", "values before mod are $statOneValue $statTwoValue $statThreeValue $statFourValue")
    }

    fun iterateThroughStatsToAddOrSubtractRemainder() {
        if (statsAreInExcess) {
            while (totalValueOfStats() > 200) for (i in 1..4) {
                subtractRemainderFromStats(i)
            }
        } else {
            while (totalValueOfStats() < 200) for (i in 1..4) {
                addRemainderToStats(i)
            }
        }
        Log.i("testStat", "values after mod are $statOneValue $statTwoValue $statThreeValue $statFourValue")

    }

    fun addRemainderToStats(stat: Int) {
        if (totalValueOfStats() < 200 ) {
            if (stat == 1) if (statOneValue < 70) statOneValue++
            if (stat == 2) if (statTwoValue < 70) statTwoValue++
            if (stat == 3) if (statThreeValue < 70) statThreeValue++
            if (stat == 4) if (statFourValue < 70) statFourValue++
        }
    }

    fun subtractRemainderFromStats(stat: Int) {
        if (totalValueOfStats() > 200) {
            if (stat == 1) if (statOneValue > 30) statOneValue--
            if (stat == 2) if (statTwoValue > 30 ) statTwoValue--
            if (stat == 3) if (statThreeValue > 30) statThreeValue--
            if (stat == 4) if (statFourValue > 30) statFourValue--
        }
    }

    fun totalValueOfStats() : Int {
        return statOneValue + statTwoValue + statThreeValue + statFourValue
    }
}