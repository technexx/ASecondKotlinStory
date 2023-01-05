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
    val twoCardSelectedList : MutableList<String> = MutableList(2) {""}

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)

        val rowView = inflater.inflate(R.layout.matching_adapter_views, null, true)
        val cardTextView = rowView.findViewById(R.id.matching_card_textView) as TextView
        cardTextView.text = displayedList[position]

        rowView.setOnClickListener {
            turnOverCardIfFaceDown(position)
            Log.i("testClick","clicked at position $position")
        }

        return rowView
    }

    override fun getCount(): Int {
        return fullCardList.size
    }

    private fun turnOverCardIfFaceDown(position: Int) {
        val valueBeneathSelectedCard = fullCardList[position]

        if (haveTwoOrLessCardsBeenTurnedUpright()) {
            if (isDisplayedCardFaceDown(position)) {
                guessedList.set(position, valueBeneathSelectedCard)
                displayedList.set(position,valueBeneathSelectedCard)
                notifyDataSetChanged()

                populateTwoCardSelectedList(valueBeneathSelectedCard)
                numberOfCardsTurnedOver++
            }
        }
    }

    private fun haveTwoOrLessCardsBeenTurnedUpright() : Boolean {
        return numberOfCardsTurnedOver < 2
    }
    private fun isDisplayedCardFaceDown(position: Int) : Boolean {
        return (displayedList[position].equals(" "))
    }

    private fun populateTwoCardSelectedList(cardValue: String) {
        if (numberOfCardsTurnedOver == 0) twoCardSelectedList[0] = cardValue
        if (numberOfCardsTurnedOver == 1) twoCardSelectedList[1] = cardValue
        Log.i("testList", "two card list is $twoCardSelectedList")
    }

    private fun doBothSelectedCardsMatch() : Boolean {
        return twoCardSelectedList[0] == twoCardSelectedList[1]
    }
}