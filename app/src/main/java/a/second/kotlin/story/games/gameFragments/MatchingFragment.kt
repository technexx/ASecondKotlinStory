package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment

class MatchingFragment : Fragment(), CustomAdapter.AdapterData {
    private lateinit var rootView : View

    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    private lateinit var matchingGridView : GridView
    private lateinit var customAdapter : CustomAdapter

    private var fullCardLetterList : ArrayList<String> = ArrayList()

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var objectAnimator : ObjectAnimator
    private var progressValue = 0

    private lateinit var stateOfAnswerTextView : TextView

    private var gameHasBeenWon = false

    override fun gameIsWon() {
        gameHasBeenWon = true
        setWinningTextView()
        stopObjectAnimator()
        customAdapter.setGameIsOverToTrue()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_matching_layout, container, false)

        customAdapter = CustomAdapter(requireContext(), R.layout.matching_adapter_views, fullCardLetterList, this)

        instantiateXmlViews()
        instantiateMatchingGridViewAndAdapter()
        instantiateProgressBar()
        instantiateObjectAnimator()
        startObjectAnimator()

        populateFullCardLetterList()

        return rootView
    }

    private fun instantiateProgressBar() {
        timerProgressBar = rootView.findViewById(R.id.matching_cards_timer_progress_bar)
        progressValue = 1000
        timerProgressBar.max = progressValue
    }

    private fun instantiateObjectAnimator() {
        objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", progressValue, 0)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 50000

        objectAnimator.doOnEnd {
            if (!gameHasBeenWon) setLosingTextView() else setWinningTextView()
            gameHasBeenWon = false
        }
    }

    private fun startObjectAnimator() {
        objectAnimator.start()
    }

    private fun stopObjectAnimator() {
        objectAnimator.cancel()
    }

    private fun setWinningTextView() {
        stateOfAnswerTextView.setText(R.string.matching_problem_correct)
    }

    private fun setLosingTextView() {
        stateOfAnswerTextView.setText(R.string.matching_problem_incorrect)
    }

    private fun populateFullCardLetterList() {
        var input: Char
        input = 'A'

        while (input <= 'H') {
            fullCardLetterList.add(input.toString())
            fullCardLetterList.add(input.toString())
            input++
        }

        fullCardLetterList.shuffle()
    }

    private fun instantiateMatchingGridViewAndAdapter() {
        matchingGridView = rootView.findViewById(R.id.matching_cards_gridView)
        matchingGridView.numColumns = 4

        matchingGridView.adapter = customAdapter
    }

    private fun instantiateXmlViews() {
        stateOfAnswerTextView = rootView.findViewById(R.id.matching_state_of_answer_textView)
    }
}

//We can explicitly declare objects in our constructor, so we don't have to re-assign them (e.g. guessedList = mGuessedList) within class.
class CustomAdapter (context: Context, resource: Int, val fullCardList: ArrayList<String>, val adapterData: AdapterData
): ArrayAdapter<String>(context, resource) {

    interface AdapterData {
        fun gameIsWon()
    }

    class ObjectHolder() {
        lateinit var cardViewOne : CardView
        lateinit var cardViewTwo : CardView
        lateinit var cardTextViewOne : TextView
        lateinit var cardTextViewTwo : TextView
        var cardOneString = " "
        var cardTwoString = " "

        var numberOfCardsTurnedOver = 0
        var numberOfCardsMatched = 0
        var nextClickResetsFlippedCards = false

        val twoCardSelectedPositionList : MutableList<Int> = mutableListOf(0, 0)
        val twoCardSelectedValueList : MutableList<String> = mutableListOf(" ", " ")
        var matchedPositionsList : ArrayList<Int> = ArrayList()

        var previousCardSelectedPosition = -1
        var firstCardSelectedPosition = 0
        var secondCardSelectedPosition = 0

        var gameIsOver = false
    }

    private var holder = ObjectHolder()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.matching_adapter_views, null, true)

        rowView.setOnClickListener {
            if (position != holder.previousCardSelectedPosition && !holder.matchedPositionsList.contains(position)) {
                if (holder.nextClickResetsFlippedCards) {
                    restoreAlphaOfSelectedCards(holder.cardViewOne)
                    restoreAlphaOfSelectedCards(holder.cardViewTwo)
                    resetBackGroundOfCard(holder.cardViewOne)
                    resetBackGroundOfCard(holder.cardViewTwo)

                    resetCardTextViewToBlank(holder.cardTextViewOne)
                    resetCardTextViewToBlank(holder.cardTextViewTwo)

                    changeBackgroundOfSelectedCard(holder.cardViewOne, " ")
                    changeBackgroundOfSelectedCard(holder.cardViewTwo, " ")

                    holder.nextClickResetsFlippedCards = false
                }

                holder.numberOfCardsTurnedOver++
                holder.previousCardSelectedPosition = position

                populateCardHolderListsWithSelection(position)
                holder.firstCardSelectedPosition = holder.twoCardSelectedPositionList[0]
                holder.secondCardSelectedPosition = holder.twoCardSelectedPositionList[1]

                holder.cardViewOne = parent[holder.firstCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
                holder.cardViewTwo = parent[holder.secondCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
                holder.cardTextViewOne = parent[holder.firstCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView
                holder.cardTextViewTwo = parent[holder.secondCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView

                if (holder.numberOfCardsTurnedOver == 1) {
                    holder.cardOneString = fullCardList[holder.firstCardSelectedPosition]
                    changeBackgroundOfSelectedCard(holder.cardViewOne, holder.cardOneString)
                    holder.cardTextViewOne.text = holder.cardOneString
                }
                if (holder.numberOfCardsTurnedOver == 2) {
                    holder.cardTwoString = fullCardList[holder.secondCardSelectedPosition]
                    changeBackgroundOfSelectedCard(holder.cardViewTwo, holder.cardTwoString)
                    holder.cardTextViewTwo.text = holder.cardTwoString

                    if (!doBothSelectedCardsMatch()) {
                        lowerAlphaOfSelectedCards(holder.cardViewOne)
                        lowerAlphaOfSelectedCards(holder.cardViewTwo)
                        holder.nextClickResetsFlippedCards = true
                    } else {
                        addPositionsToMatchedPositionsList(holder.firstCardSelectedPosition)
                        addPositionsToMatchedPositionsList(holder.secondCardSelectedPosition)
                        holder.numberOfCardsMatched +=2
                        if (holder.numberOfCardsMatched == 16) adapterData.gameIsWon()
                    }
                    resetCardTurnOverCount()
                    holder.previousCardSelectedPosition = -1
                }
            }
        }
        return rowView
    }

    override fun getCount(): Int {
        return fullCardList.size
    }

    fun setGameIsOverToTrue() {
        holder.gameIsOver = true
    }

    fun resetBoard() {
        holder = ObjectHolder()
        notifyDataSetChanged()
    }

    private fun populateCardHolderListsWithSelection(position: Int) {
        val valueBeneathSelectedCard = fullCardList[position]

        populateTwoCardSelectedPositionList(position)
        populateTwoCardSelectedValueList(valueBeneathSelectedCard)
    }

    private fun populateTwoCardSelectedPositionList(position: Int) {
        if (holder.numberOfCardsTurnedOver == 1) holder.twoCardSelectedPositionList[0] = position
        if (holder.numberOfCardsTurnedOver == 2) holder.twoCardSelectedPositionList[1] = position
    }

    private fun populateTwoCardSelectedValueList(cardValue: String) {
        if (holder.numberOfCardsTurnedOver == 1) holder.twoCardSelectedValueList[0] = cardValue
        if (holder.numberOfCardsTurnedOver == 2) holder.twoCardSelectedValueList[1] = cardValue
    }

    private fun addPositionsToMatchedPositionsList(position: Int) { holder.matchedPositionsList.add(position) }

    private fun doBothSelectedCardsMatch() : Boolean {
        return holder.twoCardSelectedValueList[0] == holder.twoCardSelectedValueList[1]
    }

    private fun resetBackGroundOfCard(cardView: CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun resetCardTextViewToBlank(textView : TextView) {
        textView.text = " "
    }

    private fun lowerAlphaOfSelectedCards(cardView: CardView) {
        cardView.alpha = 0.4f
    }

    private fun restoreAlphaOfSelectedCards(cardView: CardView) {
        cardView.alpha = 1.0f
    }

    private fun resetCardTurnOverCount() { holder.numberOfCardsTurnedOver = 0 }

    private fun changeBackgroundOfSelectedCard(cardView: CardView, letter: String) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, cardColor(letter)))
    }

    private fun cardColor(letter: String) : Int {
        var colorToReturn = R.color.white
        when (letter) {
            "A" -> colorToReturn = R.color.light_teal
            "B" -> colorToReturn = R.color.purple_200
            "C" -> colorToReturn = R.color.circular_progress_default_progress
            "D" -> colorToReturn = R.color.lighter_green
            "E" -> colorToReturn = R.color.light_red
            "F" -> colorToReturn = R.color.light_blue
            "G" -> colorToReturn = R.color.light_orange
            "H" -> colorToReturn = R.color.android_yellow
        }
        return colorToReturn
    }
}