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

    fun getRandomEventString() : String {
        if (categoryOfEvent() == JOB_EVENT) {
            if (goodOrBadModifier() == BAD_ROLL) return randomBadJobEvent() else return randomGoodJobEvent()

        }
        if (categoryOfEvent() == FINANCES_EVENT) {
            if (goodOrBadModifier() == BAD_ROLL) return randomBadFinancesEvent() else return randomGoodFinancesEvent()
        }

        if (categoryOfEvent() == FAMILY_EVENT) {
            if (goodOrBadModifier() == BAD_ROLL) return randomBadFamilyEvent() else return randomGoodFamilyEvent()
        }

        if (categoryOfEvent() == SOCIAL_EVENT) {
            if (goodOrBadModifier() == BAD_ROLL) return randomBadSocialEvent() else return randomGoodSocialEvent()
        }

        return ""
    }

    fun categoryOfEvent() : Int {
        return (0 until 5).random()
    }

    fun goodOrBadModifier() : Int {
        return (0 until 2).random()
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