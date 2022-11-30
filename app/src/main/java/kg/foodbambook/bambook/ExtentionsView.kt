package kg.foodbambook.bambook

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.hide(hide: Boolean) {
    if (hide) gone() else visible()
}

fun View.show(show: Boolean = true) {
    if (show) visible() else gone()
}

fun EditText.validate(
    validator: (String) -> Boolean,
    message: String
): Boolean {
    val flag = validator(this.text.toString())
    this.error = if (flag) null else message

    return flag
}
fun EditText.cursorToEnd(
) {
    setSelection(text.toString().length)
}