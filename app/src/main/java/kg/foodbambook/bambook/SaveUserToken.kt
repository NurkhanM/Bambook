package kg.foodbambook.bambook

import android.content.Context
import android.content.SharedPreferences


object SaveUserToken {

    val APP_PREFERENCES = "mysettings"


    private var mSettings: SharedPreferences? = null

    fun saveToken(Token: String, context: Context) {

        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        val editor = mSettings!!.edit()
        editor.putString("Token", "token $Token")
        editor.apply()
    }

    fun getToken(context: Context): String? {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return mSettings!!.getString("Token", "empty")
    }

    fun clearToken(context: Context) {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mSettings!!.edit().clear()
        editor.apply()

    }

    fun hasToken(context: Context): Boolean {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val token = mSettings!!.getString("Token", "empty")
        return token != "empty" && token != ""
    }
}
