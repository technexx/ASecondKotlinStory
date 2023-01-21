package a.second.kotlin.story

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ItemViewModel : ViewModel() {
    //Only object that's tied to observer. Other objects are simple set and retrieved once a correct or incorrect answer is sent.
    val mutableCorrectAnswerBoolean = MutableLiveData<Boolean>()
    val isAnswerCorrect : LiveData<Boolean> get() = mutableCorrectAnswerBoolean

    val mutableTypeOfEventTriggered = MutableLiveData<Int>()
    val typeOfEventTriggered : LiveData<Int> get() = mutableTypeOfEventTriggered

    var gameBeingPlayed : String = ""
    var switchGameFragments = false

    class StatsViewModel : ItemViewModel() {

    }

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