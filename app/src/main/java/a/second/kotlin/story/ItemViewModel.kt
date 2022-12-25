package a.second.kotlin.story

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ItemViewModel : ViewModel() {
    val mutableSelectedItem = MutableLiveData<String>()
    val selectedItem : LiveData<String> get() = mutableSelectedItem

    val mutableCorrectAnswerBoolean = MutableLiveData<Boolean>()
    val isAnswerCorrect : LiveData<Boolean> get() = mutableCorrectAnswerBoolean

    class StatsViewModel : ItemViewModel() {

    }

    class GamesViewModel : ItemViewModel() {
        fun setMathAnswer (string: String) {
            mutableSelectedItem.value = string
        }

        fun getMathAnswer() : String? {
            Log.i("testModel", "string set as ${mutableSelectedItem.value}")
            return mutableSelectedItem.value
        }

        fun setIsAnswerCorrect(isCorrect : Boolean) {
            mutableCorrectAnswerBoolean.value = isCorrect
        }

        fun getIsAnswerCorrect() : Boolean? {
            return mutableCorrectAnswerBoolean.value
        }
    }
}