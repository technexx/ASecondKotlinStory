package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.Hangman
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.LayoutManager

class HangmanFragment : Fragment() {

    val HangmanClass = Hangman()
    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    lateinit var keyboardGridView : GridView
    lateinit var puzzleRecyclerView : RecyclerView
    lateinit var puzzleAdapter : Hangman.PuzzleRecyclerAdapter
    lateinit var rootView : View

    var normalWordList : ArrayList<String> = ArrayList()
    var hardWordStringList : ArrayList<String> = ArrayList()

    var totalLetterList : ArrayList<String> = ArrayList()
    var unSelectedLetterList : ArrayList<String> = ArrayList()
    var selectedLetterList : ArrayList<String> = ArrayList()

    var puzzleWordBankList : ArrayList<String> = ArrayList()
    var selectedWordLetterListForPuzzle: ArrayList<String> = ArrayList()
    var blankedOutLetterListOfPuzzleWord: ArrayList<String> = ArrayList()

    val NORMAL_WORD = 0
    val HARD_WORD = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_hangman_layout, container, false)

        normalWordList = convertStringListToArrayList(getString(R.string.normal_words_string).split(" "))
        hardWordStringList = convertStringListToArrayList(getString(R.string.hard_words_string).split(" "))

        instantiatePuzzleRecyclerView()
        instantiateKeyboardGridViewAndAdapter()
        populateTotalLetterList()
        populateUnselectedLetterList()

        assignedWordListBasedOnDifficulty(NORMAL_WORD)
        populatePuzzleSelectedWordList(randomWordAsArrayListOfLetters())

        populateBlankedOutLetterListOfPuzzleWord()
        refreshEntirePuzzleLetterAdapter()

        keyboardGridView.setOnItemClickListener { parent, view, position, id ->
            val letterClicked = HangmanClass.alphabetStringArray()[position]
            val letterTextView : TextView = parent.get(position).findViewById(R.id.hangman_alphabet_letter)

            addLetterToSelectedList(letterClicked)
            removeLetterFromUnselectedList(letterClicked)
            colorSelectedLetter(letterTextView, letterClicked)
        }


        return rootView
    }

    private fun assignedWordListBasedOnDifficulty(difficulty: Int) {
        when (difficulty) {
            NORMAL_WORD -> populatePuzzleWordBankList(normalWordList)
            HARD_WORD -> populatePuzzleWordBankList(hardWordStringList)
        }
    }
    private fun populatePuzzleWordBankList(array: ArrayList<String>) {
        puzzleWordBankList.addAll(array)
        Log.i("testList", "word array is " + puzzleWordBankList)
    }

    private fun populatePuzzleSelectedWordList(array: ArrayList<String>) {
        selectedWordLetterListForPuzzle.addAll(array)
        Log.i("testSelect", "selected word list is " + selectedWordLetterListForPuzzle)
    }

    private fun randomWordAsArrayListOfLetters() : ArrayList<String> {
        var listToReturn: ArrayList<String> = ArrayList()

        val wordRoll = (puzzleWordBankList.indices).random()
        val wordSelected : String = puzzleWordBankList.get(wordRoll)
        val stringAsList : List<String> = wordSelected.split("")

        for (i in stringAsList.indices) {
            if (!stringAsList.get(i).equals(""))
                listToReturn.add(stringAsList.get(i))
        }

        return convertStringListToArrayList(stringAsList)
    }

    private fun populateTotalLetterList() {
        totalLetterList.addAll(HangmanClass.alphabetStringArray())
    }

    private fun populateUnselectedLetterList() {
        unSelectedLetterList.addAll(HangmanClass.alphabetStringArray())
    }

    private fun removeLetterFromUnselectedList(letter: String) {
        for (i in totalLetterList) if (unSelectedLetterList.contains(letter)) unSelectedLetterList.remove(letter)
    }

    private fun addLetterToSelectedList(letter: String) {
        for (i in totalLetterList) if (!selectedLetterList.contains(letter)) selectedLetterList.add(letter)
    }

    private fun colorSelectedLetter(textView: TextView, letter: String) {
        for (i in totalLetterList) if (!unSelectedLetterList.contains(letter)) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
        }
    }

    private fun doesSelectedLetterExistInWord(letter: String) : Boolean {
        return selectedWordLetterListForPuzzle.contains(letter)
    }

    private fun convertStringListToArrayList(list: List<String>) : ArrayList<String> {
        val arrayListToReturn : ArrayList<String> = ArrayList()
        arrayListToReturn.addAll(list)
        return arrayListToReturn
    }

    private fun instantiateKeyboardGridViewAndAdapter() {
        keyboardGridView = rootView.findViewById(R.id.hangman_keyboard_gridView)
        keyboardGridView.numColumns = 9

        val keyboardAdapter : Hangman.KeyboardGridViewAdapter = Hangman.KeyboardGridViewAdapter(requireContext(), R.layout.hangman_keyboard_adapter_view, R.id.hangman_alphabet_letter, totalLetterList)
        keyboardGridView.adapter = keyboardAdapter
    }

    private fun instantiatePuzzleRecyclerView() {
        puzzleRecyclerView = rootView.findViewById(R.id.hangman_puzzle_recyclerView)

        puzzleAdapter = Hangman.PuzzleRecyclerAdapter(blankedOutLetterListOfPuzzleWord)
        puzzleRecyclerView.adapter = puzzleAdapter
        puzzleRecyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    }

    private fun populateBlankedOutLetterListOfPuzzleWord() {
        for (i in 0..selectedWordLetterListForPuzzle.size) {
            blankedOutLetterListOfPuzzleWord.add("\uFF3F")
        }
    }
    private fun refreshSinglePositionOfPuzzleLetterAdapter(position : Int) {
        puzzleAdapter.notifyItemChanged(position)
    }

    private fun refreshEntirePuzzleLetterAdapter() {
        puzzleAdapter.notifyDataSetChanged()
    }

    private fun letterListLogs() {
        Log.i("testList", "total list is $totalLetterList")
        Log.i("testList", "unSelected list is $unSelectedLetterList")
        Log.i("testList", "selected list is $selectedLetterList")
    }
}