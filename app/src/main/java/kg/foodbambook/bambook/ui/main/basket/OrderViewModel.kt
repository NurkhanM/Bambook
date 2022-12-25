package kg.foodbambook.bambook.ui.main.basket

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kg.docplus.fcm.FCMTokenUtils
import kg.foodbambook.kg.App
import kg.foodbambook.bambook.Constants.KEY_ERROR
import kg.foodbambook.bambook.checkIfIsInternetProblemAndShowMessage
import kg.foodbambook.bambook.model.*
import kg.foodbambook.bambook.utils.Event
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel(application: Application) : AndroidViewModel(application) {


    @Suppress("PrivatePropertyName")
    private val TAG = OrderViewModel::class.java.simpleName

    private val user = MutableLiveData<User>(null)
    private val contacts = MutableLiveData<Contacts>(null)
    private val _orderImpossible = MutableLiveData<String>()
    val orderImpossible: LiveData<String> = _orderImpossible

    fun getUser() : LiveData<User> {
        if (user.value == null)
            getProfileInfo(getApplication())
        return user
    }
    fun getContacts() : LiveData<Contacts> {
        if (contacts.value == null)
            getContactInfo(getApplication())
        return contacts
    }



    private val _showDialog = MutableLiveData<Event<String>>()

    val showDialog: LiveData<Event<String>>
        get() = _showDialog


    fun createOrder(
        context: Context,
        name: String, phone: String, address: String,
        payMethod: PayMethod, building: String, flat: String? = null,
        entrance: String? = null, floor: String? = null, comment: String? = null,
        bonuses: Int = 0, change: Int? = null, cutleryCount: Int? = null
    ) {


        val list = arrayListOf<OrderedDish>()
        for ((dish, quantity) in App.list.value!!) {

            var garnishId: Int? = null
            var sous: Int? = null

            if (!dish.garnishes.isNullOrEmpty()) {
                garnishId = dish.garnishes!![0].id
            }
            if (!dish.additives.isNullOrEmpty()) {
                sous = dish.additives!![0].id
            }

            Log.e("Sous",sous.toString())

            list.add(OrderedDish(dish.id, garnishId, sous, quantity))
        }

        FCMTokenUtils.deleteToken(context)
        Log.e("Token",FCMTokenUtils.getTokenFromPrefs(context))

        var order = Order(
            list, payMethod, name, phone, address, building,
            flat, entrance,
            floor, comment, bonuses, change, cutleryCount,FCMTokenUtils.getTokenFromPrefs(context)
        )

        Log.e("Order",order.toString())

        App.getClient(context).createOrder(order).enqueue(object : Callback<OrderGet?> {
            override fun onFailure(call: Call<OrderGet?>, t: Throwable) {
            }

            override fun onResponse(call: Call<OrderGet?>, response: Response<OrderGet?>) {
                Log.e(TAG + "LOH", Gson().toJson(order))
                when {
                    response.isSuccessful -> {
                        _showDialog.value = Event(response.body()!!.future_bonuses + " баллов")
                        Log.e(TAG, response.message())
                    }

                    response.code() == 400 -> {
                        val responseJson = JSONObject(response.errorBody()?.string() ?: return)
                        if (responseJson.has(KEY_ERROR)) {
                            _orderImpossible.value = responseJson.getString(KEY_ERROR)
                        }
                    }

                    else -> {
                        Log.e(TAG + "LOH", response.errorBody()!!.string())
                    }
                }
            }
        })
    }


    private fun getProfileInfo(context: Context) {
        App.getClient(context)
            .getProfileInfo()
            .enqueue(object : Callback<User?> {
                override fun onFailure(call: Call<User?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context)
                }

                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    if (response.isSuccessful) {
                        Log.e("PROFILE",response.body().toString())
                        user.value = response.body()
                    }
                }
            })
    }

    private fun getContactInfo(context: Context){
        App.getClient(context)
            .getContacts()
            .enqueue(object: Callback<Contacts?> {
                override fun onFailure(call: Call<Contacts?>, t: Throwable) {

                }

                override fun onResponse(call: Call<Contacts?>, response: Response<Contacts?>) {
                    if (response.isSuccessful) {

                        contacts.value = response.body()

                    }
                }
            })
    }
}