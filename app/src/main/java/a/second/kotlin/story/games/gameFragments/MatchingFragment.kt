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
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView

class MatchingFragment : Fragment() {

    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()
    private lateinit var rootView : View

    private lateinit var matchingGridView : GridView
    private lateinit var matchingAdapter : ArrayAdapter<String>

    private var fullCardLetterList : ArrayList<String> = ArrayList()
    private var blankCardLetterList : ArrayList<String> = ArrayList()
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
        populatedBlankLetterCardList()
        displayFullCardLetterList()

        return rootView
    }

    private fun clearDisplayedCardLetterList() { displayedCardLetterList.clear() }

    private fun displayFullCardLetterList() {
        displayedCardLetterList.addAll(fullCardLetterList)
        matchingAdapter.notifyDataSetChanged()
    }

    private fun displayBlankCardLetterList() {
        displayedCardLetterList.addAll(blankCardLetterList)
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

    private fun populatedBlankLetterCardList() {
        for (i in fullCardLetterList) blankCardLetterList.add(" ")
    }

    private fun instantiateMatchingGridViewAndAdapter() {
        matchingGridView = rootView.findViewById(R.id.matching_cards_gridView)
        matchingGridView.numColumns = 4
        matchingAdapter = ArrayAdapter(requireContext(), R.layout.matching_adapter_views, R.id.matching_card_textView, displayedCardLetterList)
        matchingGridView.adapter = matchingAdapter

    }
}