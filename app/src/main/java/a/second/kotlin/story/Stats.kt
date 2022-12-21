package a.second.kotlin.story

import android.content.Context
import android.util.Log

class Stats (context : Context){
    var mContext : Context = context

    var statOneValue : Int = 0
    var statTwoValue: Int = 0
    var statThreeValue : Int = 0
    var statFourValue : Int = 0

    var statsAreInExcess : Boolean = false

    var statOneCritical = false
    var statTwoCritical = false
    var statThreeCritical = false
    var statFourCritical = false

    fun setInitialRandomValuesForStats() {
        statOneValue = (30..70).random()
        statTwoValue = (30..70).random()
        statThreeValue = (30..70).random()
        statFourValue = (30..70).random()

        var tempTotalValue = totalValueOfStats()

        if (tempTotalValue > 200) {
            statsAreInExcess = true
        }
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

    fun statsOneString() : String {
        return mContext.getString(R.string.stat_one).removeSuffix(":")
    }

    fun statsTwoString() : String {
        return mContext.getString(R.string.stat_two).removeSuffix(":")
    }

    fun statsThreeString() : String {
        return mContext.getString(R.string.stat_three).removeSuffix(":")
    }

    fun statsFourString() : String {
        return mContext.getString(R.string.stat_four).removeSuffix(":")
    }


    fun totalValueOfStats() : Int {
        return statOneValue + statTwoValue + statThreeValue + statFourValue
    }
}