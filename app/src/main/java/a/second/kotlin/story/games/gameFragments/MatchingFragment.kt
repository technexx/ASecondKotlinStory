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
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

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
        disableGridView()
        Log.i("testWin", "game won callback")
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
//        objectAnimator.start()
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

    private fun disableGridView() {
        matchingGridView.isEnabled = false
        matchingGridView.isClickable = false
    }

    private fun enableGridView() {
        matchingGridView.isEnabled = true
        matchingGridView.isClickable = true
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

    var cardViewOne = CardView(context)
    var cardViewTwo = CardView(context)
    var cardTextViewOne = TextView(context)
    var cardTextViewTwo = TextView(context)
    var cardOneString = " "
    var cardTwoString = " "

    var numberOfCardsTurnedOver = 0
    var numberOfCardsMatched = 0
    var nextClickResetsFlippedCards = false

    val twoCardSelectedPositionList : MutableList<Int> = mutableListOf(0, 0)
    val twoCardSelectedValueList : MutableList<String> = mutableListOf(" ", " ")

    var previousCardSelectedPosition = -1
    var firstCardSelectedPosition = 0
    var secondCardSelectedPosition = 0


    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.matching_adapter_views, null, true)

        rowView.setOnClickListener {
            if (position != previousCardSelectedPosition) {
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

                Log.i("testFlip", "position list is $twoCardSelectedPositionList")
                Log.i("testFlip", "number of cards turned over are $numberOfCardsTurnedOver")

                if (numberOfCardsTurnedOver == 1) {
                    cardOneString = fullCardList[firstCardSelectedPosition]
                    changeBackgroundOfSelectedCard(cardViewOne, cardOneString)
                    cardTextViewOne.text = cardOneString
                    Log.i("testFlip", "first card position is $firstCardSelectedPosition")

                }
                if (numberOfCardsTurnedOver == 2) {
                    cardTwoString = fullCardList[secondCardSelectedPosition]
                    changeBackgroundOfSelectedCard(cardViewTwo, cardTwoString)
                    cardTextViewTwo.text = cardTwoString
                }

                if (numberOfCardsTurnedOver == 2) {
                    if (!doBothSelectedCardsMatch()) {
                        lowerAlphaOfSelectedCards(cardViewOne)
                        lowerAlphaOfSelectedCards(cardViewTwo)
                        nextClickResetsFlippedCards = true
                    } else {
                        numberOfCardsMatched +=2
                        if (numberOfCardsMatched == 16) {
                            adapterData.gameIsWon()
                        }
                        Log.i("testWin", "number of cards matched is $numberOfCardsMatched")
                    }
                    resetCardTurnOverCount()
                    previousCardSelectedPosition = -1
                }
            }

        }
        return rowView
    }

    override fun getCount(): Int {
        return fullCardList.size
    }

    private fun resetCardTurnOverCount() { numberOfCardsTurnedOver = 0 }

    private fun populateCardHolderListsWithSelection(position: Int) {
        val valueBeneathSelectedCard = fullCardList[position]

        populateTwoCardSelectedPositionList(position)
        populateTwoCardSelectedValueList(valueBeneathSelectedCard)
    }

    private fun resetBackGroundOfCard(cardView: CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun resetCardTextViewToBlank(textView : TextView) {
        textView.text = " "
    }

    private fun populateTwoCardSelectedPositionList(position: Int) {
        if (numberOfCardsTurnedOver == 1) twoCardSelectedPositionList[0] = position
        if (numberOfCardsTurnedOver == 2) twoCardSelectedPositionList[1] = position
    }

    private fun populateTwoCardSelectedValueList(cardValue: String) {
        if (numberOfCardsTurnedOver == 1) twoCardSelectedValueList[0] = cardValue
        if (numberOfCardsTurnedOver == 2) twoCardSelectedValueList[1] = cardValue
    }

    private fun doBothSelectedCardsMatch() : Boolean {
        return twoCardSelectedValueList[0] == twoCardSelectedValueList[1]
    }

    private fun lowerAlphaOfSelectedCards(cardView: CardView) {
        cardView.alpha = 0.4f
    }

    private fun restoreAlphaOfSelectedCards(cardView: CardView) {
        cardView.alpha = 1.0f
    }

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