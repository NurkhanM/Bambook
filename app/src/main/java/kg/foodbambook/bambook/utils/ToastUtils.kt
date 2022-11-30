package kg.foodbambook.bambook.utils

import android.content.Context

import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtils {

    private var mCurrentToast: Toast? = null

    fun showToast(context: Context, text: String) {

        if (mCurrentToast != null) {
            mCurrentToast!!.cancel()
            mCurrentToast = null
        }
        mCurrentToast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        mCurrentToast!!.show()
    }

    fun showToast(context: Context, @StringRes resId: Int) {

        if (mCurrentToast != null) {
            mCurrentToast!!.cancel()
        }
        mCurrentToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT)
        mCurrentToast!!.show()
    }

    fun showLongToast(context: Context, text: String) {

        if (mCurrentToast != null) {
            mCurrentToast!!.cancel()
        }
        mCurrentToast = Toast.makeText(context, text, Toast.LENGTH_LONG)
        mCurrentToast!!.show()
    }

    fun showLongToast(context: Context, @StringRes resId: Int) {

        if (mCurrentToast != null) {
            mCurrentToast!!.cancel()
        }
        mCurrentToast = Toast.makeText(context, resId, Toast.LENGTH_LONG)
        mCurrentToast!!.show()
    }


}