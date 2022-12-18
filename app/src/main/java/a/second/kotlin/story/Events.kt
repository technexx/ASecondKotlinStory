package a.second.kotlin.story

class Events {

    var jobEventsBadArrayList = ArrayList<String>(R.array.job_events_bad)
    var jobEventsGoodArrayList = ArrayList<String>(R.array.job_events_good)
    var financesEventsBadArrayList = ArrayList<String>(R.array.finance_events_bad)
    var financesEventsGoodArrayList = ArrayList<String>(R.array.finance_events_good)
    var familyEventsBadArrayList = ArrayList<String>(R.array.family_events_bad)
    var familyEventsGoodArrayList = ArrayList<String>(R.array.family_events_good)
    var socialEventsBadArrayList = ArrayList<String>(R.array.social_life_bad)
    var socialEventsGoodArrayList = ArrayList<String>(R.array.social_life_good)

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
        rollEventBadOrGood()
        setRandomEventString()
        setRandomEventValue()
    }

    fun rollEventCategory() {
        rolledEvent = categoryOfEvent()
    }

    fun rollEventBadOrGood() {
        rolledBadOrGood = goodOrBadModifier()
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

    fun getRandomEventString() : String {
        return eventString
    }

    fun getRandomEventValue(): Int {
        return eventValue
    }

    fun categoryOfEvent() : Int {
        return (0..3).random()
    }

    fun goodOrBadModifier() : Int {
        return (0..1).random()
    }

    fun randomValueForBadEvents() : Int {
        return (5..15).random()
    }

    fun randomValueForGoodEvents() : Int {
        return (8..20).random()
    }

    fun randomBadJobEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until jobEventsBadArrayList.size).random()
        return jobEventsBadArrayList.get(eventRoll)
    }

    fun randomGoodJobEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until jobEventsGoodArrayList.size).random()
        return jobEventsGoodArrayList.get(eventRoll)
    }

    fun randomBadFinancesEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until financesEventsBadArrayList.size).random()
        return financesEventsBadArrayList.get(eventRoll)
    }

    fun randomGoodFinancesEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until financesEventsGoodArrayList.size).random()
        return financesEventsGoodArrayList.get(eventRoll)
    }

    fun randomBadFamilyEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until familyEventsBadArrayList.size).random()
        return familyEventsBadArrayList.get(eventRoll)
    }

    fun randomGoodFamilyEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until familyEventsGoodArrayList.size).random()
        return familyEventsGoodArrayList.get(eventRoll)
    }

    fun randomBadSocialEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until socialEventsBadArrayList.size).random()
        return socialEventsBadArrayList.get(eventRoll)
    }

    fun randomGoodSocialEvent() : String {
        //until is X to Y, excluding Y.
        val eventRoll = (0 until socialEventsGoodArrayList.size).random()
        return socialEventsGoodArrayList.get(eventRoll)
    }
}