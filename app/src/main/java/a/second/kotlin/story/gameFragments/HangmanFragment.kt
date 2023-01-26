package a.second.kotlin.story.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.gameFragments.gameData.Hangman
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

class HangmanFragment : Fragment() {

    private lateinit var HangmanData : Hangman
    private lateinit var GallowsData : Hangman.GallowsCanvas
    private val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()
    private lateinit var rootView : View

    private lateinit var keyboardGridView : GridView
    private lateinit var keyboardAdapter : Hangman.KeyboardGridViewAdapter
    private lateinit var puzzleRecyclerView : RecyclerView
    private lateinit var puzzleAdapter : Hangman.PuzzleRecyclerAdapter

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

    private lateinit var hangmanStateOfAnswerTextView : TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_hangman_layout, container, false)

        HangmanData = Hangman()
        GallowsData = rootView.findViewById(R.id.hangman_canvas)

        hangmanStateOfAnswerTextView = rootView.findViewById(R.id.hangman_state_of_answer_textView)

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

        sendGameBeingPlayedToViewModel()

        keyboardGridView.setOnItemClickListener { parent, view, position, id ->
            val letterClicked = HangmanData.alphabetStringArray()[position]
            val letterTextView : TextView = parent[position].findViewById(R.id.hangman_alphabet_letter)

            drawLetterOnBoardOrDrawHangman(letterClicked)

            addLetterToGuessedList(letterClicked)
            removeLetterFromUnguessedList(letterClicked)
            colorSelectedLetter(letterTextView, letterClicked)

            setStateOfAnswersTextView()
            disableKeyboardWhenGameHasEnded()

            sendAnswerStateToViewModel()
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
        fullAlphabetLetterList.addAll(HangmanData.alphabetStringArray())
    }

    private fun populateUnselectedLetterList() {
        listOfLetterNotYetGuessed.addAll(HangmanData.alphabetStringArray())
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
            } else {
                GallowsData.iterateProgress()
                GallowsData.reDrawCanvas()
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

    private fun refreshEntirePuzzleLetterAdapter() {
        puzzleAdapter.notifyDataSetChanged()
    }

    private fun setStateOfAnswersTextView() {
        if (gameHasBeenWon()) {
            hangmanStateOfAnswerTextView.text = getString(R.string.hangman_game_won)
        }
        if (gameHasBeenLost()) {
            hangmanStateOfAnswerTextView.text = getString(R.string.hangman_game_lost)
        }
    }

    private fun disableKeyboardWhenGameHasEnded() {
        if (gameHasBeenWon() || gameHasBeenLost() ) keyboardGridView.isEnabled = false
    }

    private fun gameHasBeenWon() : Boolean {
        return selectedWordLetterListForPuzzle.equals(revealedLetterListOfPuzzleWord)
    }

    private fun gameHasBeenLost() : Boolean {
        return GallowsData.progress > 6
    }

    private fun sendAnswerStateToViewModel() {
        if (gameHasBeenWon()) gamesViewModel.setIsAnswerCorrect(true)
        if (gameHasBeenLost()) gamesViewModel.setIsAnswerCorrect(false)
    }

    private fun sendGameBeingPlayedToViewModel() {
        gamesViewModel.gameBeingPlayed = ("Hangman")
    }

    private fun instantiateKeyboardGridViewAndAdapter() {
        keyboardGridView = rootView.findViewById(R.id.hangman_keyboard_gridView)
        keyboardGridView.numColumns = 9

        keyboardAdapter = Hangman.KeyboardGridViewAdapter(requireContext(), R.layout.hangman_keyboard_adapter_view, R.id.hangman_alphabet_letter, fullAlphabetLetterList)
        keyboardGridView.adapter = keyboardAdapter
    }

    private fun instantiatePuzzleRecyclerView() {
        puzzleRecyclerView = rootView.findViewById(R.id.hangman_puzzle_recyclerView)

        puzzleAdapter = Hangman.PuzzleRecyclerAdapter(revealedLetterListOfPuzzleWord)
        puzzleRecyclerView.adapter = puzzleAdapter
        puzzleRecyclerView.addItemDecoration(puzzleLetterDivider())
        puzzleRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun disableAdapterClicks() { keyboardGridView.isEnabled = false }

    private fun puzzleLetterDivider() : DividerItemDecoration {
        var decoration = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
        decoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.horizontal_divider_blank)!!)
        return decoration
    }
}