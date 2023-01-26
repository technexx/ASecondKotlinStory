package a.second.kotlin.story.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MathSumsFragment : Fragment(), SumsCustomAdapter.AdapterData {

    override fun gameIsWon() {
        pauseObjectAnimator()
        endOfGameFunctions(true)
    }

    fun disableAdapterClicks() { sumsCustomAdapter.disableClicksIfGameOver() }

    private lateinit var rootView : View
    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    private lateinit var sumsGridView : GridView
    private lateinit var sumsCustomAdapter : SumsCustomAdapter

    private lateinit var timerProgressBar : ProgressBar
    private lateinit var objectAnimator : ObjectAnimator
    private var progressValue = 0

    private var fullCardIntegerList : ArrayList<Int> = ArrayList()
    private var unMatchedCardsRemainingList : ArrayList<Int> = ArrayList()
    private var currentIntegerTarget = 0

    private lateinit var targetAnswerTextView : TextView
    private lateinit var stateOfAnswerTextView : TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_sums_layout, container, false)

        instantiateXMLObjects()
        populateCardIntegerLists()
        instantiateSumsGridViewAndAdapter()
        populateNextTargetValue()
        setTargetAnswerTextView(currentIntegerTarget)

        instantiateProgressBar()
        instantiateObjectAnimator()
        setEndOfObjectAnimatorListener()
        startObjectAnimator()

        return rootView
    }

    private fun populateCardIntegerLists() {
        while (fullCardIntegerList.size < 16) {
            val numberToAdd = (1..10).random()
            fullCardIntegerList.add(numberToAdd)
            unMatchedCardsRemainingList.add(numberToAdd)
        }
    }

    override fun targetNumberHit(listOfMatchedNumbers: ArrayList<Int>) {
        removedMatchedCardsFromUnmatchedCardsList(listOfMatchedNumbers)
        populateNextTargetValue()
        setTargetAnswerTextView(currentIntegerTarget)
    }

    private fun removedMatchedCardsFromUnmatchedCardsList(matchedCardsList: List<Int>) {
        for (i in matchedCardsList.indices) {
            unMatchedCardsRemainingList.remove(matchedCardsList[i])
        }
    }

    private fun populateNextTargetValue() {
        var valueToAdd = 0
        var numberOfCardsToAdd: Int
        var maxCardsToAdd = 0

        if (unMatchedCardsRemainingList.size > 6) maxCardsToAdd = (2..5).random() else maxCardsToAdd = (2..3).random()
        numberOfCardsToAdd = (2..maxCardsToAdd).random()

        if (unMatchedCardsRemainingList.size < 5) {
            numberOfCardsToAdd = unMatchedCardsRemainingList.size
        }

        //"Until" excludes last number.
        for (i in 0 until numberOfCardsToAdd) {
            valueToAdd += unMatchedCardsRemainingList[i]
        }

        currentIntegerTarget = valueToAdd
        sumsCustomAdapter.updateIntegerTarget(valueToAdd)
    }

    private fun setTargetAnswerTextView(targetValue: Int) { targetAnswerTextView.text = getString(R.string.sums_target_textView, targetValue.toString()) }

    private fun toggleStateOfAnswersAndTargetValueTextViews(gameOver: Boolean) {
        if (gameOver) {
            stateOfAnswerTextView.visibility = View.VISIBLE
            targetAnswerTextView.visibility = View.GONE
        } else {
            stateOfAnswerTextView.visibility = View.GONE
            targetAnswerTextView.visibility = View.VISIBLE
        }
    }

    private fun instantiateProgressBar() {
        timerProgressBar = rootView.findViewById(R.id.sums_cards_timer_progress_bar)
        progressValue = 1000
        timerProgressBar.max = progressValue
    }

    private fun instantiateObjectAnimator() {
        objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", progressValue, 0)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 15000
    }

    private fun startObjectAnimator() {
        objectAnimator.start()
    }

    fun pauseObjectAnimator() {
        objectAnimator.pause()
    }

    //Cancelling animator occurs when all sums are matched before animation duration ends.
    private fun setEndOfObjectAnimatorListener() {
        objectAnimator.doOnEnd {
            endOfGameFunctions(false)
        }
    }

    private fun endOfGameFunctions(gameHasBeenWon : Boolean) {
        gamesViewModel.gameBeingPlayed = ("Sums")
        toggleStateOfAnswersAndTargetValueTextViews(true)

        sendEndGameWinOrLoss(gameHasBeenWon)
        setStateOfAnswersTextView(gameHasBeenWon)
    }

    private fun sendEndGameWinOrLoss(gameIsWon: Boolean) {
        gamesViewModel.setIsAnswerCorrect(gameIsWon)
    }

    private fun setStateOfAnswersTextView(gameIsWon: Boolean) {
        if (gameIsWon) stateOfAnswerTextView.text = getString(R.string.sums_game_won) else stateOfAnswerTextView.text = getString(R.string.sums_game_lost)
    }

    private fun instantiateSumsGridViewAndAdapter() {
        sumsCustomAdapter = SumsCustomAdapter(requireContext(), R.layout.sums_adapter_views, fullCardIntegerList, this)
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
class SumsCustomAdapter (context: Context, resource: Int, val fullCardIntegerList: ArrayList<Int>, val adapterData: AdapterData
): ArrayAdapter<String>(context, resource) {

    lateinit var populatedCardTextView : TextView
    lateinit var selectedCardView : CardView
    lateinit var selectedCardTextView : TextView

    var cardSelectedPositionsList : ArrayList<Int> = ArrayList()
    var cardsMatchedPositionsList : ArrayList<Int> = ArrayList()

    var currentIntegerTarget = 0
    var totalSelectedCardsValue = 0

    var cardsMatchedValuesList : ArrayList<Int> = ArrayList()

    var rowView = View(context)

    fun disableClicksIfGameOver() {
        rowView.isClickable = false
        rowView.isEnabled = false
    }

    interface AdapterData {
        fun targetNumberHit(listOfMatchedNumbers : ArrayList<Int>)
        fun gameIsWon()
    }

    fun updateIntegerTarget(target: Int) { currentIntegerTarget = target }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        rowView = inflater.inflate(R.layout.sums_adapter_views, null, true)

        populatedCardTextView = rowView.findViewById(R.id.sums_card_textView)
        populatedCardTextView.text = fullCardIntegerList[position].toString()

        disableClicksIfGameOver()

        rowView.setOnClickListener {
            if (!cardsMatchedPositionsList.contains(position)) {
                selectedCardView = parent[position].findViewById(R.id.sums_card_cardView)
                selectedCardTextView = parent[position].findViewById(R.id.sums_card_textView)

                if (!isCardHighlighted(selectedCardView)) {
                    highlightBackgroundOfCardView()
                    addToCardSelectedPositionList(position)
                    addToSelectedCardsValue(fullCardIntegerList.get(position))
                    addToCardsMatchedValuesList(fullCardIntegerList.get(position))
                } else {
                    unHighlightBackgroundOfCardView()
                    removeFromCardSelectedPositionList(position)

                    subtractFromCardsSelectedValue(fullCardIntegerList.get(position))
                    subtractFromCardsMatchedValuesList(fullCardIntegerList.get(position))
                }

                if (totalSelectedCardsValue == currentIntegerTarget) {
                    for (i in cardSelectedPositionsList.indices) {
                        val cardView = parent[cardSelectedPositionsList[i]].findViewById(R.id.sums_card_cardView) as CardView
                        changeBackgroundColorOfMatchedCards(cardView)
                        addToCardsMatchedPositionList(cardSelectedPositionsList[i])
                    }
                    zeroOutTotalCardsSelectedValue()
                    clearTotalSelectedCardsPositionList()

                    if (cardsMatchedPositionsList.size < 16) {
                        adapterData.targetNumberHit(cardsMatchedValuesList)
                    } else {
                        adapterData.gameIsWon()
                    }

                    clearCardsMatchedValuesList()
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

    private fun addToSelectedCardsValue(value: Int) { totalSelectedCardsValue += value }

    private fun subtractFromCardsSelectedValue(value: Int) { totalSelectedCardsValue -= value }

    private fun zeroOutTotalCardsSelectedValue() { totalSelectedCardsValue = 0 }

    private fun addToCardsMatchedPositionList(value: Int) { cardsMatchedPositionsList.add(value) }


    private fun addToCardsMatchedValuesList(value: Int) { cardsMatchedValuesList.add(value) }

    private fun subtractFromCardsMatchedValuesList(value: Int) { cardsMatchedValuesList.remove(value) }

    private fun clearCardsMatchedValuesList() { cardsMatchedValuesList.clear() }

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