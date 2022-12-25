package kg.foodbambook.bambook.ui.main.profile

import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kg.foodbambook.kg.App
import kg.foodbambook.bambook.SaveUserToken
import kg.foodbambook.bambook.checkIfIsInternetProblemAndShowMessage
import kg.foodbambook.bambook.model.ChangePasswordCredentials
import kg.foodbambook.bambook.model.User
import kg.foodbambook.bambook.model.UserDto
import kg.foodbambook.bambook.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private val TAG = ProfileViewModel::class.java.simpleName
class ProfileViewModel(application: Application) : AndroidViewModel(application) {


    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val user = MutableLiveData<User>(null)

    val progress: MutableLiveData<Boolean> = MutableLiveData(false)


    fun getUser() : LiveData<User> {
        if (user.value == null)
            getProfileInfo(getApplication())
        return user
    }


    fun signOut(context: Context){
        SaveUserToken.clearToken(context)
        mAuth.signOut()
        App.resetApi()
    }

    private fun getProfileInfo(context: Context){
        App.getClient(context)
            .getProfileInfo()
            .enqueue(object: Callback<User?> {
                override fun onFailure(call: Call<User?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context)
                }

                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    if (response.isSuccessful) {
                        user.value = response.body()
                    } else {
                        context.toast("Произошла ошибка")
                    }
                }
            })
    }


    fun updateProfile(context: Context, user: UserDto) {
        App.getClient(context)
            .updateProfile(user)
            .enqueue(object: Callback<User?> {
                override fun onFailure(call: Call<User?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context)
                }

                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    if (response.isSuccessful) {
                        context.toast("Профиль обновлен")
                    } else {
                        context.toast("Не удалось обновить профиль")

                    }
                }
            })
    }

    fun changePassword(context: Context, dialog: DialogInterface, oldPassword: String, newPassword: String){

/*        val oldPasswordRB = RequestBody.create(MediaType.parse("text/plain"), oldPassword)
        val newPasswordRB = RequestBody.create(MediaType.parse("text/plain"), newPassword)*/

        progress.value = true
        val credentials = ChangePasswordCredentials(oldPassword, newPassword)
        App.getClient(context)
            .changePassword(credentials)
            .enqueue(object: Callback<Any?> {
                override fun onFailure(call: Call<Any?>, t: Throwable) {

                    progress.value = false
                    if(t is IOException){
                        context.toast("Нет подключения к интернету")
                    }else{
                        context.toast("Неизвестная ошибка")
                    }


                }

                override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                    if (response.isSuccessful){
                        Log.e("PAssw",response.body().toString())
                        context.toast("Пароль успешно изменен")
                        dialog.dismiss()
                    }else{
                        val error = response.errorBody()?.string()
                        if (error != null) {
                            if (error.contains("Старый парол")) {
                                context.toast("Старый пароль неверный")

                            }else{
                                context.toast(response.errorBody()!!.string())

                            }
                        }else{
                                context.toast(response.errorBody()!!.string())

                        }
                    }

                    progress.value = false
                }
            })

    }
}
