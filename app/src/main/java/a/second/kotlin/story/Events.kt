package a.second.kotlin.story

import android.content.Context
import android.util.Log

class Events(context: Context) {

    var jobEventsBadArrayList = context.resources.getStringArray(R.array.job_events_bad)
    var jobEventsGoodArrayList = context.resources.getStringArray(R.array.job_events_good)
    var financesEventsBadArrayList = context.resources.getStringArray(R.array.finance_events_bad)
    var financesEventsGoodArrayList = context.resources.getStringArray(R.array.finance_events_good)
    var familyEventsBadArrayList = context.resources.getStringArray(R.array.family_events_bad)
    var familyEventsGoodArrayList = context.resources.getStringArray(R.array.family_events_good)
    var socialEventsBadArrayList = context.resources.getStringArray(R.array.social_life_bad)
    var socialEventsGoodArrayList = context.resources.getStringArray(R.array.social_life_good)

    var JOB_EVENT = 0
    var FINANCES_EVENT = 1
    var FAMILY_EVENT = 2
    var SOCIAL_EVENT = 3

    var BAD_ROLL = 0
    var GOOD_ROLL = 1

    var rolledEvent = 0;
    var rolledBadOrGood = 0

    var eventString = ""
    var eventValue = 0

    fun aggregatedRoll() {
        rollEventCategory()
        setGoodOrBadModifier()
        setRandomEventString()
        setRandomEventValue()
    }

    fun rollEventCategory() {
        rolledEvent = categoryOfEvent()
    }

    fun setRandomEventString() {
        if (rolledEvent == JOB_EVENT) {
            if (rolledBadOrGood == BAD_ROLL) eventString = randomBadJobEvent() else eventString = randomGoodJobEvent()

        }
        if (rolledEvent== FINANCES_EVENT) {
            if (rolledBadOrGood == BAD_ROLL) eventString = randomBadFinancesEvent() else eventString = randomGoodFinancesEvent()
        }

        if (rolledEvent == FAMILY_EVENT) {
            if (rolledBadOrGood == BAD_ROLL) eventString = randomBadFamilyEvent() else eventString = randomGoodFamilyEvent()
        }

        if (rolledEvent == SOCIAL_EVENT) {
            if (rolledBadOrGood == BAD_ROLL) eventString = randomBadSocialEvent() else eventString = randomGoodSocialEvent()
        }
    }

    fun setRandomEventValue() {
        if (rolledBadOrGood == BAD_ROLL) eventValue = randomValueForBadEvents() else eventValue = randomValueForGoodEvents()
    }

    fun categoryOfEvent() : Int {
        return (0..3).random()
    }

    fun setGoodOrBadModifier() {
        val roll = (0..4).random()
        rolledBadOrGood = if (roll < 4) BAD_ROLL else GOOD_ROLL
    }

    fun randomValueForBadEvents() : Int {
        return (5..15).random()
    }

    fun randomValueForGoodEvents() : Int {
        return (3..10).random()
    }

    fun randomBadJobEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (jobEventsBadArrayList.indices).random()
        return jobEventsBadArrayList[eventRoll]
    }

    fun randomGoodJobEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (jobEventsGoodArrayList.indices).random()
        return jobEventsGoodArrayList[eventRoll]
    }

    fun randomBadFinancesEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (financesEventsBadArrayList.indices).random()
        return financesEventsBadArrayList[eventRoll]
    }

    fun randomGoodFinancesEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (financesEventsGoodArrayList.indices).random()
        return financesEventsGoodArrayList[eventRoll]
    }

    fun randomBadFamilyEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (familyEventsBadArrayList.indices).random()
        return familyEventsBadArrayList[eventRoll]
    }

    fun randomGoodFamilyEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (familyEventsGoodArrayList.indices).random()
        return familyEventsGoodArrayList[eventRoll]
    }

    fun randomBadSocialEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (socialEventsBadArrayList.indices).random()
        return socialEventsBadArrayList[eventRoll]
    }

    fun randomGoodSocialEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (socialEventsGoodArrayList.indices).random()
        return socialEventsGoodArrayList[eventRoll]
    }
}