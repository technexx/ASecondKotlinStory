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
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MathSumsFragment : Fragment(), SumsCustomAdapter.AdapterData{
    override fun gameIsWon() {
    }

    private lateinit var rootView : View

    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    private lateinit var sumsGridView : GridView
    private lateinit var sumsCustomAdapter : SumsCustomAdapter

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var objectAnimator : ObjectAnimator
    private var progressValue = 0

    private var fullCardIntegerList : ArrayList<Int> = ArrayList()

    private lateinit var stateOfAnswerTextView : TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_sums_layout, container, false)

        sumsCustomAdapter = SumsCustomAdapter(requireContext(), R.layout.sums_adapter_views, fullCardIntegerList, this)

        populateFullCardIntegerList()
        instantiateSumsGridViewAndAdapter()

        return rootView
    }

    private fun populateFullCardIntegerList() {
        while (fullCardIntegerList.size > 16) {
            val valueToAdd = (2..99).random()
            if (!fullCardIntegerList.contains(valueToAdd)) {
                fullCardIntegerList.add(valueToAdd)
            }
        }
        Log.i("testList", "list is $fullCardIntegerList")
    }

    private fun instantiateSumsGridViewAndAdapter() {
        sumsGridView = rootView.findViewById(R.id.sums_cards_gridView)
        sumsGridView.numColumns = 4

        sumsGridView.adapter = sumsCustomAdapter
    }
}

class SumsCustomAdapter (context: Context, resource: Int, val fullCardList: ArrayList<Int>, val adapterData: AdapterData
): ArrayAdapter<String>(context, resource) {

    interface AdapterData {
        fun gameIsWon()
    }

    class ObjectHolder() {
        lateinit var selectedCardView : CardView
        lateinit var selectedCardTextView : TextView
    }

    private var holder = ObjectHolder()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.sums_adapter_views, null, true)

        holder.selectedCardView = getCardViewAtPosition(parent, position)
        holder.selectedCardTextView = getCardTextViewAtPosition(parent, position)

        holder.selectedCardView.setOnClickListener {
            Log.i("testClick", "clicked at $position")
        }

        return rowView
    }

    override fun getCount(): Int {
        return fullCardList.size
    }

    private fun getCardViewAtPosition(parent: ViewGroup, position: Int) : CardView {
        return parent[position].findViewById(R.id.sums_card_cardView) as CardView
    }

    private fun getCardTextViewAtPosition(parent: ViewGroup, position: Int) : TextView {
        return parent[position].findViewById(R.id.sums_card_textView) as TextView
    }
}