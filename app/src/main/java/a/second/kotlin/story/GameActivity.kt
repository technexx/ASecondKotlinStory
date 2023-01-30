package a.second.kotlin.story

import a.second.kotlin.story.gameFragments.HangmanFragment
import a.second.kotlin.story.gameFragments.MatchingFragment
import a.second.kotlin.story.gameFragments.MathProblemsFragment
import a.second.kotlin.story.gameFragments.MathSumsFragment
import android.annotation.SuppressLint
import android.content.SharedPreferences
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
class GameActivity : AppCompatActivity() {
    var mathSumsFragment : MathSumsFragment = MathSumsFragment()
    var mathProblemsFragment : MathProblemsFragment = MathProblemsFragment()
    var hangmanFragment : HangmanFragment = HangmanFragment()
    var matchingFragment : MatchingFragment = MatchingFragment()

    val gamesViewModel : ItemViewModel.GamesViewModel by viewModels()

    private var job: Job = Job()
    private lateinit var Events : Events
    private lateinit var Stats : Stats
    private val eventTimes = EventTimes()
    private lateinit var DecimalToStringConversions: DecimalToStringConversions

    var handler : Handler = Handler()
    var eventTimerRunnable : Runnable = Runnable { }

    private lateinit var sanityHeaderTextView: TextView
    private lateinit var sanityTextView: TextView
    private lateinit var statOneHeader : TextView
    private lateinit var statOneTextView : TextView
    private lateinit var statTwoHeader : TextView
    private lateinit var statTwoTextView : TextView
    private lateinit var statThreeHeader : TextView
    private lateinit var statThreeTextView : TextView
    private lateinit var statFourHeader : TextView
    private lateinit var statFourTextView : TextView

    private lateinit var existenceTimerTextView : TextView
    private lateinit var eventTextView : TextView
    private lateinit var statWarningTextView : TextView

    private var randomMillisDelayForEvent : Long = 0

    private var eventString : String = ""

    private var JOB_EVENT = 0
    private var FINANCES_EVENT = 1
    private var FAMILY_EVENT = 2
    private var SOCIAL_EVENT = 3
    private var LAST_EVENT = -1

    private var lastLoadedFragmentId = -1

    private var gameIsActive = true

    override fun onBackPressed() {
        super.onBackPressed()
        removeCurrentFragment()
    }

    private fun removeCurrentFragment() {
        supportFragmentManager.beginTransaction()
            .remove(getLastLoadedFragment())
            .commit()
    }

    private fun saveHighScore() {
        val sharedPref = getSharedPreferences("sharedPref", 0)
        val sharedPrefEditor = sharedPref.edit()
        val currentHighScore = sharedPref.getLong("highScore", 0)

        if (eventTimes.totalSpawnTimeInMilliseconds > currentHighScore) {
            sharedPrefEditor.putLong("highScore", eventTimes.totalSpawnTimeInMilliseconds)
            sharedPrefEditor.apply()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        Events = Events(applicationContext)
        Stats = Stats(applicationContext)
        DecimalToStringConversions = DecimalToStringConversions()

        sanityHeaderTextView = findViewById(R.id.sanity_header_textView)
        sanityTextView = findViewById(R.id.sanity_textView)

        statOneHeader = findViewById(R.id.stat_one_header_textView)
        statTwoHeader = findViewById(R.id.stat_two_header_textView)
        statThreeHeader = findViewById(R.id.stat_three_header_textView)
        statFourHeader = findViewById(R.id.stat_four_header_textView)

        statOneTextView = findViewById(R.id.stat_one_textView)
        statTwoTextView = findViewById(R.id.stat_two_textView)
        statThreeTextView = findViewById(R.id.stat_three_textView)
        statFourTextView = findViewById(R.id.stat_four_textView)

        existenceTimerTextView = findViewById(R.id.existence_timer_textView)
        eventTextView = findViewById(R.id.event_textView)
        statWarningTextView = findViewById(R.id.zero_stat_warning)

        Stats.setInitialRandomValuesForStats()
        Stats.iterateThroughStatsToAddOrSubtractRemainder()

        existenceTimerTextView.text = getString(R.string.two_item_concat, getString(R.string.time_since_spawn), "0:00")

        attachInitialGameFragment()
        setDefaultStatHeadersOnTextViews()
        setValuesToStatsTextViews()
        setViewModelObserver()

        instantiateEventTimerRunnable()

        newGameFunctions()

        setSanityTextView()
    }

    private fun newGameFunctions() {
        setRandomMillisDelayForEventTrigger()
        setValuesToStatsTextViews()

        setStableTimeForEventTimer()
        startTimeIterationCoRoutine()
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
            clearStatModificationTextViews()
            changeStatTextViewFromGame(gameBeingPlayed, statChangeValue)

            if (hasAStatReachedNegativeValue()) {
                gameIsOverFunctions()
            } else {
                handler.postDelayed( {
                    if (gameIsActive) switchFragmentForNextGame(getFragmentBasedOnRoll())
                }, 2000)
            }
        })

        gamesViewModel.mutableTypeOfEventTriggered.observe(this) {
            if (hasAStatReachedNegativeValue()) { gameIsOverFunctions() }
        }
    }

    private fun gameIsOverFunctions() {
        cancelEventTimerCoroutine()
        cancelEventTimerRunnable()
        pauseObjectAnimatorOfCurrentFragment()
        disableFragmentClicks()
        gameIsActive = false

        saveHighScore()
    }

    private fun hasAStatReachedNegativeValue() : Boolean {
        return Stats.statOneValue < 0 || Stats.statTwoValue < 0 || Stats.statThreeValue < 0 || Stats.statFourValue < 0
    }

    private fun disableFragmentClicks() {
        when (lastLoadedFragmentId) {
            0 -> mathSumsFragment.disableAdapterClicks()
            2 -> hangmanFragment.disableAdapterClicks()
            3 -> matchingFragment.disableAdapterClicks()
        }
    }

    private fun attachInitialGameFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.game_frame_layout, getFragmentBasedOnRoll())
            .commit()
    }

    private fun getFragmentBasedOnRoll() : Fragment {
        var fragmentToReturn : Fragment? = null

        var roll = (0..3).random()
        while (roll == lastLoadedFragmentId) { roll = (0..3).random() }
        lastLoadedFragmentId = roll

        reInstantiateFragment(roll)

        if (roll == 0) fragmentToReturn = mathSumsFragment
        if (roll == 1) fragmentToReturn = mathProblemsFragment
        if (roll == 2) fragmentToReturn = hangmanFragment
        if (roll == 3) fragmentToReturn = matchingFragment

        return fragmentToReturn!!
    }

    private fun reInstantiateFragment(fragmentRoll : Int) {
        when (fragmentRoll)  {
            0 -> mathSumsFragment = MathSumsFragment()
            1 -> mathProblemsFragment = MathProblemsFragment()
            2 -> hangmanFragment = HangmanFragment()
            3 -> matchingFragment = MatchingFragment()
        }
    }

    private fun getLastLoadedFragment() : Fragment {
        var fragmentToReturn = Fragment()

        when (lastLoadedFragmentId) {
            0 -> fragmentToReturn = mathSumsFragment
            1 -> fragmentToReturn = mathProblemsFragment
            2 -> fragmentToReturn = hangmanFragment
            3 -> fragmentToReturn = matchingFragment
        }

        return fragmentToReturn
    }

    private fun switchFragmentForNextGame(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in_animation, R.anim.fade_out_animation)
            .replace(R.id.game_frame_layout, fragment)
            .commit()
    }

    private fun pauseObjectAnimatorOfCurrentFragment() {
        when (lastLoadedFragmentId) {
            0 -> mathSumsFragment.pauseObjectAnimator()
            1 -> mathProblemsFragment.pauseObjectAnimator()
            3 -> matchingFragment.pauseObjectAnimator()
        }
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
        return (8..15).random()
    }

    private suspend fun spawnTimeIteration() {
        delay(randomMillisDelayForEvent)
        rollEvent()
        setRandomMillisDelayForEventTrigger()
        startTimeIterationCoRoutine()
    }

    private fun rollEvent() {
        Events.aggregatedRoll()

        getAndAssignEventString()
        setEventStringToTextView()

        setLastTriggeredEventVariable()

        changeStatValuesFromEvent()

        removeCriticalStatWarning()
        checkAffectedStatAgainstZeroSum()
        sendEventStatChangeToLiveDataViewModel()

        setValuesToStatsTextViews()
        changeStatValueTextViewsFromEvent()
        setStatTextViewToRedIfAtZeroAndBlackIfNot()
    }

    private fun setRandomMillisDelayForEventTrigger() {
        val randomStop = (5000..10000).random()
        randomMillisDelayForEvent = randomStop.toLong()
    }

    private fun startTimeIterationCoRoutine() {
        job = GlobalScope.launch(Dispatchers.Main) {
            if (gameIsActive) {
                postEventTimerRunnable()
                spawnTimeIteration()
            }
        }
    }


    private fun instantiateEventTimerRunnable() {
        eventTimerRunnable = Runnable {
            eventTimes.currentTime = System.currentTimeMillis()
            eventTimes.totalSpawnTimeInMilliseconds = eventTimes.iteratedTime()

            existenceTimerTextView.text = getString(R.string.two_item_concat, getString(R.string.time_since_spawn), DecimalToStringConversions.timeWithMillis(eventTimes.totalSpawnTimeInMilliseconds))

            handler.postDelayed(eventTimerRunnable, 50)
        }
    }

    class EventTimes {
        var stableTime : Long = 0
        var currentTime : Long = 0
        var totalSpawnTimeInMilliseconds : Long = 0

        fun iteratedTime() : Long { return currentTime - stableTime}
    }

    private fun sendEventStatChangeToLiveDataViewModel() {
        gamesViewModel.setTypeOfEventTriggered(Events.rolledEvent)
    }

    private fun setStableTimeForEventTimer() { eventTimes.stableTime = System.currentTimeMillis() }

    private fun postEventTimerRunnable() { handler.post(eventTimerRunnable) }

    private fun cancelEventTimerRunnable() { handler.removeCallbacks(eventTimerRunnable) }

    private fun cancelEventTimerCoroutine() { job.cancel() }

    private fun getAndAssignEventString() {
        eventString = Events.eventString
    }

    private fun setEventStringToTextView() {
        eventTextView.text = eventString
    }

    private fun setLastTriggeredEventVariable() {
        when (Events.rolledEvent) {
            JOB_EVENT -> LAST_EVENT == JOB_EVENT
            FINANCES_EVENT -> LAST_EVENT == FINANCES_EVENT
            FAMILY_EVENT -> LAST_EVENT == FAMILY_EVENT
            SOCIAL_EVENT -> LAST_EVENT == SOCIAL_EVENT
        }
    }

    private fun changeStatValuesFromEvent() {
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

    private fun changeStatValueTextViewsFromEvent() {
        clearStatModificationTextViews()

        val valueModifier = Events.eventValue
        when (Events.rolledEvent) {
            JOB_EVENT -> statOneTextView.text = getString(R.string.two_item_concat, Stats.statOneValue.toString(), intToPlusOrMinusString(valueModifier))
            FINANCES_EVENT -> statTwoTextView.text = getString(R.string.two_item_concat, Stats.statTwoValue.toString(), intToPlusOrMinusString(valueModifier))
            FAMILY_EVENT -> statThreeTextView.text = getString(R.string.two_item_concat, Stats.statThreeValue.toString(), intToPlusOrMinusString(valueModifier))
            SOCIAL_EVENT -> statFourTextView.text = getString(R.string.two_item_concat, Stats.statFourValue.toString(), intToPlusOrMinusString(valueModifier))
        }
    }

    private fun intToPlusOrMinusString(intValue: Int) : String {
        var valueToReturn = "0"
        if (intValue > 0) valueToReturn = "(+$intValue)" else valueToReturn = "($intValue)"
        return valueToReturn
    }

    private fun clearStatModificationTextViews() {
        statOneTextView.text = getString(R.string.two_item_concat, Stats.statOneValue.toString(), "")
        statTwoTextView.text = getString(R.string.two_item_concat, Stats.statTwoValue.toString(), "")
        statThreeTextView.text = getString(R.string.two_item_concat, Stats.statThreeValue.toString(), "")
        statFourTextView.text = getString(R.string.two_item_concat, Stats.statFourValue.toString(), "")
    }

    private fun setStatTextViewToRedIfAtZeroAndBlackIfNot() {
        if (Stats.statOneValue <= 0) statOneHeader.setTextColor(Color.RED) else statOneHeader.setTextColor(Color.BLACK)
        if (Stats.statTwoValue <= 0) statTwoHeader.setTextColor(Color.RED) else statTwoHeader.setTextColor(Color.BLACK)
        if (Stats.statThreeValue <= 0) statThreeHeader.setTextColor(Color.RED) else statThreeHeader.setTextColor(Color.BLACK)
        if (Stats.statFourValue <= 0) statFourHeader.setTextColor(Color.RED) else statFourHeader.setTextColor(Color.BLACK)
    }

    private fun checkAffectedStatAgainstZeroSum() {
        when (Events.rolledEvent) {
            JOB_EVENT -> statOneZeroCheckAndLogic()
            FINANCES_EVENT -> statTwoZeroCheckAndLogic()
            FAMILY_EVENT -> statThreeZeroCheckAndLogic()
            SOCIAL_EVENT -> statFourZeroCheckAndLogic()
        }
    }

    private fun removeCriticalStatWarning() { statWarningTextView.text = "" }

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

//    private fun sanityCalculation() : Int {
//
//    }

    private fun setSanityTextView() { sanityTextView.text = "100" }
}