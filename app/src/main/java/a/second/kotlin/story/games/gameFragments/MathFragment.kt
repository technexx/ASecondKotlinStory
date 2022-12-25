package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.MathProblems
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MathFragment : Fragment() {

    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()
    var MathProblems : MathProblems = MathProblems()

    lateinit var rootView : View
    lateinit var problemTextView : TextView
    lateinit var answerEditText : EditText
    lateinit var submitButton : Button
    lateinit var answerStateTextView : TextView

    var inputOne : Int = 0
    var inputTwo : Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_math_layout, container, false)

        problemTextView = rootView.findViewById(R.id.problem_textView)
        answerEditText = rootView.findViewById(R.id.math_answer_editText)
        submitButton = rootView.findViewById(R.id.submit_math_button)
        answerStateTextView = rootView.findViewById(R.id.state_of_answer_textView)

        setTypeOfProblem()
        setInputsToTextView()
        setSubmitButtonListener()

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
            sendGameBeingPlayedToViewModel()

            //Observed data (i.e. State of Answer) must be called after all intended values to change are set.
            setStateOfAnswerTextView()
            sendAnswerStateToViewModel()
        }
    }
    private fun setStateOfAnswerTextView() {
        if (doesUserInputMatchAnswer()) answerStateTextView.text = getString(R.string.math_problem_correct) else answerStateTextView.text = getString(R.string.math_problem_incorrect)
    }

    private fun doesUserInputMatchAnswer() : Boolean {
        return answerEditText.text.toString() == MathProblems.answer.toString()
    }

    private fun sendAnswerStateToViewModel() {
        gamesViewModel.setIsAnswerCorrect(doesUserInputMatchAnswer())
    }

    private fun sendGameBeingPlayedToViewModel() {
        gamesViewModel.setWhichGameIsBeingPlayed("Math")
    }
}