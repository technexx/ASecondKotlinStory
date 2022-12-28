package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.Hangman
import a.second.kotlin.story.games.MathProblems
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HangmanFragment : Fragment() {

    val HangmanClass = Hangman()
    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    lateinit var keyboardGridView : GridView
    lateinit var rootView : View

    var easyWordStringList : List<String> = ArrayList()
    var mediumWordStringList : List<String> = ArrayList()
    var hardWordStringList : List<String> = ArrayList()

    var totalLetterList : ArrayList<String> = ArrayList()
    var unSelectedLetterList : ArrayList<String> = ArrayList()
    var selectedLetterList : ArrayList<String> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_hangman_layout, container, false)

        easyWordStringList = getString(R.string.easy_words_string).split(" ")
        mediumWordStringList = getString(R.string.medium_words_string).split(" ")
        hardWordStringList = getString(R.string.hard_words_string).split(" ")

        populateTotalLetterList()
        populateUnselectedLetterList()

        keyboardGridView = rootView.findViewById(R.id.hangman_keyboard_gridView)
        keyboardGridView.numColumns = 9

        val keyboardAdapter : Hangman.KeyboardRecyclerAdapter = Hangman.KeyboardRecyclerAdapter(requireContext(), R.layout.hangman_keyboard_adapter_views, R.id.hangman_letter, totalLetterList)

        keyboardGridView.adapter = keyboardAdapter

        keyboardGridView.setOnItemClickListener { parent, view, position, id ->
            addLetterToSelectedList(HangmanClass.alphabetStringArray()[position])
            removeLetterFromUnselectedList(HangmanClass.alphabetStringArray()[position])

            val letterTextView : TextView = parent.get(position).findViewById(R.id.hangman_letter)
            letterTextView.text = "BOO"

            Log.i("testList", "total list is $totalLetterList")
            Log.i("testList", "unSelected list is $unSelectedLetterList")
            Log.i("testList", "selected list is $selectedLetterList")
        }


        return rootView
    }

    fun populateTotalLetterList() {
        totalLetterList.addAll(HangmanClass.alphabetStringArray())
    }

    fun populateUnselectedLetterList() {
        unSelectedLetterList.addAll(HangmanClass.alphabetStringArray())
    }

    fun removeLetterFromUnselectedList(letter: String) {
        for (i in totalLetterList) if (unSelectedLetterList.contains(letter)) unSelectedLetterList.remove(letter)
    }

    fun addLetterToSelectedList(letter: String) {
        for (i in totalLetterList) if (!selectedLetterList.contains(letter)) selectedLetterList.add(letter)
    }

//    fun greyOutSelectedLetter(letter: String, position: Int) {
//        for (i in totalLetterList) if (!selectedLetterList.contains(letter))
//    }
}