package kg.foodbambook.bambook.ui.main.basket

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kg.foodbambook.bambook.R
import kg.foodbambook.bambook.model.Dish
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BasketRVAdapter internal constructor(
    private val context: Context,
    private val viewModel: BasketViewModel
) : androidx.recyclerview.widget.RecyclerView.Adapter<BasketRVAdapter.ProductItemViewHolder>() {

    private var itemMap = HashMap<Dish, Int>()
    private var itemList = ArrayList<Dish>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val view = inflater.inflate(R.layout.rv_cart_item, parent, false)
        return ProductItemViewHolder(view)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        holder.setData(itemList[position])
    }

    fun setDataList(list: HashMap<Dish, Int>) {
        itemList.addAll(list.keys)
        itemMap = list
        notifyDataSetChanged()
    }

    inner class ProductItemViewHolder (itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)  {
        private val image:ImageView = itemView.findViewById(R.id.image)
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)
        private val removeButton: ImageView = itemView.findViewById(R.id.close)
        private val countTextView: TextView = itemView.findViewById(R.id.cutleryCount)
        private val countInc: Button = itemView.findViewById(R.id.count_inc)
        private val countDec: Button = itemView.findViewById(R.id.count_dec)

        private var count = 1
        private var price = 0.0

        init {
            removeButton.setOnClickListener {
                try {
                    viewModel.removeItemFromList(itemList[adapterPosition])
                    itemList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                } catch (e: IndexOutOfBoundsException) {
                    return@setOnClickListener
                }
            }
            countInc.setOnClickListener {
                count++
                countTextView.text = count.toString()
                viewModel.updateList(itemList[adapterPosition], count)

            }
            countDec.setOnClickListener {
                if (count <= 1) {
                    return@setOnClickListener
                }
                count--

                countTextView.text = count.toString()
                viewModel.updateList(itemList[adapterPosition], count)
            }
        }


        fun setData(item: Dish) {

            price = item.price!!

            count = itemMap[item]!!
            countTextView.text = itemMap[item]!!.toString()


            Glide
                .with(context)
                .load(item.photo)
                .into(image)
            Log.e("NAMEEE",item.name)
            nameTextView.text = item.name
            priceTextView.text = item.price.toString()

        }
    }


}
