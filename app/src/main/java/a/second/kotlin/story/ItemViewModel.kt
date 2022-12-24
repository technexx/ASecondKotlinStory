package a.second.kotlin.story

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<String>()
    val selectedItem : LiveData<String> get() = mutableSelectedItem

    fun setSelectedString (string: String) {
        mutableSelectedItem.value = string
        Log.i("testModel", "string set as $string")
    }
}