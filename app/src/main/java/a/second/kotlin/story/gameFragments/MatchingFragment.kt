package a.second.kotlin.story.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.service.autofill.FieldClassification.Match
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MatchingFragment : Fragment(), MatchingCustomAdapter.AdapterData {
    private lateinit var rootView : View

    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    private lateinit var matchingGridView : GridView
    private lateinit var MatchingCustomAdapter : MatchingCustomAdapter

    private var fullCardLetterList : ArrayList<String> = ArrayList()

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var objectAnimator : ObjectAnimator
    private var progressValue = 0

    private lateinit var stateOfAnswerTextView : TextView

    override fun gameIsWon() {
        endOfGameFunction(true)
        pauseObjectAnimator()
    }

    fun disableAdapterClicks() { MatchingCustomAdapter.disableClicksIfGameOver() }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_matching_layout, container, false)

        MatchingCustomAdapter = MatchingCustomAdapter(requireContext(), R.layout.matching_adapter_views, fullCardLetterList, this)

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
            endOfGameFunction(false)
        }
    }

    private fun endOfGameFunction(gameIsWon: Boolean) {
        gamesViewModel.gameBeingPlayed = ("Matching")
        gamesViewModel.setIsAnswerCorrect(gameIsWon)
        setStateOfAnswerTextViewToEndGame(gameIsWon)
    }

    private fun setStateOfAnswerTextViewToEndGame(isGameWon: Boolean) {
        if (isGameWon) stateOfAnswerTextView.text = getString(R.string.matching_game_won) else stateOfAnswerTextView.text = getString(R.string.matching_game_lost)
    }

    private fun startObjectAnimator() {
        objectAnimator.start()
    }

    fun pauseObjectAnimator() {
        objectAnimator.pause()
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

        matchingGridView.adapter = MatchingCustomAdapter

        matchingGridView.isEnabled = false

    }

    private fun instantiateXmlViews() {
        stateOfAnswerTextView = rootView.findViewById(R.id.matching_state_of_answer_textView)
    }
}

//We can explicitly declare objects in our constructor, so we don't have to re-assign them (e.g. guessedList = mGuessedList) within class.
class MatchingCustomAdapter (context: Context, resource: Int, val fullCardValueList: ArrayList<String>, val adapterData: AdapterData
): ArrayAdapter<String>(context, resource) {

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

    var rowView = View(context)

    interface AdapterData {
        fun gameIsWon()
    }

    fun disableClicksIfGameOver() {
        rowView.isClickable = false
        rowView.isEnabled = false
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        rowView = inflater.inflate(R.layout.matching_adapter_views, null, true)

        disableClicksIfGameOver()

        rowView.setOnClickListener {
            if (position != previousCardSelectedPosition && !matchedPositionsList.contains(position)) {
                if (nextClickResetsFlippedCards) {
                    restoreAlphaOfSelectedCards(cardViewOne)
                    restoreAlphaOfSelectedCards(cardViewTwo)
                    resetBackGroundOfCard(cardViewOne)
                    resetBackGroundOfCard(cardViewTwo)

                    resetCardTextViewToBlank(cardTextViewOne)
                    resetCardTextViewToBlank(cardTextViewTwo)

                    changeBackgroundOfSelectedCard(cardViewOne, " ")
                    changeBackgroundOfSelectedCard(cardViewTwo, " ")

                    nextClickResetsFlippedCards = false
                }

                numberOfCardsTurnedOver++
                previousCardSelectedPosition = position

                populateCardHolderListsWithSelection(position)
                firstCardSelectedPosition = twoCardSelectedPositionList[0]
                secondCardSelectedPosition = twoCardSelectedPositionList[1]

                cardViewOne = parent[firstCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
                cardViewTwo = parent[secondCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
                cardTextViewOne = parent[firstCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView
                cardTextViewTwo = parent[secondCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView

                if (numberOfCardsTurnedOver == 1) {
                    cardOneString = fullCardValueList[firstCardSelectedPosition]
                    changeBackgroundOfSelectedCard(cardViewOne, cardOneString)
                    cardTextViewOne.text = cardOneString
                }
                if (numberOfCardsTurnedOver == 2) {
                    cardTwoString = fullCardValueList[secondCardSelectedPosition]
                    changeBackgroundOfSelectedCard(cardViewTwo, cardTwoString)
                    cardTextViewTwo.text = cardTwoString

                    if (!doBothSelectedCardsMatch()) {
                        lowerAlphaOfSelectedCards(cardViewOne)
                        lowerAlphaOfSelectedCards(cardViewTwo)
                        nextClickResetsFlippedCards = true
                    } else {
                        addPositionsToMatchedPositionsList(firstCardSelectedPosition)
                        addPositionsToMatchedPositionsList(secondCardSelectedPosition)
                        numberOfCardsMatched +=2
                        if (numberOfCardsMatched == 16) adapterData.gameIsWon()
                    }
                    resetCardTurnOverCount()
                    previousCardSelectedPosition = -1
                }
            }
        }
        return rowView
    }

    override fun getCount(): Int {
        return fullCardValueList.size
    }

    private fun populateCardHolderListsWithSelection(position: Int) {
        val valueBeneathSelectedCard = fullCardValueList[position]

        populateTwoCardSelectedPositionList(position)
        populateTwoCardSelectedValueList(valueBeneathSelectedCard)
    }

    private fun populateTwoCardSelectedPositionList(position: Int) {
        if (numberOfCardsTurnedOver == 1) twoCardSelectedPositionList[0] = position
        if (numberOfCardsTurnedOver == 2) twoCardSelectedPositionList[1] = position
    }

    private fun populateTwoCardSelectedValueList(cardValue: String) {
        if (numberOfCardsTurnedOver == 1) twoCardSelectedValueList[0] = cardValue
        if (numberOfCardsTurnedOver == 2) twoCardSelectedValueList[1] = cardValue
    }

    private fun addPositionsToMatchedPositionsList(position: Int) { matchedPositionsList.add(position) }

    private fun doBothSelectedCardsMatch() : Boolean {
        return twoCardSelectedValueList[0] == twoCardSelectedValueList[1]
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

    private fun resetCardTurnOverCount() { numberOfCardsTurnedOver = 0 }

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