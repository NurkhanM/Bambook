package kg.docplus.fcm

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult

import java.io.IOException

object FCMTokenUtils {


    fun deleteToken(context: Context) {
        try {
            val originalToken = getTokenFromPrefs(context)
            Log.d("FCMToken", "Token before deletion: $originalToken")
            FirebaseInstanceId.getInstance().deleteInstanceId()

            // Clear current saved token
            saveTokenToPrefs("", context)
            // Now manually call onTokenRefresh()
            Log.d("FCMToken", "Getting new token")
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                val newToken = task.result!!.id
                saveTokenToPrefs(newToken, context)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun saveTokenToPrefs(_token: String, context: Context) {
        // Access Shared Preferences
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        // Save to SharedPreferences
        editor.putString("registration_id", _token)
        editor.apply()
    }

    fun getTokenFromPrefs(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        var regId = preferences.getString("registration_id", null)

        if (regId == null || regId.isEmpty()) {
            regId = FirebaseInstanceId.getInstance().token
        }
        return regId!!
    }
}
