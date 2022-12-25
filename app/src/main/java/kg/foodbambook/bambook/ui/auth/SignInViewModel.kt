package kg.foodbambook.bambook.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kg.foodbambook.bambook.*
import kg.foodbambook.bambook.model.ErrorResponse
import kg.foodbambook.bambook.model.Token
import kg.foodbambook.bambook.model.version.Version
import kg.foodbambook.bambook.utils.RetrofitErrorUtils
import kg.foodbambook.bambook.utils.ToastUtils
import kg.foodbambook.kg.App
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SignInViewModel() : ViewModel() {

    companion object {
        val TAG = SignInViewModel::class.java.simpleName
    }

    private val state = MutableLiveData<Int>()
    val data = MutableLiveData<Version>(null)

    fun getState(): LiveData<Int> {
        return state
    }

    fun getAppVersion(context: Context): LiveData<Version> {
        if (data.value == null)
            getVersion(context)
        return data
    }


    fun login(context: Context, phone: String, password: String) {
        state.value = Constants.STATE_LOADING
        val phoneRB = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
        val passwordRB = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        App.getClient(context).login(phoneRB, passwordRB).enqueue(object : Callback<Token?> {
            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Log.e(TAG, "onFailure c = $call")
                if (t is IOException) {
                    state.value = Constants.STATE_NO_INTERNET
                } else {
                    state.value = Constants.STATE_AUTH_ERROR
                }
            }

            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                Log.e(TAG, "onResponse ")
                if (!response.isSuccessful) {

                    if (response.errorBody() == null) {
                        ToastUtils.showToast(context, "Unknown Server Error")
                    }
                    val error = RetrofitErrorUtils.parseError(
                        response.errorBody()!!,
                        ErrorResponse::class.java
                    )
                    Log.e(TAG, error.error!![0])
                    ToastUtils.showToast(context, error.error!![0])
                    if (!error.error.isNullOrEmpty())
                        context.toast(error.error!![0])
                    else context.toast(response.errorBody()!!.string())
                    state.value = Constants.STATE_AUTH_ERROR
                    return
                }

                val token = response.body()
                if (token != null) {
                    SaveUserToken.saveToken(token.token, context)
                    state.value = Constants.STATE_SIGNIN_SUCCESS
                    App.resetApi()
                    return
                }

                state.value = Constants.STATE_AUTH_ERROR

            }
        })
    }

    private fun getVersion(context: Context) {
        App.getClient(context)
            .getVersion()
            .enqueue(object : Callback<Version?> {
                override fun onFailure(call: Call<Version?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context)
                }

                override fun onResponse(call: Call<Version?>, response: Response<Version?>) {
                    if (response.isSuccessful) {
                        Log.e("VERSION", response.body().toString())
                        data.value = response.body()
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("errrrr", error.toString())

                    }
                }
            })
    }

}
