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
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL

class HangmanFragment : Fragment() {

    var HangmanClass = Hangman()
    lateinit var GallowsClass : Hangman.GallowsCanvas
    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    private lateinit var keyboardGridView : GridView
    private lateinit var puzzleRecyclerView : RecyclerView
    private lateinit var puzzleAdapter : Hangman.PuzzleRecyclerAdapter
    private lateinit var rootView : View

    private var normalWordList : ArrayList<String> = ArrayList()
    private var hardWordStringList : ArrayList<String> = ArrayList()

    private var fullAlphabetLetterList : ArrayList<String> = ArrayList()
    private var listOfLetterNotYetGuessed : ArrayList<String> = ArrayList()
    private var listOfLettersGuessed : ArrayList<String> = ArrayList()

    private var puzzleWordBankList : ArrayList<String> = ArrayList()
    private var selectedWordLetterListForPuzzle: ArrayList<String> = ArrayList()
    private var revealedLetterListOfPuzzleWord: ArrayList<String> = ArrayList()

    private val NORMAL_WORD = 0
    private val HARD_WORD = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_hangman_layout, container, false)

        HangmanClass = Hangman()
//        GallowsClass = Hangman.GallowsCanvas(requireContext(), null)
        GallowsClass = rootView.findViewById(R.id.hangman_canvas)

        normalWordList = convertStringListToArrayList(getString(R.string.normal_words_string).split(" "))
        hardWordStringList = convertStringListToArrayList(getString(R.string.hard_words_string).split(" "))

        instantiatePuzzleRecyclerView()
        instantiateKeyboardGridViewAndAdapter()
        populateFullAlphabetLetterList()
        populateUnselectedLetterList()

        assignedWordListBasedOnDifficulty(NORMAL_WORD)
        populatePuzzleSelectedWordList(randomWordAsArrayListOfLetters())

        populateRevealedLetterListOfPuzzleWordWithBlanks()
        refreshEntirePuzzleLetterAdapter()

        keyboardGridView.setOnItemClickListener { parent, view, position, id ->
            val letterClicked = HangmanClass.alphabetStringArray()[position]
            val letterTextView : TextView = parent[position].findViewById(R.id.hangman_alphabet_letter)

            drawLetterOnBoardOrDrawHangman(letterClicked)

            addLetterToGuessedList(letterClicked)
            removeLetterFromUnguessedList(letterClicked)
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
    }

    private fun populatePuzzleSelectedWordList(array: ArrayList<String>) {
        selectedWordLetterListForPuzzle.addAll(array)
        Log.i("testSelect", "selected word list after copied to array is " + selectedWordLetterListForPuzzle)
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

        return convertStringListToArrayList(listToReturn)
    }

    private fun convertStringListToArrayList(list: List<String>) : ArrayList<String> {
        val arrayListToReturn : ArrayList<String> = ArrayList()
        arrayListToReturn.addAll(list)
        return arrayListToReturn
    }

    private fun populateFullAlphabetLetterList() {
        fullAlphabetLetterList.addAll(HangmanClass.alphabetStringArray())
    }

    private fun populateUnselectedLetterList() {
        listOfLetterNotYetGuessed.addAll(HangmanClass.alphabetStringArray())
    }

    ////////////////////////////////////////////////////////////////////////////////

    private fun removeLetterFromUnguessedList(letter: String) {
        if (listOfLetterNotYetGuessed.contains(letter)) listOfLetterNotYetGuessed.remove(letter)
    }

    private fun addLetterToGuessedList(letter: String) {
        if (!listOfLettersGuessed.contains(letter)) {
            listOfLettersGuessed.add(letter)
        }
    }

    private fun colorSelectedLetter(textView: TextView, letter: String) {
        if (listOfLettersGuessed.contains(letter)) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
        }
    }

    private fun drawLetterOnBoardOrDrawHangman(letter: String) {
        if (!hasLetterBeenSelectedBefore(letter)) {
            if (doesSelectedLetterExistInWord(letter)) {
                replaceBlankWithLetterInRevealedLetterList(letter)
                refreshEntirePuzzleLetterAdapter()
                Log.i("testLetter", "letter $letter exists")
            } else {
                GallowsClass.iterateProgress()
                GallowsClass.reDrawCanvas()
                Log.i("testLetter", "letter $letter does NOT exist")
            }
        }
    }

    private fun hasLetterBeenSelectedBefore(letter: String) : Boolean {
        return listOfLettersGuessed.contains(letter)
    }

    private fun doesSelectedLetterExistInWord(letter: String) : Boolean {
        return selectedWordLetterListForPuzzle.contains(letter)
    }

    private fun replaceBlankWithLetterInRevealedLetterList(letter: String) {
        for (i in selectedWordLetterListForPuzzle.indices) if (selectedWordLetterListForPuzzle[i] == letter) revealedLetterListOfPuzzleWord[i] =
            letter
    }

    private fun populateRevealedLetterListOfPuzzleWordWithBlanks() {
        for (i in 0 until selectedWordLetterListForPuzzle.size) {
            revealedLetterListOfPuzzleWord.add("\uFF3F")
        }
    }

    private fun refreshSinglePositionOfPuzzleLetterAdapter(position : Int) {
        puzzleAdapter.notifyItemChanged(position)
    }

    private fun refreshEntirePuzzleLetterAdapter() {
        puzzleAdapter.notifyDataSetChanged()
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private fun instantiateKeyboardGridViewAndAdapter() {
        keyboardGridView = rootView.findViewById(R.id.hangman_keyboard_gridView)
        keyboardGridView.numColumns = 9

        val keyboardAdapter : Hangman.KeyboardGridViewAdapter = Hangman.KeyboardGridViewAdapter(requireContext(), R.layout.hangman_keyboard_adapter_view, R.id.hangman_alphabet_letter, fullAlphabetLetterList)
        keyboardGridView.adapter = keyboardAdapter
    }

    private fun instantiatePuzzleRecyclerView() {
        puzzleRecyclerView = rootView.findViewById(R.id.hangman_puzzle_recyclerView)

        puzzleAdapter = Hangman.PuzzleRecyclerAdapter(revealedLetterListOfPuzzleWord)
        puzzleRecyclerView.adapter = puzzleAdapter
        puzzleRecyclerView.addItemDecoration(puzzleLetterDivider())
        puzzleRecyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    }

    private fun puzzleLetterDivider() : DividerItemDecoration {
        var decoration = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
        decoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.horizontal_divider_blank)!!)
        return decoration
    }

    private fun letterListLogs() {
        Log.i("testList", "total list is $fullAlphabetLetterList")
        Log.i("testList", "unSelected list is $listOfLetterNotYetGuessed")
        Log.i("testList", "selected list is $listOfLettersGuessed")
    }
}