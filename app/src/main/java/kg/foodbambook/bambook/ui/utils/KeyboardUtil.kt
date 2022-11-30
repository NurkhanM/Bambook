package kg.foodbambook.bambook.ui.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull

class KeyboardUtil{
    companion object {
        fun hideKeyboard( activity: Activity) {
            if (activity.currentFocus != null) {
                val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }
}