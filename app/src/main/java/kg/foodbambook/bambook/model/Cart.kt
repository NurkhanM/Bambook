package kg.foodbambook.bambook.model

import android.util.Log

class Cart {
    private val TAG = Cart::class.java.simpleName
    var list: HashMap<Dish, Int> = HashMap()
    var sum = 0.0


    fun updateList (dish: Dish, quantity: Int){
        if (list.containsKey(dish)) {
            sum += ( dish.price!! * (quantity - list[dish]!!))
            list[dish] = quantity
        } else {
            sum += (quantity * dish.price!!)
            list[dish] = quantity
        }
        Log.e(TAG, list.toString())
    }

    fun remove(dish: Dish){
        if (!list.containsKey(dish)) return
        sum -= (list[dish]!! * dish.price!!)
        list.remove(dish)
    }

    fun addToList(dish: Dish, quantity: Int){
        if (list.contains(dish)) {
            list[dish] = list[dish]!! + quantity
        }else list[dish] = quantity


        sum += (dish.price!! * quantity)
    }
    fun clear(){
        list.clear()
        sum = 0.0
    }

}