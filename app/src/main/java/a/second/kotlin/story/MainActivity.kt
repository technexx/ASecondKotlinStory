
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

    lateinit var existenceTimerTextView : TextView
    lateinit var startStopButton : TextView

    var job: Job = Job()

    private var totalSpawnTimeInMilliseconds : Long = 0
    private var randomMillisValueForEvent : Long = 0
    private var routineIsActive: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                        Log.i("testRoutine", "checking!")
                        spawnTimeIteration()
                    }
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
        existenceTimerTextView.setText((totalSpawnTimeInMilliseconds).toString())

        triggerEvent(timeToAdd)
    }

    private fun triggerEvent(timeIterated: Long) {
        if (timeIterated >= randomMillisValueForEvent) {
            routineIsActive = false;
            randomMillisValueForEvent = 0;
            startTimeIterationCoRoutine()
        }
    }
}