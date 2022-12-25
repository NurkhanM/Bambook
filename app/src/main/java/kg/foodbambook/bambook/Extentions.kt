package kg.foodbambook.bambook

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import kg.foodbambook.kg.R
import retrofit2.Response
import java.io.IOException


fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(this, message, duration).apply {
        show()
    }

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(this, resId, duration).apply {
        show()
    }

fun Activity.hideKeyboard() {
    if (this.currentFocus != null) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}

fun Throwable.checkIfIsInternetProblemAndShowMessage(context: Context) {
    if (this is IOException) {
        context.toast(R.string.error_message_bad_internet_connection)
    } else {
        context.toast(R.string.error_message_converting_failed)
    }
}

fun <T> Response<T>.ifSuccessful(func: Response<T>.() -> Unit) {
    if (isSuccessful) func()
}