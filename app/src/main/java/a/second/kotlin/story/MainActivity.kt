
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var job: Job = Job()
        var routineIsActive : Boolean = false

        var existenceTimerTextView: TextView = findViewById(R.id.existence_timer_textView)
        val startStopButton: Button = findViewById(R.id.start_stop_button)

        var totalSpawnTimeInMilliseconds: Long = 0
        var totalSpawnTimeInSeconds: Long = 0

        fun toggleStartStopButton(enabled: Boolean) {
            if (enabled) {
                startStopButton.isEnabled = true;
                startStopButton.isClickable = true;
            } else {
                startStopButton.isEnabled = false;
                startStopButton.isClickable = false;
            }
        }

        suspend fun testIteration() {
            val startTime = System.currentTimeMillis()

            delay(50)

            var timeToAdd = System.currentTimeMillis() - startTime
            totalSpawnTimeInMilliseconds += timeToAdd

            existenceTimerTextView.setText((totalSpawnTimeInMilliseconds).toString())

//            Log.i("testRoutine", "value is " + totalSpawnTimeInMilliseconds)
        }

        fun startTimeIterationCoRoutine() {
            if (routineIsActive) {
                job = GlobalScope.launch(Dispatchers.Main) {
                    Log.i("testRoutine", "boolan in coroutine is " + routineIsActive)

                    if (routineIsActive) {
                        while (isActive)
                            testIteration()
                    }
                }
            } else {
                job.cancel()
                Log.i("testRoutine", "job cancelling!")
            }
        }

            startStopButton.setOnClickListener {
                routineIsActive = !routineIsActive

                Log.i("testRoutine", "boolan in button click is " + routineIsActive)

                startTimeIterationCoRoutine()
            }
        }
    }