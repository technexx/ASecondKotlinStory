package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
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
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
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

        populateFullCardIntegerList()
        instantiateSumsGridViewAndAdapter()

        return rootView
    }

    private fun populateFullCardIntegerList() {
        while (fullCardIntegerList.size < 16) {
            val valueToAdd = (2..99).random()
            if (!fullCardIntegerList.contains(valueToAdd)) {
                fullCardIntegerList.add(valueToAdd)
            }
        }

        Log.i("testList", "list is $fullCardIntegerList")
    }

    private fun instantiateSumsGridViewAndAdapter() {
        sumsCustomAdapter = SumsCustomAdapter(requireContext(), R.layout.sums_adapter_views, fullCardIntegerList, this)
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
        lateinit var populatedCardTextView : TextView
        lateinit var selectedCardView : CardView
        lateinit var selectedCardTextView : TextView

        var cardSelectedPositionsList : ArrayList<Int> = ArrayList()
        var cardSelectedValueList : ArrayList<String> = ArrayList()
    }

    private var holder = ObjectHolder()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.sums_adapter_views, null, true)

        holder.populatedCardTextView = rowView.findViewById(R.id.sums_card_textView)
        holder.populatedCardTextView.setText(fullCardList[position].toString())

        rowView.setOnClickListener {
            holder.selectedCardView = parent[position].findViewById(R.id.sums_card_cardView)
            holder.selectedCardTextView = parent[position].findViewById(R.id.sums_card_textView)

            if (!isCardHighlighted(holder.selectedCardView)) highlightBackgroundOfCardView() else unHighlightBackgroundOfCardView()

            addToCardPositionList(position)
        }

        return rowView
    }

    override fun getCount(): Int {
        return fullCardList.size
    }

    private fun addToCardPositionList(position: Int) {
        holder.cardSelectedPositionsList.add(position)
    }

    private fun removeFromCardPositionList(position: Int) {
        holder.cardSelectedPositionsList.remove(position)
    }

    private fun highlightBackgroundOfCardView() {
        holder.selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_teal))
        holder.selectedCardView.isSelected = true
    }

    private fun unHighlightBackgroundOfCardView() {
        holder.selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        holder.selectedCardView.isSelected = false
    }

    private fun isCardHighlighted(cardView: CardView) : Boolean {
        return cardView.isSelected
    }
}