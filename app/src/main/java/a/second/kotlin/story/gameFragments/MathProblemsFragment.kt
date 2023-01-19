package a.second.kotlin.story.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.gameFragments.gameData.MathProblems
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL

class MathProblemsFragment : Fragment(), AnswerAdapter.AdapterData {

    override fun gameIsWon() {
        endOfGameFunctions(true)
        pauseObjectAnimator()
    }

    lateinit var rootView : View
    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()
    var MathProblemsData : MathProblems = MathProblems()

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var objectAnimator : ObjectAnimator
    private var progressValue = 0

    lateinit var problemTextView : TextView
    lateinit var answerEditText : EditText
    lateinit var submitButton : Button
    lateinit var mathAnswerStateTextView : TextView

    private lateinit var answerRecyclerView : RecyclerView
    private lateinit var answerAdapter : AnswerAdapter

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

        instantiateProgressBar()
        instantiateObjectAnimator()
        startObjectAnimator()
        setEndOfObjectAnimatorListener()

        instantiateRecyclerViewAndAdapter()

        return rootView
    }

    private fun setTypeOfProblem() {
        when (problemRoll()) {
            0 -> MathProblemsData.assignAdditionInputs()
            1 -> MathProblemsData.assignSubtractionInputs()
            2 -> MathProblemsData.assignMultiplicationInputs()
            3 -> MathProblemsData.assignDivisionInputs()
        }
    }

    private fun problemRoll() : Int {
        return (0..3).random()
    }

    private fun setInputsToTextView() {
        problemTextView.text = MathProblemsData.createProblemString()
    }

    private fun setSubmitButtonListener() {
        submitButton.setOnClickListener {
            iterateAdapterAnswerCountAndCallNotifyIfCorrect()
            setStateOfAnswerTextViewToProblemAnswered()
            showNextProblemAndSetEditTextToNullIfAnswerIsCorrect()
        }
    }

    private fun showNextProblemAndSetEditTextToNullIfAnswerIsCorrect() {
        if (doesUserInputMatchAnswer()) {
            setTypeOfProblem()
            setInputsToTextView()
            answerEditText.text = null
        }
    }

    private fun iterateAdapterAnswerCountAndCallNotifyIfCorrect() { if (doesUserInputMatchAnswer())
        answerAdapter.iterateCorrectAnswerCount()
        answerAdapter.notifyDataSetChanged()
    }

    private fun setStateOfAnswerTextViewToProblemAnswered() {
        if (doesUserInputMatchAnswer()) mathAnswerStateTextView.text = getString(R.string.math_problems_answer_correct) else mathAnswerStateTextView.text = getString(R.string.math_problems_answer_incorrect)
    }

    private fun setStateOfAnswerTextViewToEndGame(isGameWon: Boolean) {
        if (isGameWon) mathAnswerStateTextView.text = getString(R.string.math_problems_game_won) else mathAnswerStateTextView.text = getString(R.string.math_problems_game_lost)
    }

    private fun doesUserInputMatchAnswer() : Boolean {
        return answerEditText.text.toString() == MathProblemsData.answer.toString()
    }

    private fun instantiateRecyclerViewAndAdapter() {
        answerRecyclerView = rootView.findViewById(R.id.math_problems_answers_recyclerView)
        answerAdapter = AnswerAdapter(this)

        answerRecyclerView.adapter = answerAdapter
        answerRecyclerView.addItemDecoration(answerCircleDivider())
        answerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    }

    private fun answerCircleDivider() : DividerItemDecoration {
        var decoration = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
        decoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.horizontal_divider_blank)!!)
        return decoration
    }

    private fun instantiateProgressBar() {
        timerProgressBar = rootView.findViewById(R.id.sums_cards_timer_progress_bar)
        progressValue = 1000
        timerProgressBar.max = progressValue
    }

    private fun instantiateObjectAnimator() {
        objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", progressValue, 0)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 15000
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
            endOfGameFunctions(false)
        }
    }

    private fun endOfGameFunctions(gameIsWon: Boolean) {
        gamesViewModel.gameBeingPlayed = ("MathProblems")
        gamesViewModel.setIsAnswerCorrect(gameIsWon)
        setStateOfAnswerTextViewToEndGame(gameIsWon)
    }
}

class AnswerAdapter(val adapterData: AdapterData) : RecyclerView.Adapter<AnswerAdapter.AnswerCountHolder>() {
    interface AdapterData {
        fun gameIsWon()
    }

    private var correctAnswerCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerCountHolder {
        val letterItem = LayoutInflater.from(parent.context).inflate(R.layout.math_problems_adapter_views, parent, false)
        return AnswerCountHolder(letterItem)
    }

    override fun onBindViewHolder(holder: AnswerCountHolder, position: Int) {
        if (position >= correctAnswerCount) {
            holder.circleImage.setImageResource(R.drawable.sphere_hollow)
        } else {
            holder.circleImage.setImageResource(R.drawable.sphere_filled)
        }

        if (correctAnswerCount == 5) adapterData.gameIsWon()
    }

    override fun getItemCount(): Int {
        return 5
    }

    class AnswerCountHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val circleImage : ImageView = itemView.findViewById(R.id.math_problems_circle_imageView)
    }

    fun iterateCorrectAnswerCount() { correctAnswerCount++ }
}