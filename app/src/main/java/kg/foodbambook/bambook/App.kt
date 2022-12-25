package kg.foodbambook.kg

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import kg.foodbambook.bambook.model.Cart
import kg.foodbambook.bambook.model.Dish
import kg.foodbambook.bambook.retrofit.BambookAPI
import kg.foodbambook.bambook.retrofit.RetrofitClient

class App: Application() {

    companion object {
//        private const val BASE_URL = "http://46.229.215.95"
        private const val BASE_URL = "https://bambook.kg"
//        private const val BASE_URL = "http://217.151.230.134"
//        private const val TEST_URL = "http://bambook.sunrisetest.online"

        private var currentUrl = BASE_URL

        private var api:BambookAPI? = null
        fun getClient(context: Context): BambookAPI {
            if (api == null)
                api = RetrofitClient.create(context, currentUrl)!!.create(BambookAPI::class.java)
            return api!!
        }

        fun resetApi(){
            api = null
            RetrofitClient.resetRetrofit()
        }


        val cart = Cart()
        val list = MutableLiveData(cart.list)

        fun addToCart(dish: Dish, quantity: Int){
            cart.addToList(dish, quantity)
        }

        fun updateCart(dish: Dish, quantity: Int){
            cart.updateList(dish, quantity)
        }

        fun getSum() = cart.sum

        fun removeFromCart(dish: Dish){
            cart.remove(dish)
        }

        fun emptyCart(){
            cart.clear()
        }
    }
}