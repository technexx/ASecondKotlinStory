package a.second.kotlin.story.games.gameFragments

import a.second.kotlin.story.ItemViewModel
import a.second.kotlin.story.R
import a.second.kotlin.story.games.Hangman
import a.second.kotlin.story.games.MathProblems
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HangmanFragment : Fragment() {

    val gamesViewModel : ItemViewModel.GamesViewModel by activityViewModels()

    lateinit var keyboardRecycler : RecyclerView

    lateinit var rootView : View

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_hangman_layout, container, false)

        keyboardRecycler = rootView.findViewById(R.id.hangman_keyboard_recyclerView)
        keyboardRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val keyboardAdapter : Hangman.KeyboardRecyclerAdapter = Hangman.KeyboardRecyclerAdapter()

        keyboardRecycler.adapter = keyboardAdapter


        return rootView
    }
}