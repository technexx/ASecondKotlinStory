package a.second.kotlin.story

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ItemViewModel : ViewModel() {
    val mutableSelectedItem = MutableLiveData<String>()
    val selectedItem : LiveData<String> get() = mutableSelectedItem

    class MathViewModel() : ItemViewModel() {
        fun setSelectedString (string: String) {
            mutableSelectedItem.value = string
        }

        fun getSelectedString() : String? {
            Log.i("testModel", "string set as ${mutableSelectedItem.value}")
            return mutableSelectedItem.value
        }
    }
}