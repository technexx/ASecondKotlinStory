package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.R
import a.second.kotlin.story.games.MathProblems
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text

class MathFragment : Fragment() {

    lateinit var rootView : View

    var MathProblems : MathProblems = MathProblems()

    lateinit var problemTextView: TextView

    var inputOne : Int = 0
    var inputTwo : Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.game_math_layout, container, false)

        problemTextView = rootView.findViewById(R.id.problem_textView)

        setTypeOfProblem()
        setInputsToTextView()

        return rootView
    }

    private fun problemRoll() : Int {
        return (0..3).random()
    }

    private fun setTypeOfProblem() {
        when (problemRoll()) {
            0 -> MathProblems.assignAdditionInputs()
            1 -> MathProblems.assignSubtractionInputs()
            2 -> MathProblems.assignMultiplicationInputs()
            3 -> MathProblems.assignDivisionInputs()
        }
    }

    private fun setInputsToTextView() {
        problemTextView.text = MathProblems.createProblemString()
    }
}