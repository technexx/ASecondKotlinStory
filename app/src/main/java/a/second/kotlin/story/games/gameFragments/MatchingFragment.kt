package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.Hangman
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_matching_layout, container, false)

        instantiateMatchingGridViewAndAdapter()

        populateFullCardLetterList()
        populateGuessedCardLetterListWithBlanks()
        displayGuessedCardLetterList()

        matchingGridView.setOnItemClickListener { parent, view, position, id ->
        }

        return rootView
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
                     val fullCardList: ArrayList<String>) : ArrayAdapter<String>(context, resource) {

    var numberOfCardsTurnedOver = 0
    val twoCardSelectedPositionList : MutableList<Int> = mutableListOf(0, 0)
    val twoCardSelectedValueList : MutableList<String> = mutableListOf(" ", " ")

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)

        val rowView = inflater.inflate(R.layout.matching_adapter_views, null, true)
        val cardItemView = rowView.findViewById(R.id.matching_card_cardView) as CardView
        val cardTextView = rowView.findViewById(R.id.matching_card_textView) as TextView

        var nextClickResetsFlippedCards = false

        rowView.setOnClickListener {
            numberOfCardsTurnedOver++
            populateCardHolderListsWithSelection(position)

            val firstCardSelectedPosition = twoCardSelectedPositionList[0]
            val secondCardSelectedPosition = twoCardSelectedPositionList[1]

            val cardViewOne = parent[firstCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
            val cardViewTwo = parent[secondCardSelectedPosition].findViewById(R.id.matching_card_cardView) as CardView
            val cardTextOne = parent[firstCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView
            val cardTextTwo = parent[secondCardSelectedPosition].findViewById(R.id.matching_card_textView) as TextView

            val cardOneString = displayedList[firstCardSelectedPosition]
            val cardTwoString = displayedList[secondCardSelectedPosition]

            if (!nextClickResetsFlippedCards) {
                changeBackgroundOfSelectedCard(cardViewOne, cardOneString)
                changeBackgroundOfSelectedCard(cardViewTwo, cardTwoString)

                cardTextOne.text = cardOneString
                cardTextTwo.text = cardTwoString

                if (numberOfCardsTurnedOver == 2) {
                    if (!doBothSelectedCardsMatch()) {
                        lowerAlphaOfSelectedCards(cardViewOne)
                        lowerAlphaOfSelectedCards(cardViewTwo)
                        nextClickResetsFlippedCards = true
                    }
                    resetCardTurnOverCount()
                }
            } else {
                //Todo: Needs to retain previous two card positions for this to work.
                restoreAlphaOfSelectedCards(cardViewOne)
                restoreAlphaOfSelectedCards(cardViewTwo)
                resetBackGroundOfCard(cardViewOne)
                resetBackGroundOfCard(cardViewTwo)

                cardTextOne.text = " "
                cardTextTwo.text = " "

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
//        Log.i("testList", "two card position list is $twoCardSelectedPositionList")
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