package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.Hangman
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.nio.channels.SelectableChannel

class MatchingFragment : Fragment() {

    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()
    private lateinit var rootView : View

    private lateinit var matchingGridView : GridView
    private lateinit var matchingAdapter : ArrayAdapter<String>

    private var fullCardLetterList : ArrayList<String> = ArrayList()
    private var guessedCardLetterList : ArrayList<String> = ArrayList()
    private var displayedCardLetterList : ArrayList<String> = ArrayList()

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var progressBarRunnable : Runnable
    private lateinit var objectAnimator : ObjectAnimator
    private var handler = Handler()
    private var progressValue = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_matching_layout, container, false)

        instantiateMatchingGridViewAndAdapter()
        instantiateProgressBar()
        instantiateProgressBarRunnable()

        populateFullCardLetterList()
        populateGuessedCardLetterListWithBlanks()
        displayGuessedCardLetterList()

        return rootView
    }

    private fun instantiateProgressBar() {
        timerProgressBar = rootView.findViewById(R.id.matching_cards_timer_progress_bar)
        progressValue = 1000
        timerProgressBar.max = progressValue
    }

    private fun instantiateProgressBarRunnable() {
        progressBarRunnable = Runnable {
            progressValue -= 10
            timerProgressBar.progress = progressValue
            handler.postDelayed(progressBarRunnable, 100)

            Log.i("testProgress", "progressValue is $progressValue")
        }

        handler.post(progressBarRunnable)
    }

    private fun instantiateObjectAnimator() {
        objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", progressValue, 0)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 15000
        objectAnimator.start()
    }

    fun iterateProgressBar() {

    }

    private fun displayFullCardLetterList() {
        displayedCardLetterList.addAll(fullCardLetterList)
        matchingAdapter.notifyDataSetChanged()
    }

    private fun displayGuessedCardLetterList() {
        displayedCardLetterList.addAll(guessedCardLetterList)
        matchingAdapter.notifyDataSetChanged()
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

    private fun populateGuessedCardLetterListWithBlanks() {
        for (i in fullCardLetterList) guessedCardLetterList.add(" ")
    }

    private fun instantiateMatchingGridViewAndAdapter() {
        matchingGridView = rootView.findViewById(R.id.matching_cards_gridView)
        matchingGridView.numColumns = 4
        matchingAdapter = ArrayAdapter(requireContext(), R.layout.matching_adapter_views, R.id.matching_card_textView, displayedCardLetterList)

        val customAdapter: CustomAdapter = CustomAdapter(requireContext(), R.layout.matching_adapter_views, displayedCardLetterList, guessedCardLetterList, fullCardLetterList)
        matchingGridView.adapter = customAdapter
    }
}

//We can explicitly declare objects in our constructor, so we don't have to re-assign them (e.g. guessedList = mGuessedList) within class.
class CustomAdapter (context: Context, resource: Int, val displayedList: ArrayList<String>, val guessedList: ArrayList<String>,
                     val fullCardList: ArrayList<String>): ArrayAdapter<String>(context, resource) {

    var numberOfCardsTurnedOver = 0
    var nextClickResetsFlippedCards = false

    val twoCardSelectedPositionList : MutableList<Int> = mutableListOf(0, 0)
    val twoCardSelectedValueList : MutableList<String> = mutableListOf(" ", " ")

    var firstCardSelectedPosition = 0
    var secondCardSelectedPosition = 0

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.matching_adapter_views, null, true)

        var cardViewOne: CardView
        var cardViewTwo: CardView
        var cardTextViewOne: TextView
        var cardTextViewTwo: TextView
        var cardOneString: String
        var cardTwoString: String

        rowView.setOnClickListener {
            //            Log.i("testCard", "position list is $twoCardSelectedPositionList")
            //            Log.i("testCard","value list is $twoCardSelectedValueList")

            if (!nextClickResetsFlippedCards) {
                numberOfCardsTurnedOver++
                populateCardHolderListsWithSelection(position)

                firstCardSelectedPosition = twoCardSelectedPositionList[0]
                secondCardSelectedPosition = twoCardSelectedPositionList[1]
            }

            cardViewOne = parent[firstCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
            cardViewTwo = parent[secondCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
            cardTextViewOne = parent[firstCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView
            cardTextViewTwo = parent[secondCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView

            if (!nextClickResetsFlippedCards) {
                if (numberOfCardsTurnedOver == 1) {
                    cardOneString = displayedList[firstCardSelectedPosition]
                    changeBackgroundOfSelectedCard(cardViewOne, cardOneString)
                    cardTextViewOne.text = cardOneString

                }
                if (numberOfCardsTurnedOver == 2) {
                    cardTwoString = displayedList[secondCardSelectedPosition]
                    changeBackgroundOfSelectedCard(cardViewTwo, cardTwoString)
                    cardTextViewTwo.text = cardTwoString
                }

                if (numberOfCardsTurnedOver == 2) {
                    if (!doBothSelectedCardsMatch()) {
                        lowerAlphaOfSelectedCards(cardViewOne)
                        lowerAlphaOfSelectedCards(cardViewTwo)
                        nextClickResetsFlippedCards = true
                    }
                    resetCardTurnOverCount()
                }
            } else {
                restoreAlphaOfSelectedCards(cardViewOne)
                restoreAlphaOfSelectedCards(cardViewTwo)
                resetBackGroundOfCard(cardViewOne)
                resetBackGroundOfCard(cardViewTwo)

                cardTextViewOne.text = " "
                cardTextViewTwo.text = " "

                changeBackgroundOfSelectedCard(cardViewOne, " ")
                changeBackgroundOfSelectedCard(cardViewTwo, " ")

                nextClickResetsFlippedCards = false
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

        guessedList[position] = valueBeneathSelectedCard
        displayedList[position] = valueBeneathSelectedCard

        populateTwoCardSelectedPositionList(position)
        populateTwoCardSelectedValueList(valueBeneathSelectedCard)
    }

    private fun resetBackGroundOfCard(cardView: CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun populateTwoCardSelectedPositionList(position: Int) {
        if (numberOfCardsTurnedOver == 1) twoCardSelectedPositionList[0] = position
        if (numberOfCardsTurnedOver == 2) twoCardSelectedPositionList[1] = position
    }

    private fun populateTwoCardSelectedValueList(cardValue: String) {
        if (numberOfCardsTurnedOver == 1) twoCardSelectedValueList[0] = cardValue
        if (numberOfCardsTurnedOver == 2) twoCardSelectedValueList[1] = cardValue
    }

    private fun setTwoCardListValuesToBlankString() {
        twoCardSelectedValueList[0] = " "
        twoCardSelectedValueList[1] = " "
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