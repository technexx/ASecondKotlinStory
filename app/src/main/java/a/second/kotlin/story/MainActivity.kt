
package a.second.kotlin.story

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.DispatcherState
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import org.w3c.dom.Text


//Todo: Use ViewModel and Coroutines.
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

    private var totalSpawnTimeInMilliseconds : Long = 0
    private var randomMillisValueForEvent : Long = 0
    private var routineIsActive: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statOneHeader = findViewById(R.id.stat_one_header_textView)
        statTwoHeader = findViewById(R.id.stat_two_header_textView)
        statThreeHeader = findViewById(R.id.stat_three_header_textView)
        statFourHeader = findViewById(R.id.stat_four_header_textView)

        statOneTextView = findViewById(R.id.stat_one_textView)
        statTwoTextView = findViewById(R.id.stat_two_textView)
        statThreeTextView = findViewById(R.id.stat_three_textView)
        statFourTextView = findViewById(R.id.stat_four_textView)

        setDefaultStatHeadersOnTextViews(getString(R.string.stat_one), getString(R.string.stat_two), getString(R.string.stat_three), getString(R.string.stat_four))
        setDefaultStatValuesOnTextViews(0)

        existenceTimerTextView = findViewById(R.id.existence_timer_textView)
        startStopButton = findViewById(R.id.start_stop_button)

        fun toggleStartStopButton(enabled: Boolean) {
            if (enabled) {
                startStopButton.isEnabled = true;
                startStopButton.isClickable = true;
            } else {
                startStopButton.isEnabled = false;
                startStopButton.isClickable = false;
            }
        }

        startStopButton.setOnClickListener {
            routineIsActive = true
            setRandomMillisValueForEventTrigger()
            startTimeIterationCoRoutine()
        }
    }

    private fun setRandomMillisValueForEventTrigger() {
        val randomStop = (5000..8000).random()
        randomMillisValueForEvent = randomStop.toLong()
    }

    //Only executes once so changing boolean doesn't stop it.
    private fun startTimeIterationCoRoutine() {
        if (routineIsActive) {
            job = GlobalScope.launch(Dispatchers.Main) {
                if (routineIsActive) {
                    while (totalSpawnTimeInMilliseconds <= randomMillisValueForEvent) {
                        spawnTimeIteration()
                    }
                    //Post-while code.
                }
            }
        } else {
            job.cancel()
        }
    }

    private suspend fun spawnTimeIteration() {
        val startTime = System.currentTimeMillis()
        delay(50)

        var timeToAdd = System.currentTimeMillis() - startTime
        totalSpawnTimeInMilliseconds += timeToAdd
        existenceTimerTextView.text = (totalSpawnTimeInMilliseconds).toString()
    }

    private fun setDefaultStatHeadersOnTextViews(nameOne: String, nameTwo: String, nameThree: String, nameFour: String) {
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

    private fun triggerEvent(timeIterated: Long) {

    }
}