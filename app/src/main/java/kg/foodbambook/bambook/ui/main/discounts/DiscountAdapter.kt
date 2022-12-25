package kg.foodbambook.bambook.ui.main.discounts

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Stock
import kg.foodbambook.bambook.ui.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class DiscountAdapter internal constructor(
    private val context: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder>() {

    private var itemList = emptyList<Stock>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val view = inflater.inflate(R.layout.rv_discount_item, parent, false)
        return DiscountViewHolder(view)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        holder.setData(itemList[position])
    }

    fun setDataList(list: ArrayList<Stock>) {
        itemList = list
        notifyDataSetChanged()
    }
    inner class DiscountViewHolder  (itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)  {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val endDate: TextView = itemView.findViewById(R.id.endOfData)
        fun setData(stock: Stock){

            title.text = stock.title
            description.text = stock.description

            val pattern = "dd.MM.yyyy"
            val df = SimpleDateFormat(pattern, Locale.ENGLISH)
            Log.e("END_DATE",stock.end_date)
            endDate.text = context.getString(R.string.discount_date, df.format(DateUtils.convertToDate(stock.end_date)))

            Glide.with(context)
                .load(stock.image)
                .into(image)


        }
    }

}