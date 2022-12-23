package a.second.kotlin.story.games

import a.second.kotlin.story.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Hangman {

    class LetterHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val letterView : TextView = itemView.findViewById(R.id.hangman_letter)
    }

    class KeyboardRecyclerAdapter() : RecyclerView.Adapter<LetterHolder>() {
        val alphabetArray : Array<String> = alphabetStringArray()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val letterItem = LayoutInflater.from(parent.context,).inflate(R.layout.hangman_keyboard_adapter_views, parent, false)
            return LetterHolder(letterItem)
        }

        override fun onBindViewHolder(holder: LetterHolder, position: Int) {
            var letter = alphabetArray[position]
        }

        override fun getItemCount(): Int {
            return alphabetArray.size
        }

        fun alphabetStringArray(): Array<String> {
            val alphabet =
                "A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z"
            return alphabet.split(", ").toTypedArray()
        }
    }

    class AlphabetConversions{
        fun convertPositionToLetter(position: Int): String {
            var letter = ""
            if (position == 0) letter = "A"
            if (position == 1) letter = "B"
            if (position == 2) letter = "C"
            if (position == 3) letter = "D"
            if (position == 4) letter = "E"
            if (position == 5) letter = "F"
            if (position == 6) letter = "G"
            if (position == 7) letter = "H"
            if (position == 8) letter = "I"
            if (position == 9) letter = "J"
            if (position == 10) letter = "K"
            if (position == 11) letter = "L"
            if (position == 12) letter = "M"
            if (position == 13) letter = "N"
            if (position == 14) letter = "O"
            if (position == 15) letter = "P"
            if (position == 16) letter = "Q"
            if (position == 17) letter = "R"
            if (position == 18) letter = "S"
            if (position == 19) letter = "T"
            if (position == 20) letter = "U"
            if (position == 21) letter = "V"
            if (position == 22) letter = "W"
            if (position == 23) letter = "X"
            if (position == 24) letter = "Y"
            if (position == 25) letter = "Z"
            return letter
        }
    }

}