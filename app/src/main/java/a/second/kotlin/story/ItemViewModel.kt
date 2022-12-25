package a.second.kotlin.story

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ItemViewModel : ViewModel() {
    val mutableCorrectAnswerBoolean = MutableLiveData<Boolean>()
    val isAnswerCorrect : LiveData<Boolean> get() = mutableCorrectAnswerBoolean

    val mutableWhichGameIsBeingPlayed = MutableLiveData<String>()
    val gameBeingPlayed : LiveData<String> get() = mutableWhichGameIsBeingPlayed

    class StatsViewModel : ItemViewModel() {

    }

    class GamesViewModel : ItemViewModel() {
        fun setIsAnswerCorrect(isCorrect : Boolean) {
            mutableCorrectAnswerBoolean.value = isCorrect
        }

        fun getIsAnswerCorrect() : Boolean? {
            return isAnswerCorrect.value
        }

        fun setWhichGameIsBeingPlayed(game: String) {
            mutableWhichGameIsBeingPlayed .value = game
        }

        fun getGameBeingPlayed() : String? {
            return gameBeingPlayed.value
        }
    }
}