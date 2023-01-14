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
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MathSumsFragment : Fragment(), SumsCustomAdapter.AdapterData {

    override fun targetNumberHit(value: Int) {
        removeMatchedTargetFromList(value)
        Log.i("testCall", "value called back is $value")
    }

    override fun gameIsWon() {
    }

    private fun removeMatchedTargetFromList(number: Int) {
        targetValuesList.remove(number)
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

    private lateinit var targetAnswerTextView : TextView
    private lateinit var stateOfAnswerTextView : TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_sums_layout, container, false)

        instantiateXMLObjects()
        populateFullCardIntegerList()
        populateTargetValuesList()
        instantiateSumsGridViewAndAdapter()
        populateTargetAnswerTextView(targetValuesList[0])

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
    }

    private fun populateTargetAnswerTextView(targetValue: Int) { targetAnswerTextView.text = getString(R.string.sums_target_textView, targetValue.toString()) }

    private fun instantiateSumsGridViewAndAdapter() {
        sumsCustomAdapter = SumsCustomAdapter(requireContext(), R.layout.sums_adapter_views, fullCardIntegerList, targetValuesList, this)
        sumsGridView = rootView.findViewById(R.id.sums_cards_gridView)
        sumsGridView.numColumns = 4
        sumsGridView.adapter = sumsCustomAdapter
    }

    private fun instantiateXMLObjects() {
        targetAnswerTextView = rootView.findViewById(R.id.target_answer_textView)
        stateOfAnswerTextView = rootView.findViewById(R.id.sums_state_of_answer_textView)
    }
}

//Constructor input lists are separate objects from those in our Fragment class. We simply pass them in and name them the same.
class SumsCustomAdapter (context: Context, resource: Int, val fullCardIntegerList: ArrayList<Int>, val targetValuesList: ArrayList<Int>, val adapterData: AdapterData
): ArrayAdapter<String>(context, resource) {

    lateinit var populatedCardTextView : TextView
    lateinit var selectedCardView : CardView
    lateinit var selectedCardTextView : TextView

    var cardSelectedPositionsList : ArrayList<Int> = ArrayList()
    var cardsMatchedPositionsList : ArrayList<Int> = ArrayList()

    var totalSelectedCardsValue = 0

    interface AdapterData {
        fun targetNumberHit(value: Int)
        fun gameIsWon()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.sums_adapter_views, null, true)

        populatedCardTextView = rowView.findViewById(R.id.sums_card_textView)
        populatedCardTextView.text = fullCardIntegerList[position].toString()

        rowView.setOnClickListener {
            selectedCardView = parent[position].findViewById(R.id.sums_card_cardView)
            selectedCardTextView = parent[position].findViewById(R.id.sums_card_textView)

            if (!isCardHighlighted(selectedCardView)){
                highlightBackgroundOfCardView()
                addSelectedValueToCardsValueList(fullCardIntegerList[position])
                addToCardSelectedPositionList(position)
            } else {
                unHighlightBackgroundOfCardView()
                subtractSelectedValueFromCardsValueList(fullCardIntegerList[position])
                removeFromCardSelectedPositionList(position)
            }

            Log.i("testPositionList", "position list is $cardSelectedPositionsList")

            Log.i("testMatch", "list of target values is $targetValuesList")
            Log.i("testMatch", "selected value total is $totalSelectedCardsValue")
            Log.i("testMatch", "values match is ${doSelectedCardsEqualAValueFromTargetList()}")

            if (doSelectedCardsEqualAValueFromTargetList()) {
                for (i in cardSelectedPositionsList.indices) {
                    val cardView = parent[cardSelectedPositionsList[i]].findViewById(R.id.sums_card_cardView) as CardView
                    changeBackgroundColorOfMatchedCards(cardView)
                    addToCardsMatchedPositionList(cardSelectedPositionsList[i])
                }
                adapterData.targetNumberHit(totalSelectedCardsValue)
                removeTargetValueFromList(totalSelectedCardsValue)
                clearTotalSelectedCardsPositionList()
                Log.i("testMatch", "matched position list is {$cardsMatchedPositionsList}")
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

    private fun addToCardSelectedPositionList(position: Int) {
        cardSelectedPositionsList.add(position)
    }

    private fun removeFromCardSelectedPositionList(position: Int) {
        cardSelectedPositionsList.remove(position)
    }

    private fun highlightBackgroundOfCardView() {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lighter_grey))

        selectedCardView.isSelected = true
    }

    private fun unHighlightBackgroundOfCardView() {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        selectedCardView.isSelected = false
    }

    private fun isCardHighlighted(cardView: CardView) : Boolean { return cardView.isSelected }

    private fun addSelectedValueToCardsValueList(value: Int) { totalSelectedCardsValue += value }

    private fun subtractSelectedValueFromCardsValueList(value: Int) { totalSelectedCardsValue -= value }

    private fun addToCardsMatchedPositionList(value: Int) { cardsMatchedPositionsList.add(value) }

    private fun clearCardsMatchedPositionList() { cardsMatchedPositionsList.clear() }

    private fun removeTargetValueFromList(value: Int) { targetValuesList.remove(value) }

    private fun clearTotalSelectedCardsPositionList() { cardSelectedPositionsList.clear() }

    private fun changeBackgroundColorOfMatchedCards(cardView: CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_teal))
    }
}