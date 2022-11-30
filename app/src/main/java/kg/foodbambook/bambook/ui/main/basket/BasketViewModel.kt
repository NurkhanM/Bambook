package kg.foodbambook.bambook.ui.main.basket

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kg.foodbambook.bambook.App
import kg.foodbambook.bambook.checkIfIsInternetProblemAndShowMessage
import kg.foodbambook.bambook.model.CheckWorkTimeResponse
import kg.foodbambook.bambook.model.Dish
import kg.foodbambook.bambook.model.User
import kg.foodbambook.bambook.utils.ConsumableValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BasketViewModel : ViewModel() {

    var totalSum: MutableLiveData<Double> = MutableLiveData()
    var list: MutableLiveData<MutableMap<Dish, Int>> = MutableLiveData()
    var bonus = 0
    val user = MutableLiveData<User>(null)
    private val _checkWorkTimeLiveData =
        MutableLiveData<ConsumableValue<CheckWorkTimeResponse>>()
    val checkWorkTimeLiveData: LiveData<ConsumableValue<CheckWorkTimeResponse>>
        get() = _checkWorkTimeLiveData

    fun getTotalSum(): LiveData<Double> {
        if (totalSum.value == null) totalSum.value = App.getSum() - bonus
        return totalSum
    }

    fun updateSum(bonus: Int) {
        this.bonus = bonus
        totalSum.value = App.getSum() - bonus
    }

    fun updateList(dish: Dish, quantity: Int) {
        App.updateCart(dish, quantity)
        totalSum.value = App.getSum()
    }

    fun removeItemFromList(dish: Dish) {
        App.removeFromCart(dish)
        totalSum.value = App.getSum()
    }

    fun getList() {
        list.value!!.putAll(App.list.value!!)
    }

    fun getProfileInfo(context: Context) {
        App.getClient(context)
            .getProfileInfo()
            .enqueue(object : Callback<User?> {
                override fun onFailure(call: Call<User?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context)
                }

                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    if (response.isSuccessful) {
                        Log.e("PROFILE", response.body().toString())
                        user.value = response.body()
                    } else {
                        val error = response.errorBody()?.string()

                    }
                }
            })
    }

    fun checkWorkTime(context: Context) {
        App.getClient(context)
            .checkWorkTime()
            .enqueue(object : Callback<CheckWorkTimeResponse> {
                override fun onResponse(
                    call: Call<CheckWorkTimeResponse>,
                    response: Response<CheckWorkTimeResponse>
                ) {
                    if (response.isSuccessful) {
                        _checkWorkTimeLiveData.value =
                            ConsumableValue(response.body()!!)
                    } else {
                        response.errorBody()?.let {
                            _checkWorkTimeLiveData.value = ConsumableValue(
                                Gson().fromJson(
                                    it.string(), CheckWorkTimeResponse::class.java
                                )
                            )
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CheckWorkTimeResponse>, t: Throwable
                ) {
                }
            })
    }
}
