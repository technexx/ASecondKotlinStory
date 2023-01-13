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
import kotlin.math.max

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
    private var targetValuesList : ArrayList<Int> = ArrayList()

    private lateinit var stateOfAnswerTextView : TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_sums_layout, container, false)

        populateFullCardIntegerList()
        populateTargetValuesList()
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
    }

    private fun populateTargetValuesList() {
        val cardValueTempList = ArrayList(fullCardIntegerList)
        var valueToAdd = 0

        while (cardValueTempList.size > 0) {
            var numberOfCardsToAdd: Int
            var maxCardsToAdd = 0

            if (cardValueTempList.size > 6) maxCardsToAdd = (2..5).random() else maxCardsToAdd = (2..3).random()
            numberOfCardsToAdd = (2..maxCardsToAdd).random()

            if (cardValueTempList.size < 5) {
                numberOfCardsToAdd = cardValueTempList.size
            }

            repeat(numberOfCardsToAdd) {
                valueToAdd += cardValueTempList[0]
                cardValueTempList.removeAt(0)
            }

            targetValuesList.add(valueToAdd)
            valueToAdd = 0
        }

        Log.i("testList", "target values list is $targetValuesList")
    }

    private fun instantiateSumsGridViewAndAdapter() {
        sumsCustomAdapter = SumsCustomAdapter(requireContext(), R.layout.sums_adapter_views, fullCardIntegerList, targetValuesList, this)
        sumsGridView = rootView.findViewById(R.id.sums_cards_gridView)
        sumsGridView.numColumns = 4
        sumsGridView.adapter = sumsCustomAdapter
    }
}

//Constructor input lists are separate objects from those in our Fragment class. We simply pass them in and name them the same.
class SumsCustomAdapter (context: Context, resource: Int, val fullCardIntegerList: ArrayList<Int>, val targetValuesList: ArrayList<Int>, val adapterData: AdapterData
): ArrayAdapter<String>(context, resource) {

    lateinit var populatedCardTextView : TextView
    lateinit var selectedCardView : CardView
    lateinit var selectedCardTextView : TextView

    var cardSelectedPositionsList : ArrayList<Int> = ArrayList()
    var cardSelectedValueList : ArrayList<Int> = ArrayList()

    var targetTotal = 0
    var totalSelectedCardsValue = 0

    interface AdapterData {
        fun gameIsWon()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.sums_adapter_views, null, true)

        populatedCardTextView = rowView.findViewById(R.id.sums_card_textView)
        populatedCardTextView.setText(fullCardIntegerList[position].toString())

        rowView.setOnClickListener {
            selectedCardView = parent[position].findViewById(R.id.sums_card_cardView)
            selectedCardTextView = parent[position].findViewById(R.id.sums_card_textView)

            if (!isCardHighlighted(selectedCardView)){
                highlightBackgroundOfCardView()
                addSelectedValueToCardsValueList(fullCardIntegerList[position])
            } else {
                unHighlightBackgroundOfCardView()
                subtractSelectedValueFromCardsValueList(fullCardIntegerList[position])
            }

            Log.i("testMatch", "list of target values is $targetValuesList")
            Log.i("testMatch", "selected value total is ${totalSelectedCardsValue}")
            Log.i("testMatch", "values match is ${doSelectedCardsEqualAValueFromTargetList()}")

            if (doSelectedCardsEqualAValueFromTargetList()) {

            }

        }

        return rowView
    }

    override fun getCount(): Int {
        return fullCardIntegerList.size
    }

    private fun doSelectedCardsEqualAValueFromTargetList() : Boolean {
        var booleanToReturn = false
        for (i in targetValuesList.indices) {
            if (totalSelectedCardsValue == targetValuesList[i]) booleanToReturn = true
        }
        return booleanToReturn
    }

    private fun addToCardPositionList(position: Int) {
        cardSelectedPositionsList.add(position)
    }

    private fun removeFromCardPositionList(position: Int) {
        cardSelectedPositionsList.remove(position)
    }

    private fun highlightBackgroundOfCardView() {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_teal))
        selectedCardView.isSelected = true
    }

    private fun unHighlightBackgroundOfCardView() {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        selectedCardView.isSelected = false
    }

    private fun isCardHighlighted(cardView: CardView) : Boolean {
        return cardView.isSelected
    }

    private fun addSelectedValueToCardsValueList(value: Int) {
        totalSelectedCardsValue += value
    }

    private fun subtractSelectedValueFromCardsValueList(value: Int) {
        totalSelectedCardsValue -= value
    }
}