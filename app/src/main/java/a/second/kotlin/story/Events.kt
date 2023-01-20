package a.second.kotlin.story

import android.content.Context
import android.util.Log

class Events(context: Context) {

    private var jobEventsBadArrayList = context.resources.getStringArray(R.array.job_events_bad)
    private var jobEventsGoodArrayList = context.resources.getStringArray(R.array.job_events_good)
    private var financesEventsBadArrayList = context.resources.getStringArray(R.array.finance_events_bad)
    private var financesEventsGoodArrayList = context.resources.getStringArray(R.array.finance_events_good)
    private var familyEventsBadArrayList = context.resources.getStringArray(R.array.family_events_bad)
    private var familyEventsGoodArrayList = context.resources.getStringArray(R.array.family_events_good)
    private var socialEventsBadArrayList = context.resources.getStringArray(R.array.social_life_bad)
    private var socialEventsGoodArrayList = context.resources.getStringArray(R.array.social_life_good)

    private var JOB_EVENT = 0
    private var FINANCES_EVENT = 1
    private var FAMILY_EVENT = 2
    private var SOCIAL_EVENT = 3

    private var BAD_ROLL = 0
    private var GOOD_ROLL = 1

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

    private fun rollEventCategory() {
        rolledEvent = categoryOfEvent()
    }

    private fun setRandomEventString() {
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

    private fun setRandomEventValue() {
        if (rolledBadOrGood == BAD_ROLL) eventValue = -randomValueForBadEvents() else eventValue = randomValueForGoodEvents()
    }

    private fun categoryOfEvent() : Int { return (0..3).random() }

    private fun setGoodOrBadModifier() {
        val roll = (0..4).random()
        rolledBadOrGood = if (roll < 4) BAD_ROLL else GOOD_ROLL
    }

    private fun randomValueForBadEvents() : Int { return (5..15).random() }

    private fun randomValueForGoodEvents() : Int {
        return (3..10).random()
    }

    private fun randomBadJobEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (jobEventsBadArrayList.indices).random()
        return jobEventsBadArrayList[eventRoll]
    }

    private fun randomGoodJobEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (jobEventsGoodArrayList.indices).random()
        return jobEventsGoodArrayList[eventRoll]
    }

    private fun randomBadFinancesEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (financesEventsBadArrayList.indices).random()
        return financesEventsBadArrayList[eventRoll]
    }

    private fun randomGoodFinancesEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (financesEventsGoodArrayList.indices).random()
        return financesEventsGoodArrayList[eventRoll]
    }

    private fun randomBadFamilyEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (familyEventsBadArrayList.indices).random()
        return familyEventsBadArrayList[eventRoll]
    }

    private fun randomGoodFamilyEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (familyEventsGoodArrayList.indices).random()
        return familyEventsGoodArrayList[eventRoll]
    }

    private fun randomBadSocialEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (socialEventsBadArrayList.indices).random()
        return socialEventsBadArrayList[eventRoll]
    }

    private fun randomGoodSocialEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (socialEventsGoodArrayList.indices).random()
        return socialEventsGoodArrayList[eventRoll]
    }
}