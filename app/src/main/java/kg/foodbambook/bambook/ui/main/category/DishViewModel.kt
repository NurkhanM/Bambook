package kg.foodbambook.bambook.ui.main.category

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kg.foodbambook.kg.App
import kg.foodbambook.bambook.model.Dish
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DishViewModel(val context: Context,val id: Int) : ViewModel() {

    private val TAG = DishViewModel::class.java.simpleName
    val dish: MutableLiveData<Dish> = MutableLiveData()

    fun getDish(): LiveData<Dish>{
        if (dish.value == null)
            fetchDish()
        return dish
    }

    fun fetchDish(){
        App.getClient(context)
            .getDish(id)
            .enqueue(object: Callback<Dish?> {
                override fun onFailure(call: Call<Dish?>, t: Throwable) {

                }

                override fun onResponse(call: Call<Dish?>, response: Response<Dish?>) {
                    if (response.isSuccessful) {
                        dish.value = response.body()
                        Log.e(TAG, response.body().toString())
                    }
                }
            })

    }

    fun addToCart(dish: Dish, quantity: Int){
        App.updateCart(dish, quantity)
    }
}
