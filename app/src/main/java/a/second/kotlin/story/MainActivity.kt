
package a.second.kotlin.story

import a.second.kotlin.story.gameFragments.HangmanFragment
import a.second.kotlin.story.gameFragments.MatchingFragment
import a.second.kotlin.story.gameFragments.MathProblemsFragment
import a.second.kotlin.story.gameFragments.MathSumsFragment
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.service.autofill.FieldClassification.Match
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.launch

//Todo: Remember to SUSPEND and not BLOCK if using UI thread. runBlocking is a default CoroutineScope.

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    val gamesViewModel : ItemViewModel.GamesViewModel by viewModels()

    var job: Job = Job()
    private lateinit var Events : Events
    private lateinit var Stats : Stats
    private lateinit var DecimalToStringConversions: DecimalToStringConversions

    private lateinit var SumsFragment : MathSumsFragment
    private lateinit var MathProblemsFragment : MathProblemsFragment
    private lateinit var HangmanFragment : HangmanFragment
    private lateinit var MatchingFragment : MatchingFragment

    private lateinit var statOneHeader : TextView
    private lateinit var statOneTextView : TextView
    private lateinit var statTwoHeader : TextView
    private lateinit var statTwoTextView : TextView
    private lateinit var statThreeHeader : TextView
    private lateinit var statThreeTextView : TextView
    private lateinit var statFourHeader : TextView
    private lateinit var statFourTextView : TextView

    private lateinit var existenceTimerTextView : TextView
    private lateinit var startStopButton : TextView
    private lateinit var eventTextView : TextView
    private lateinit var statWarningTextView : TextView

    private var totalSpawnTimeInMilliseconds : Long = 0
    private var temporaryEventTime : Long = 0
    private var randomMillisDelayForEvent : Long = 0

    private var eventString : String = ""

    private var JOB_EVENT = 0
    private var FINANCES_EVENT = 1
    private var FAMILY_EVENT = 2
    private var SOCIAL_EVENT = 3

    private var BAD_ROLL = 0
    private var GOOD_ROLL = 1

    private var previousFragmentId = 1

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

        existenceTimerTextView.text = getString(R.string.two_item_concat, getString(R.string.time_since_spawn), "0:00")

        attachInitialGameFragment()
        setDefaultStatHeadersOnTextViews()
        setValuesToStatsTextViews()
        setViewModelObserver()

        startStopButton.setOnClickListener {
            setRandomMillisDelayForEventTrigger()
            startTimeIterationCoRoutine()
            setValuesToStatsTextViews()
        }
    }

    private fun setViewModelObserver() {
        gamesViewModel.mutableCorrectAnswerBoolean.observe(this, Observer {

            val answerState = gamesViewModel.getIsAnswerCorrect()
            val gameBeingPlayed = gamesViewModel.getWhichIsGameBeingPlayed()
            var statChangeValue = statChangeValueForGame()

            if (answerState != null) {
                //If answer is negative, stat change value becomes negative as well.
                if (!answerState) statChangeValue = -statChangeValue
            }

            changeStatValueFromGame(gameBeingPlayed, statChangeValue)
            changeStatTextViewFromGame(gameBeingPlayed, statChangeValue)

            Handler().postDelayed( {
                switchFragmentForNextGame(getFragmentBasedOnRoll())
                Log.i("testFrag", "switching fragment!")
            }, 3000)

        })
    }

    private fun attachInitialGameFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.game_frame_layout, getFragmentBasedOnRoll())
            .commit()
    }

    private fun getFragmentBasedOnRoll() : Fragment {
        var fragmentToReturn : Fragment? = null

        var roll = (0..3).random()

        while (roll == previousFragmentId) { roll = (0..3).random() }
        previousFragmentId = roll

        if (roll == 0) fragmentToReturn = MathSumsFragment()
        if (roll == 1) fragmentToReturn = MathProblemsFragment()
        if (roll == 2) fragmentToReturn = HangmanFragment()
        if (roll == 3) fragmentToReturn = MatchingFragment()

        return fragmentToReturn!!
    }

    private fun switchFragmentForNextGame(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in_animation, R.anim.fade_out_animation)
            .replace(R.id.game_frame_layout, fragment)
            .commit()
    }

    private fun changeStatValueFromGame(game: String?, value: Int) {
        if (game == "Sums") Stats.statOneValue += value
        if (game == "MathProblems") Stats.statTwoValue += value
        if (game == "Matching") Stats.statThreeValue += value
        if (game == "Hangman") Stats.statFourValue += value
    }

    private fun changeStatTextViewFromGame(game: String?, value: Int) {
        if (game == "Sums") statOneTextView.text = getString(R.string.two_item_concat, Stats.statOneValue.toString(), intToPlusOrMinusString(value))
        if (game == "MathProblems") statTwoTextView.text = getString(R.string.two_item_concat, Stats.statTwoValue.toString(), intToPlusOrMinusString(value))
        if (game == "Matching") statThreeTextView.text = getString(R.string.two_item_concat, Stats.statThreeValue.toString(), intToPlusOrMinusString(value))
        if (game == "Hangman") statFourTextView.text = getString(R.string.two_item_concat, Stats.statFourValue.toString(), intToPlusOrMinusString(value))
    }

    private fun statChangeValueForGame() : Int {
        return (3..6).random()
    }

    private fun setRandomMillisDelayForEventTrigger() {
        val randomStop = (3000..5000).random()
        randomMillisDelayForEvent = randomStop.toLong()
    }

    private fun toggleStartStopButton(enabled: Boolean) { startStopButton.isClickable = enabled }

    //Only executes once so changing boolean doesn't stop it.
    private fun startTimeIterationCoRoutine() {
        toggleStartStopButton(false)

        job = GlobalScope.launch(Dispatchers.Main) {
            while (temporaryEventTime <= randomMillisDelayForEvent) {
                spawnTimeIteration()
            }
            //Post-while code.
            rollEvent()
            resetTemporaryEventTime()
            setRandomMillisDelayForEventTrigger()
//            resetButtonClickability()

            startTimeIterationCoRoutine()
        }
    }

    private suspend fun spawnTimeIteration() {
        val startTime = System.currentTimeMillis()
        delay(50)

        val timeToAdd = System.currentTimeMillis() - startTime

        temporaryEventTime += timeToAdd
        totalSpawnTimeInMilliseconds += timeToAdd
        existenceTimerTextView.text = getString(R.string.two_item_concat, getString(R.string.time_since_spawn), DecimalToStringConversions.timeWithMillis(totalSpawnTimeInMilliseconds))
    }

    private fun resetRandomMillisDelayForEventTime() { randomMillisDelayForEvent = 0 }

    private fun resetTemporaryEventTime() { temporaryEventTime = 0 }

    private fun resetButtonClickability() { toggleStartStopButton(true) }

    private fun rollEvent() {
        Events.aggregatedRoll()

        getAndAssignEventString()
        setEventStringToTextView()

        setValuesToStatsVariables()
        setValuesToStatsTextViews()

        changeStatValueFromEvent()

        setStatTextViewToRedIfAtZeroAndBlackIfNot()
        checkAffectedStatAgainstZeroSum()

        blankOutCriticalScoreTextView()
    }

    private fun getAndAssignEventString() {
        eventString = Events.eventString
    }

    private fun setEventStringToTextView() {
        eventTextView.text = eventString
    }

    private fun setValuesToStatsVariables() {
        when (Events.rolledEvent) {
            JOB_EVENT -> Stats.statOneValue += Events.eventValue
            FINANCES_EVENT -> Stats.statTwoValue += Events.eventValue
            FAMILY_EVENT -> Stats.statThreeValue += Events.eventValue
            SOCIAL_EVENT -> Stats.statFourValue += Events.eventValue
        }
    }

    private fun setValuesToStatsTextViews() {
        statOneTextView.text = Stats.statOneValue.toString()
        statTwoTextView.text = Stats.statTwoValue.toString()
        statThreeTextView.text = Stats.statThreeValue.toString()
        statFourTextView.text = Stats.statFourValue.toString()
    }

    private fun blankOutCriticalScoreTextView() {
        statWarningTextView.text = ""
    }

    private fun changeStatValueFromEvent() {
        val valueModifier = Events.eventValue
        Log.i("testEvent", "valueMod is $valueModifier")
        val valueString = intToPlusOrMinusString(valueModifier)

        when (Events.rolledEvent) {
            JOB_EVENT -> statOneTextView.text = getString(R.string.two_item_concat, Stats.statOneValue.toString(), valueString)
            FINANCES_EVENT -> statTwoTextView.text =getString(R.string.two_item_concat, Stats.statTwoValue.toString(), valueString)
            FAMILY_EVENT -> statThreeTextView.text = getString(R.string.two_item_concat, Stats.statThreeValue.toString(), valueString)
            SOCIAL_EVENT -> statFourTextView.text = getString(R.string.two_item_concat, Stats.statFourValue.toString(), valueString)
        }
    }

    private fun intToPlusOrMinusString(intValue: Int) : String {
        var valueToReturn = "0"
        if (intValue > 0) valueToReturn = "(+$intValue)" else valueToReturn = "($intValue)"
        return valueToReturn
    }

    private fun setStatTextViewToRedIfAtZeroAndBlackIfNot() {
        if (Stats.statOneValue <= 0) statOneHeader.setTextColor(Color.RED) else statOneTextView.setTextColor(Color.BLACK)
        if (Stats.statTwoValue <= 0) statTwoHeader.setTextColor(Color.RED) else statTwoTextView.setTextColor(Color.BLACK)
        if (Stats.statThreeValue <= 0) statThreeHeader.setTextColor(Color.RED) else statThreeTextView.setTextColor(Color.BLACK)
        if (Stats.statFourValue <= 0) statFourHeader.setTextColor(Color.RED) else statFourTextView.setTextColor(Color.BLACK)
    }

    private fun checkAffectedStatAgainstZeroSum() {
        when (Events.rolledEvent) {
            JOB_EVENT -> statOneZeroCheckAndLogic()
            FINANCES_EVENT -> statTwoZeroCheckAndLogic()
            FAMILY_EVENT -> statThreeZeroCheckAndLogic()
            SOCIAL_EVENT -> statFourZeroCheckAndLogic()
        }
    }

    private fun statOneZeroCheckAndLogic() {
        if (Stats.statOneCritical) {
            if (Stats.statOneValue <= 0) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsOneString()), getString(R.string.end_game_append_one))
            }  else {
                Stats.statOneCritical = false
            }
        } else {
            if (Stats.statOneValue <= 0) {
                Stats.statOneValue = 0
                Stats.statOneCritical = true
                statOneTextView.text = "0"
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsOneString()))
            }
        }
    }

    private fun statTwoZeroCheckAndLogic() {
        if (Stats.statTwoCritical) {
            if (Stats.statTwoValue <= 0) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsTwoString()), getString(R.string.end_game_append_two))
            }  else {
                Stats.statTwoCritical = false
            }
        } else {
            if (Stats.statTwoValue <= 0) {
                Stats.statTwoValue = 0
                Stats.statTwoCritical = true
                statTwoTextView.text = "0"
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsTwoString()))
            }
        }
    }

    private fun statThreeZeroCheckAndLogic() {
        if (Stats.statThreeCritical) {
            if (Stats.statThreeValue <= 0) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsThreeString()), getString(R.string.end_game_append_three))
            }  else {
                Stats.statThreeCritical = false
            }
        } else {
            if (Stats.statThreeValue <= 0) {
                Stats.statThreeValue = 0
                Stats.statThreeCritical = true
                statThreeTextView.text = "0"
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsThreeString()))
            }
        }
    }

    private fun statFourZeroCheckAndLogic() {
        if (Stats.statFourCritical) {
            if (Stats.statFourValue <= 0) {
                statWarningTextView.text = getString(R.string.two_line_concat, getString(R.string.end_game_string, Stats.statsFourString()), getString(R.string.end_game_append_four))
            }  else {
                Stats.statFourCritical = false
            }
        } else {
            if (Stats.statFourValue <= 0) {
                Stats.statFourValue = 0
                Stats.statFourCritical = true
                statFourTextView.text = "0"
                statWarningTextView.text = (getString(R.string.zero_stat_warning, Stats.statsFourString()))
            }
        }
    }

    private fun setDefaultStatHeadersOnTextViews(nameOne: String = getString(R.string.stat_one), nameTwo: String = getString(R.string.stat_two), nameThree: String = getString(R.string.stat_three), nameFour: String = getString(R.string.stat_four)) {
        statOneHeader.text = nameOne
        statTwoHeader.text = nameTwo
        statThreeHeader.text = nameThree
        statFourHeader.text = nameFour
    }
}