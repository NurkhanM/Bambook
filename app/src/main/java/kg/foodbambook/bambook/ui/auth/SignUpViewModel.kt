package kg.foodbambook.bambook.ui.auth

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
import kg.foodbambook.bambook.model.ErrorSignUp
import kg.foodbambook.bambook.model.NumberCheck
import kg.foodbambook.bambook.model.Token
import kg.foodbambook.bambook.utils.RetrofitErrorUtils
import kg.foodbambook.bambook.utils.ToastUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SignUpViewModel : ViewModel() {

    private val TAG = this::class.java.simpleName

    private val state = MutableLiveData<Int>()

    fun getState(): LiveData<Int> {
        return state
    }

    private var phoneNumber = ""
    private var password = ""
    private var context: Context? = null

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
            state.value = Constants.STATE_AUTH_ERROR
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
                    Log.e(TAG, "signed in successfully")
                    createAccount()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        state.value = Constants.STATE_AUTH_ERROR
                    }
                }
            }

    }
     fun startPhoneNumberVerification( activity: Activity, phoneNumber: String, password: String) {

         context = activity
         this.phoneNumber = phoneNumber
         this.password = password
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
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            activity, // Activity (for callback binding)
            mCallBacks, // OnVerificationStateChangedCallbacks
            resendToken) // ForceResendingToken from callbacks
    }

    fun signOut(){
        auth.signOut()
    }

    private fun createAccount() {

        val phone = RequestBody.create("text/plain".toMediaTypeOrNull(), phoneNumber)
        val pass = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        App.getClient(context!!).signUp(phone, pass).enqueue(object: Callback<Token?> {
            override fun onFailure(call: Call<Token?>, t: Throwable) {
                state.value = Constants.STATE_NO_INTERNET
            }

            override fun onResponse(call: Call<Token?>, response: Response<Token?>){
                Log.e(TAG, response.body().toString())
                Log.e(TAG, response.errorBody().toString())
                Log.e(TAG, response.message().toString())

                if (!response.isSuccessful) {
                    state.value = Constants.STATE_AUTH_ERROR
                    if (response.errorBody() != null) {
                        val errorBody = RetrofitErrorUtils.parseError(response.errorBody()!!, ErrorSignUp::class.java)

                        try {

                            ToastUtils.showToast(context!!, errorBody.error!![0])
                        } catch (e: IndexOutOfBoundsException) {
                            return
                        }
                    }
                    return
                }

                val token  = response.body()
                token?.let {
                    SaveUserToken.saveToken(it.token, context!!)
                    state.value = Constants.STATE_SIGNIN_SUCCESS
                    App.resetApi()

                }


            }
        })
    }

    fun checkNumber(activity: Activity, phoneNumber: String, password: String) {
        state.value = Constants.STATE_LOADING
        App.getClient(activity)
            .checkNumber(phoneNumber)
            .enqueue(object: Callback<NumberCheck?> {
                override fun onFailure(call: Call<NumberCheck?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(activity)
                    state.value = Constants.STATE_NO_INTERNET
                }

                override fun onResponse(call: Call<NumberCheck?>, response: Response<NumberCheck?>) {
                    state.value = Constants.STATE_STOP_LOADING
                    if (response.isSuccessful) {

                        val result = response.body()
                        if (result != null) {
                            if (!result.success) {
                    //                            activity.toast(result.message.toString())
                                startPhoneNumberVerification(activity, phoneNumber,password)

                            } else {
                                state.value = Constants.STATE_NUMBER_ALREADY_REGISTERED
                            }
                        }

                    } else {
                        activity.toast(response.errorBody().toString())
                        state.value = Constants.STATE_NUMBER_ALREADY_REGISTERED
                    }

                }
            })
    }
}
