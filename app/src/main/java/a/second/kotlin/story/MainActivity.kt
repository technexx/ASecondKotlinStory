package a.second.kotlin.story

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var DecimalToStringConversions = DecimalToStringConversions()
    private lateinit var startButton : Button
    private lateinit var highScoreTextView : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.start_game_button)
        highScoreTextView = findViewById(R.id.high_score_textView)

        highScoreTextView.text = getString(R.string.two_item_concat, getString(R.string.high_score), DecimalToStringConversions.timeWithMillis(highScoreLong()))

        startButton.setOnClickListener {
            startActivity(intentToBeginGame())
        }
    }

    private fun intentToBeginGame() : Intent {
        return Intent(this, GameActivity::class.java)
    }

    private fun highScoreLong() : Long {
        val sharedPref = getSharedPreferences("sharedPref", 0)
        return sharedPref.getLong("highScore", 0)
    }
}