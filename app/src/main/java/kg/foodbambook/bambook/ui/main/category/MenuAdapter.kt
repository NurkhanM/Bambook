package kg.foodbambook.bambook.ui.main.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Category
import kg.foodbambook.bambook.ui.OnItemClickListener

class MenuAdapter internal constructor(
    private val context: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder>() {

    var itemList = emptyList<Category>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    internal lateinit var onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MenuItemViewHolder {
        val view = inflater.inflate(R.layout.rv_item, p0, false)
        return MenuItemViewHolder(view)    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(p0: MenuItemViewHolder, p1: Int) {
        val menuItem: Category = itemList[p1]

        p0.setData(menuItem, p1)
    }

    fun setDataList(items: List<Category>) {
        this.itemList = items
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    inner class MenuItemViewHolder (itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)  {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.name)


        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }
        fun setData(item: Category, pos: Int) {
            Glide.with(context)
                .load(item.image)
                .into(imageView)

            textView.text = item.name

        }
    }

}