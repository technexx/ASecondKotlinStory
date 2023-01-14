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
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

//Todo: target textview did not change after first match.
class MathSumsFragment : Fragment(), SumsCustomAdapter.AdapterData {

    override fun targetNumberHit(value: Int) {
        removeMatchedTargetFromList(value)
        if (targetValuesList.size >0 ) populateTargetAnswerTextView(targetValuesList[0])
        Log.i("testMatch", "value called back is $value")
        Log.i("testMatch", "revised target list in callback is {$targetValuesList}")
    }

    override fun gameIsWon() {
        setStateOfAnswersAndTextView(true)
        sendEndGameLiveData()
        sendEndGameWinOrLoss(true)
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

        instantiateProgressBar()
        instantiateObjectAnimator()
        startObjectAnimator()

        return rootView
    }

    private fun populateFullCardIntegerList() {
        while (fullCardIntegerList.size < 16) {
            fullCardIntegerList.add((1..10).random())
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

    private fun setStateOfAnswersAndTextView(gameIsWon: Boolean) {
        if (gameIsWon) stateOfAnswerTextView.text = getString(R.string.sums_problem_correct) else stateOfAnswerTextView.text = getString(R.string.sums_problem_incorrect)
    }

    private fun instantiateProgressBar() {
        timerProgressBar = rootView.findViewById(R.id.sums_cards_timer_progress_bar)
        progressValue = 1000
        timerProgressBar.max = progressValue
    }

    private fun instantiateObjectAnimator() {
        objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", progressValue, 0)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 20000

        objectAnimator.doOnEnd {
            sendEndGameLiveData()
            sendEndGameWinOrLoss(false)
            setStateOfAnswersAndTextView(false)
        }
    }

    private fun sendEndGameLiveData() {
        gamesViewModel.setWhichGameIsBeingPlayed("Sums")
    }

    private fun sendEndGameWinOrLoss(winOrLoss: Boolean) {
        gamesViewModel.setIsAnswerCorrect(winOrLoss)
    }

    private fun startObjectAnimator() {
        objectAnimator.start()
    }

    private fun stopObjectAnimator() {
        objectAnimator.cancel()
    }

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
            if (!cardsMatchedPositionsList.contains(position)) {
                selectedCardView = parent[position].findViewById(R.id.sums_card_cardView)
                selectedCardTextView = parent[position].findViewById(R.id.sums_card_textView)

                if (!isCardHighlighted(selectedCardView)) {
                    highlightBackgroundOfCardView()
                    addSelectedValueToCardsValueList(fullCardIntegerList[position])
                    addToCardSelectedPositionList(position)
                } else {
                    unHighlightBackgroundOfCardView()
                    subtractSelectedValueFromCardsValueList(fullCardIntegerList[position])
                    removeFromCardSelectedPositionList(position)
                }

//                Log.i("testMatch", "list of target values is $targetValuesList")
                Log.i("testMatch", "target value is ${targetValuesList[0]}")
                Log.i("testMatch", "selected value total is $totalSelectedCardsValue")

                if (doSelectedCardsEqualTargetValue(targetValuesList[0])) {
                    for (i in cardSelectedPositionsList.indices) {
                        val cardView = parent[cardSelectedPositionsList[i]].findViewById(R.id.sums_card_cardView) as CardView
                        changeBackgroundColorOfMatchedCards(cardView)
                        addToCardsMatchedPositionList(cardSelectedPositionsList[i])
                    }
                    adapterData.targetNumberHit(totalSelectedCardsValue)
                    removeTargetValueFromList(totalSelectedCardsValue)
                    zeroOutTotalCardsSelectedValue()
                    clearTotalSelectedCardsPositionList()
                    Log.i("testMatch", "matched!")
                }
            }
        }
        return rowView
    }

    override fun getCount(): Int { return fullCardIntegerList.size }

    private fun addToCardSelectedPositionList(position: Int) { cardSelectedPositionsList.add(position)}

    private fun removeFromCardSelectedPositionList(position: Int) { cardSelectedPositionsList.remove(position) }

    private fun clearTotalSelectedCardsPositionList() { cardSelectedPositionsList.clear() }

    private fun isCardHighlighted(cardView: CardView) : Boolean { return cardView.isSelected }

    private fun addSelectedValueToCardsValueList(value: Int) { totalSelectedCardsValue += value }

    private fun subtractSelectedValueFromCardsValueList(value: Int) { totalSelectedCardsValue -= value }

    private fun doSelectedCardsEqualTargetValue(value: Int) : Boolean { return totalSelectedCardsValue == value }

    private fun zeroOutTotalCardsSelectedValue() { totalSelectedCardsValue = 0 }

    private fun addToCardsMatchedPositionList(value: Int) { cardsMatchedPositionsList.add(value) }

    private fun clearCardsMatchedPositionList() { cardsMatchedPositionsList.clear() }

    private fun removeTargetValueFromList(value: Int) { targetValuesList.remove(value) }

    private fun highlightBackgroundOfCardView() {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lighter_grey))
        selectedCardView.isSelected = true
    }

    private fun unHighlightBackgroundOfCardView() {
        selectedCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        selectedCardView.isSelected = false
    }


    private fun changeBackgroundColorOfMatchedCards(cardView: CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_teal))
    }
}