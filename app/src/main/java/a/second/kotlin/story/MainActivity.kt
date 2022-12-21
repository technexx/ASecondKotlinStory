
package a.second.kotlin.story

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.launch


//Todo: Use VIEWMODEL.
//Todo: Remember to SUSPEND and not BLOCK if using UI thread. runBlocking is a default CoroutineScope.

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    var job: Job = Job()
    lateinit var Events : Events
    lateinit var Stats : Stats
    lateinit var DecimalToStringConversions: DecimalToStringConversions

    lateinit var statOneHeader : TextView
    lateinit var statOneTextView : TextView
    lateinit var statTwoHeader : TextView
    lateinit var statTwoTextView : TextView
    lateinit var statThreeHeader : TextView
    lateinit var statThreeTextView : TextView
    lateinit var statFourHeader : TextView
    lateinit var statFourTextView : TextView

    lateinit var existenceTimerTextView : TextView
    lateinit var startStopButton : TextView
    lateinit var eventTextView : TextView
    lateinit var statWarningTextView : TextView

    private var totalSpawnTimeInMilliseconds : Long = 0
    private var temporaryEventTime : Long = 0
    private var randomMillisValueForEvent : Long = 0

    private var eventString : String = ""
    private var eventValue : Int = 0

    private var JOB_EVENT = 0
    private var FINANCES_EVENT = 1
    private var FAMILY_EVENT = 2
    private var SOCIAL_EVENT = 3

    private var BAD_ROLL = 0
    private var GOOD_ROLL = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Events = Events(applicationContext)
        Stats = Stats(applicationContext)
        DecimalToStringConversions = DecimalToStringConversions()

        statOneHeader = findViewById(R.id.stat_one_header_textView)
        statTwoHeader = findViewById(R.id.stat_two_header_textView)
        statThreeHeader = findViewById(R.id.stat_three_header_textView)
        statFourHeader = findViewById(R.id.stat_four_header_textView)

        statOneTextView = findViewById(R.id.stat_one_textView)
        statTwoTextView = findViewById(R.id.stat_two_textView)
        statThreeTextView = findViewById(R.id.stat_three_textView)
        statFourTextView = findViewById(R.id.stat_four_textView)

        existenceTimerTextView = findViewById(R.id.existence_timer_textView)
        startStopButton = findViewById(R.id.start_stop_button)
        eventTextView = findViewById(R.id.event_textView)
        statWarningTextView = findViewById(R.id.zero_stat_warning)

        Stats.setInitialRandomValuesForStats()
        Stats.iterateThroughStatsToAddOrSubtractRemainder()

        setDefaultStatHeadersOnTextViews()
        setValuesToStatsTextViews()

        startStopButton.setOnClickListener {
            setRandomMillisValueForEventTrigger()
            startTimeIterationCoRoutine()
            setValuesToStatsTextViews()
        }
    }

    private fun setRandomMillisValueForEventTrigger() {
        val randomStop = (3000..5000).random()
        randomMillisValueForEvent = randomStop.toLong()
    }

    fun toggleStartStopButton(enabled: Boolean) {
        startStopButton.isClickable = enabled
    }

    //Only executes once so changing boolean doesn't stop it.
    private fun startTimeIterationCoRoutine() {
        toggleStartStopButton(false)

        job = GlobalScope.launch(Dispatchers.Main) {
            while (temporaryEventTime <= randomMillisValueForEvent) {
                spawnTimeIteration()
            }
            //Post-while code.
            rollEvent()
            resetTemporaryEventTime()
            resetRandomMillisValueForEventTime()
            resetButtonClickability()
            Log.i("testEvent", "event is $eventString")
        }
    }

    private suspend fun spawnTimeIteration() {
        val startTime = System.currentTimeMillis()
        delay(50)

        var timeToAdd = System.currentTimeMillis() - startTime

        temporaryEventTime += timeToAdd
        totalSpawnTimeInMilliseconds += timeToAdd
        existenceTimerTextView.text = DecimalToStringConversions.timeWithMillis(totalSpawnTimeInMilliseconds)
    }

    private fun resetRandomMillisValueForEventTime() {
        randomMillisValueForEvent = 0
    }

    private fun resetTemporaryEventTime() {
        temporaryEventTime = 0
    }

    private fun resetButtonClickability() {
        toggleStartStopButton(true)
    }

    private fun rollEvent() {
        Events.aggregatedRoll()

        getAndAssignEventString()
        setEventStringToTextView()

        getAndAssignEventValue()
        setValuesToStatsVariables()
        setValuesToStatsTextViews()

        if (statHasReachedZeroCheck()) statIsBelowZeroLogic()
        setValuesToStatsTextViewsWithAppend()

    }

    private fun getAndAssignEventString() {
        eventString = Events.eventString
    }

    private fun setEventStringToTextView() {
        eventTextView.text = eventString
    }

    private fun getAndAssignEventValue() {
        if (Events.rolledBadOrGood == BAD_ROLL) eventValue = -Events.eventValue else eventValue = Events.eventValue
    }

    private fun setValuesToStatsVariables() {
        if (Events.rolledEvent == JOB_EVENT) Stats.statOneValue += eventValue
        if (Events.rolledEvent == FINANCES_EVENT) Stats.statTwoValue += eventValue
        if (Events.rolledEvent == FAMILY_EVENT) Stats.statThreeValue += eventValue
        if (Events.rolledEvent == SOCIAL_EVENT) Stats.statFourValue += eventValue
    }

    private fun setValuesToStatsTextViews() {
        statOneTextView.text = Stats.statOneValue.toString()
        statTwoTextView.text = Stats.statTwoValue.toString()
        statThreeTextView.text = Stats.statThreeValue.toString()
        statFourTextView.text = Stats.statFourValue.toString()
    }

    private fun setValuesToStatsTextViewsWithAppend() {
        var valueString = ""
        if (eventValue > 0) valueString = "(+$eventValue)" else valueString = "($eventValue)"

        when (Events.rolledEvent) {
            JOB_EVENT -> statOneTextView.text = getString(R.string.stats_with_change, Stats.statOneValue.toString(), valueString)
            FINANCES_EVENT -> statTwoTextView.text =getString(R.string.stats_with_change, Stats.statTwoValue.toString(), valueString)
            FAMILY_EVENT -> statThreeTextView.text = getString(R.string.stats_with_change, Stats.statThreeValue.toString(), valueString)
            SOCIAL_EVENT -> statFourTextView.text = getString(R.string.stats_with_change, Stats.statFourValue.toString(), valueString)
        }
    }

    private fun statHasReachedZeroCheck() : Boolean {
        return (Stats.statOneValue <= 0 || Stats.statTwoValue <= 0 || Stats.statThreeValue <= 0 || Stats.statFourValue <= 0)
    }

    private fun statIsBelowZeroLogic() {
        var endGameString = getString(R.string.end_game_string, "$")
        if (Stats.statOneValue <= 0) {
            if (Stats.statOneCritical) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsOneString()), getString(R.string.end_game_append_one))
            } else {
                Stats.statOneValue = 0
                Stats.statOneCritical = true
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsOneString()))
            }

            statOneHeader.setTextColor(Color.RED)
        }
        if (Stats.statTwoValue <= 0) {
            if (Stats.statTwoCritical) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsTwoString()), getString(R.string.end_game_append_two))
            } else {
                Stats.statTwoValue = 0
                Stats.statTwoCritical = true
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsTwoString()))
            }

            statTwoHeader.setTextColor(Color.RED)
        }
        if (Stats.statThreeValue <= 0) {
            if (Stats.statThreeCritical) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsThreeString()), getString(R.string.end_game_append_three))
            } else {
                Stats.statThreeValue = 0
                Stats.statThreeCritical = true
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsThreeString()))
            }

            statThreeHeader.setTextColor(Color.RED)
        }
        if (Stats.statFourValue <= 0) {
            if (Stats.statFourCritical) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsFourString()), getString(R.string.end_game_append_four))
            } else {
                Stats.statFourValue = 0
                Stats.statFourCritical = true
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsFourString()))
            }

            statFourHeader.setTextColor(Color.RED)
        }
    }

    private fun zeroStatHasGainedBackValue() {

    }

    private fun setDefaultStatHeadersOnTextViews(nameOne: String = getString(R.string.stat_one), nameTwo: String = getString(R.string.stat_two), nameThree: String = getString(R.string.stat_three), nameFour: String = getString(R.string.stat_four)) {
        statOneHeader.text = nameOne
        statTwoHeader.text = nameTwo
        statThreeHeader.text = nameThree
        statFourHeader.text = nameFour
    }
}