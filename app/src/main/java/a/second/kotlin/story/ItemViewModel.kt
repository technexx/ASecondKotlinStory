package a.second.kotlin.story

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ItemViewModel : ViewModel() {
    val mutableCorrectAnswerBoolean = MutableLiveData<Boolean>()
    val isAnswerCorrect : LiveData<Boolean> get() = mutableCorrectAnswerBoolean

    val mutableTypeOfEventTriggered = MutableLiveData<Int>()
    val typeOfEventTriggered : LiveData<Int> get() = mutableTypeOfEventTriggered

    val mutableHighScore = MutableLiveData<Long>()
    val highScore : LiveData<Long> get() = mutableHighScore

    var gameBeingPlayed : String = ""

    class GamesViewModel : ItemViewModel() {
        fun setIsAnswerCorrect(isCorrect : Boolean) {
            mutableCorrectAnswerBoolean.value = isCorrect
        }

        fun getIsAnswerCorrect() : Boolean? {
            return isAnswerCorrect.value
        }

        fun getWhichIsGameBeingPlayed() : String {
            return gameBeingPlayed
        }

        fun setTypeOfEventTriggered(event: Int) {
            mutableTypeOfEventTriggered.value = event
        }
    }
}