package a.second.kotlin.story

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var startButton : Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.start_game_button)

        startButton.setOnClickListener {
            startActivity(intentToBeginGame())
        }
    }

    private fun intentToBeginGame() : Intent {
        return Intent(this, GameActivity::class.java)
    }
}