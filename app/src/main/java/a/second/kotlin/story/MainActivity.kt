
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

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var existenceTimerTextView : TextView = findViewById(R.id.existence_timer_textView)
        val startStopButton : Button = findViewById(R.id.start_stop_button)

        var totalSpawnTimeInMilliseconds : Long = 0
        var totalSpawnTimeInSeconds : Long = 0

        suspend fun testIteration() {
            val startTime = System.currentTimeMillis()

            delay(50)

            var timeToAdd = System.currentTimeMillis() - startTime
            totalSpawnTimeInMilliseconds += timeToAdd

            existenceTimerTextView.setText((totalSpawnTimeInMilliseconds).toString())

            Log.i("testRouting", "time is " + totalSpawnTimeInMilliseconds)
        }

        startStopButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                while (isActive)
                testIteration()
            }
        }
    }
}