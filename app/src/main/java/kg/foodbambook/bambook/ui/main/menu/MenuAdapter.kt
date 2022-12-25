package kg.foodbambook.bambook.ui.main.menu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kg.foodbambook.kg.App
import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Dish
import kg.foodbambook.bambook.ui.OnItemClickListener
import kg.foodbambook.bambook.utils.ToastUtils

class MenuAdapter internal constructor(
    private val context: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<MenuAdapter.ItemViewHolder>() {
    var itemList = emptyList<Dish>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    internal lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        val view = inflater.inflate(R.layout.rv_item_subcategory, p0, false)
        return ItemViewHolder(view)    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        val menuItem: Dish = itemList[p1]
        p0.setData(menuItem, p1)
    }

    fun setDataList(items: List<Dish>) {
        this.itemList = items
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    inner class ItemViewHolder (itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val priceTextView: TextView = itemView.findViewById(R.id.price)
        val freeSideDishButton: View = itemView.findViewById(R.id.freeSideDishButton)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)

        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
            addToCartButton.setOnClickListener {
                App.addToCart(itemList[adapterPosition].copy(garnishes = arrayListOf()), 1)

                val msg= "1 порция ${itemList[adapterPosition].name} добавлен в корзину"

                ToastUtils.showToast(context, msg) }
        }

        fun setData(item: Dish, position: Int) {
            Glide.with(context)
                .load(item.photo)
                .into(imageView)

            nameTextView.text = item.name
            priceTextView.text = context.getString(R.string.price_som, item.price.toString())

            if (item.withGarnish) freeSideDishButton.visibility = View.VISIBLE

        }
    }

}