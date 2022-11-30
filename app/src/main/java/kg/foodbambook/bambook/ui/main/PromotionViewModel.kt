package kg.foodbambook.bambook.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kg.foodbambook.bambook.App
import kg.foodbambook.bambook.ifSuccessful
import kg.foodbambook.bambook.model.Promotion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromotionViewModel : ViewModel() {

    private val _promotion = MutableLiveData<Promotion>()
    val promotion: LiveData<Promotion> = _promotion

    fun getPromotion(context: Context) {
        App.getClient(context).getPromotion().enqueue(object : Callback<Promotion> {

            override fun onResponse(call: Call<Promotion>, response: Response<Promotion>) {
                response.ifSuccessful {
                    if (body() != null) _promotion.value = response.body()!!
                }
            }

            override fun onFailure(call: Call<Promotion>, t: Throwable) {
            }

        })
    }
}