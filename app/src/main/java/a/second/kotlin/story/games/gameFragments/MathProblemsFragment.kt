package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.MathProblems
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MathProblemsFragment : Fragment() {

    //Todo: Should be a succession of problems.
    lateinit var rootView : View
    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()
    var MathProblems : MathProblems = MathProblems()

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var objectAnimator : ObjectAnimator
    private var progressValue = 0

    lateinit var problemTextView : TextView
    lateinit var answerEditText : EditText
    lateinit var submitButton : Button
    lateinit var mathAnswerStateTextView : TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_math_problems_layout, container, false)

        problemTextView = rootView.findViewById(R.id.problem_textView)
        answerEditText = rootView.findViewById(R.id.math_answer_editText)
        submitButton = rootView.findViewById(R.id.submit_math_button)
        mathAnswerStateTextView = rootView.findViewById(R.id.math_state_of_answer_textView)

        setTypeOfProblem()
        setInputsToTextView()
        setSubmitButtonListener()
        sendGameBeingPlayedToViewModel()

        instantiateProgressBar()
        instantiateObjectAnimator()
        startObjectAnimator()

        return rootView
    }

    private fun setTypeOfProblem() {
        when (problemRoll()) {
            0 -> MathProblems.assignAdditionInputs()
            1 -> MathProblems.assignSubtractionInputs()
            2 -> MathProblems.assignMultiplicationInputs()
            3 -> MathProblems.assignDivisionInputs()
        }
    }

    private fun problemRoll() : Int {
        return (0..3).random()
    }

    private fun setInputsToTextView() {
        problemTextView.text = MathProblems.createProblemString()
    }

    private fun setSubmitButtonListener() {
        submitButton.setOnClickListener {
            //Observed data (i.e. State of Answer) must be called after all intended values to change are set.
            setStateOfAnswerTextView()
            sendAnswerStateToViewModel()
        }
    }

    private fun instantiateProgressBar() {
        timerProgressBar = rootView.findViewById(R.id.sums_cards_timer_progress_bar)
        progressValue = 1000
        timerProgressBar.max = progressValue
    }

    private fun instantiateObjectAnimator() {
        objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", progressValue, 0)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 30000
    }

    private fun startObjectAnimator() {
        objectAnimator.start()
    }

    private fun pauseObjectAnimator() {
        objectAnimator.pause()
    }

    //Cancelling animator occurs when all sums are matched before animation duration ends.
    private fun setEndOfObjectAnimatorListener() {
        objectAnimator.doOnEnd {

        }
    }

    private fun setStateOfAnswerTextView() {
        if (doesUserInputMatchAnswer()) mathAnswerStateTextView.text = getString(R.string.math_problem_correct) else mathAnswerStateTextView.text = getString(R.string.math_problem_incorrect)
    }

    private fun doesUserInputMatchAnswer() : Boolean {
        return answerEditText.text.toString() == MathProblems.answer.toString()
    }

    private fun sendAnswerStateToViewModel() {
        gamesViewModel.setIsAnswerCorrect(doesUserInputMatchAnswer())
    }

    private fun sendGameBeingPlayedToViewModel() {
        gamesViewModel.gameBeingPlayed = ("Math")
    }
}