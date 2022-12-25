package kg.foodbambook.bambook.ui.main.profile.history

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.util.DataUtils
import kg.foodbambook.kg.App
import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.DishHistory
import kg.foodbambook.bambook.model.OrderHistoryItem
import kg.foodbambook.kg.ui.main.MainActivity
import kg.foodbambook.bambook.ui.utils.DateUtils
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class HistoryRVAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<HistoryRVAdapter.HistoryViewHolder>() {

    private var itemList = emptyList<OrderHistoryItem>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = inflater.inflate(R.layout.rv_history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    fun setDataList(list: List<OrderHistoryItem>) {
        itemList = list
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder  (itemView: View) : RecyclerView.ViewHolder(itemView)  {

        private var date: TextView = itemView.findViewById(R.id.date)
        private val listOfItems: TextView = itemView.findViewById(R.id.listOfItems)
        private val price: TextView = itemView.findViewById(R.id.price)
        var repeat_back:Button = itemView.findViewById(R.id.repeat_back)

        fun bindData(item: OrderHistoryItem){
            Log.e("ITEM_",item.toString())
            val pattern = "dd.MM.yyyy, HH:mm"
            val df = SimpleDateFormat(pattern, Locale.ENGLISH)
            var time = item.created_at
            time = time.substring(0, time.indexOf('.'))
            date.text = context.getString(R.string.history_date, df.format(DateUtils.convertToDate3(time)))
            price.text = context.getString(R.string.price_som, item.total)
            listOfItems.text = getDishTitles(item.elements)

            repeat_back.setOnClickListener {

                App.emptyCart()
                for (i in item.elements){
                    if (i.additive!=null){
                        i.dish.additives = arrayListOf(i.additive)
                    }

                    if (i.garnish!=null){
                        i.dish.garnishes = arrayListOf(i.garnish)
                    }
                    var garnishText = ""
                    var additiveText = ""
                    if (i.garnish!=null){
                        garnishText=" + ${i.garnish.name}"
                    }
                    if (i.additive!=null){
                        additiveText = " + ${i.additive.name}"
                    }

                    i.dish.name = "${i.dish.name}$garnishText$additiveText"
                    App.addToCart(i.dish,i.quantity)
                }

                MainActivity.bottomNavigationView.selectedItemId = R.id.basket
            }

        }
    }


    private fun getDishTitles(list: List<DishHistory>): String{
        val sb = StringBuilder()
        list.forEach {
            var s = "${it.quantity} + ${it.dish.name} + ${it.garnish?.name} + ${it.additive?.name}\n"
            s = s.replace(" + null","")
            sb.append(s)
        }
        return sb.toString()
    }

}