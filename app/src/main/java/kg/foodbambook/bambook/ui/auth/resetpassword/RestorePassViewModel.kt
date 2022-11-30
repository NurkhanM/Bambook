package kg.foodbambook.bambook.ui.auth.resetpassword

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kg.foodbambook.bambook.*
import kg.foodbambook.bambook.model.NumberCheck
import kg.foodbambook.bambook.model.RestoredPasswordRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

private val TAG = RestorePassViewModel::class.java.simpleName
class RestorePassViewModel: ViewModel() {

    private val state = MutableLiveData<Int>()
    private val accountIsExist = MutableLiveData<Boolean>(true)

    fun getState(): LiveData<Int> {
        return state
    }


    private var storedVerificationId: String? = ""
    private var auth = FirebaseAuth.getInstance()

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var mCallBacks  = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            Log.e(TAG, "onVerificationCompleted:$credential")

            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {

            Log.e(TAG, "onVerificationFailed", e)
            state.value = Constants.STATE_FAILED
            if (e is FirebaseAuthInvalidCredentialsException) {

            } else if (e is FirebaseTooManyRequestsException) {

            }

        }

        override fun onCodeSent(
            verificationId: String?,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.e(TAG, "onCodeSent")
            storedVerificationId = verificationId
            resendToken = token
            state.value = Constants.STATE_CODE_SENT
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val a = auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("SUCCESS",task.result.toString())
                    state.value = Constants.STATE_NUMBER_VERIFIED
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.e("UNSUCCESS",task.exception.toString())
                        state.value = Constants.STATE_INCORRECT_CODE
                    }
                }
            }


    }

    fun startPhoneNumberVerification(activity: Activity, phoneNumber: String) {

        Log.e(TAG, phoneNumber)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            activity,
            mCallBacks
        )

        state.value = Constants.STATE_LOADING
    }

    fun verifyPhoneNumberWithCode(code: String) {

        state.value = Constants.STATE_LOADING
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    fun resendVerificationCode(
        activity: Activity,
        phoneNumber: String

    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            activity, // Activity (for callback binding)
            mCallBacks, // OnVerificationStateChangedCallbacks
            resendToken) // ForceResendingToken from callbacks

        state.value = Constants.STATE_LOADING
    }

    fun signOut(){
        auth.signOut()
    }


    fun checkNumber(activity: Activity, phone: String) {
        state.value = Constants.STATE_LOADING
        App.getClient(activity)
            .checkNumber(phone)
            .enqueue(object: Callback<NumberCheck?> {
                override fun onFailure(call: Call<NumberCheck?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(activity)
                    state.value = Constants.STATE_NO_INTERNET
                }

                override fun onResponse(call: Call<NumberCheck?>, response: Response<NumberCheck?>) {
                    if (response.isSuccessful) {

                        val result = response.body()
                        accountIsExist.value = result!!.success
                        if (!result.success) {
//                            activity.toast(result.message.toString())
                            state.value = Constants.STATE_NUMBER_ALREADY_REGISTERED

                        } else {
                            startPhoneNumberVerification(activity, phone)
                        }

                    } else {
                        activity.toast(response.errorBody().toString())
                        state.value = Constants.STATE_NUMBER_ALREADY_REGISTERED
                    }

                }
            })
    }


    fun sendNewPassword(context: Context, password1: String, password2: String, phoneNumber: String) {
        state.value = Constants.STATE_LOADING
        val newCredentials = RestoredPasswordRequestBody(password1, password2, phoneNumber)
        App.getClient(context)
            .restorePassword(newCredentials)
            .enqueue(object: Callback<Any?> {
                override fun onFailure(call: Call<Any?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context)
                    state.value = Constants.STATE_NO_INTERNET
                }

                override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                    if (response.isSuccessful) {
                        state.value = Constants.STATE_SUCCESS
                        try {

                            val jsonObject = JSONObject(response.body().toString())
                            val message = jsonObject.getString("message")
                            context.toast(message)
                        } catch (e: Exception) {
                            context.toast("Пароль успешно обновлен")
                            Log.e(TAG, e.message.toString())
                            Log.e(TAG, e.localizedMessage)
                            Log.e(TAG, e.toString())
                        }
                    } else {
                        context.toast(response.errorBody()!!.string())
                    }
                }
            })
    }
}