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

    private var cardLetterList : ArrayList<String> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_matching_layout, container, false)

        populateCardLetterList()
        instantiateMatchingGridViewAndAdapter()

        return rootView
    }

    private fun populateCardLetterList() {
        var input: Char
        input = 'A'

        while (input <= 'H') {
            cardLetterList.add(input.toString())
            cardLetterList.add(input.toString())
            input++
        }

        cardLetterList.shuffle()

        Log.i("testList", "list is $cardLetterList")
    }

    private fun instantiateMatchingGridViewAndAdapter() {
        matchingGridView = rootView.findViewById(R.id.matching_cards_gridView)
        matchingGridView.numColumns = 4
        matchingAdapter = ArrayAdapter(requireContext(), R.layout.matching_adapter_views, R.id.matching_card_textView, cardLetterList)
        matchingGridView.adapter = matchingAdapter

    }
}