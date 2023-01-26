package a.second.kotlin.story

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val gamesViewModel : ItemViewModel.GamesViewModel by viewModels()

    private var DecimalToStringConversions = DecimalToStringConversions()
    private lateinit var startButton : Button
    private lateinit var highScoreTextView : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.start_game_button)
        highScoreTextView = findViewById(R.id.high_score_textView)

        refreshHighScore()
        setViewModelObserver()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                refreshHighScore()
            }
        }

        startButton.setOnClickListener {
            startActivity(intentToBeginGame())
        }
    }

    private fun intentToBeginGame() : Intent {
        return Intent(this, GameActivity::class.java)
    }

    private fun refreshHighScore() {
        highScoreTextView.text = getString(R.string.two_item_concat, getString(R.string.high_score), DecimalToStringConversions.timeWithMillis(highScoreLong()))
    }

    private fun highScoreLong() : Long {
        val sharedPref = getSharedPreferences("sharedPref", 0)
        return sharedPref.getLong("highScore", 0)
    }

    private fun setViewModelObserver() {
        gamesViewModel.mutableHighScore.observe(this) {
            refreshHighScore()
            Log.i("testView", "main viewModel observer triggered!")
        }
    }
}