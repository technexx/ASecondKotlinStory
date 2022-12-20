
package a.second.kotlin.story

import android.annotation.SuppressLint
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

    private var totalSpawnTimeInMilliseconds : Long = 0
    private var temporaryEventTime : Long = 0
    private var randomMillisValueForEvent : Long = 0

    lateinit var Events : Events
    lateinit var Stats : Stats
    private var eventString : String = ""
    private var eventValue : Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stats = Stats()
        Stats.setInitialRandomValuesForStats()
        Stats.iterateThroughStatsToAddOrSubtractRemainder()

        statOneHeader = findViewById(R.id.stat_one_header_textView)
        statTwoHeader = findViewById(R.id.stat_two_header_textView)
        statThreeHeader = findViewById(R.id.stat_three_header_textView)
        statFourHeader = findViewById(R.id.stat_four_header_textView)

        statOneTextView = findViewById(R.id.stat_one_textView)
        statTwoTextView = findViewById(R.id.stat_two_textView)
        statThreeTextView = findViewById(R.id.stat_three_textView)
        statFourTextView = findViewById(R.id.stat_four_textView)

        setDefaultStatHeadersOnTextViews()
        setDefaultStatValuesOnTextViews(0)

        existenceTimerTextView = findViewById(R.id.existence_timer_textView)
        startStopButton = findViewById(R.id.start_stop_button)
        eventTextView = findViewById(R.id.event_textView)

        startStopButton.setOnClickListener {
            setRandomMillisValueForEventTrigger()
            startTimeIterationCoRoutine()
        }
    }

    private fun setRandomMillisValueForEventTrigger() {
        val randomStop = (5000..8000).random()
        randomMillisValueForEvent = randomStop.toLong()
    }

    fun toggleStartStopButton(enabled: Boolean) {
        if (enabled) {
            startStopButton.isEnabled = true
            startStopButton.isClickable = true
        } else {
            startStopButton.isEnabled = false
            startStopButton.isClickable = false
        }
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
        existenceTimerTextView.text = (totalSpawnTimeInMilliseconds).toString()
    }

    private fun rollEvent() {
        Events.aggregatedRoll()
        getAndAssignEventString()
        getAndAssignEventValue()
        setEventStringToTextView()
    }

    private fun getAndAssignEventString() {
        eventString = Events.getRandomEventString()
    }

    private fun getAndAssignEventValue() {
        eventValue = Events.getRandomEventValue()
    }

    private fun setEventStringToTextView() {
        eventTextView.text = eventString
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

    private fun setDefaultStatHeadersOnTextViews(nameOne: String = getString(R.string.stat_one), nameTwo: String = getString(R.string.stat_two), nameThree: String = getString(R.string.stat_three), nameFour: String = getString(R.string.stat_four)) {
        statOneHeader.text = nameOne
        statTwoHeader.text = nameTwo
        statThreeHeader.text = nameThree
        statFourHeader.text = nameFour
    }

    private fun setDefaultStatValuesOnTextViews(value: Int) {
        statOneTextView.text = value.toString()
        statTwoTextView.text = value.toString()
        statThreeTextView.text = value.toString()
        statFourTextView.text = value.toString()
    }
}