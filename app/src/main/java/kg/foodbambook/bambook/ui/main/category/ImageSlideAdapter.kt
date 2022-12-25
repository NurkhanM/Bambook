package kg.foodbambook.bambook.ui.main.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.SliderImage
import java.lang.IndexOutOfBoundsException

class ImageSlideAdapter(private val context:Context, private var imageList: ArrayList<SliderImage>): PagerAdapter() {

    private val TAG = ImageSlideAdapter::class.java.simpleName
    private var inflater: LayoutInflater? = LayoutInflater.from(context)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageLayout: ViewGroup = inflater!!.inflate(R.layout.slider_item, container, false) as ViewGroup
        val imageView: ImageView = imageLayout.findViewById(R.id.slidingImageItem)
        Log.e(TAG, "item count $count  $position")
        Glide.with(context)
            .load(imageList[position].image)
            .into(imageView)
        try {

            container.addView(imageLayout)

        } catch (e: IndexOutOfBoundsException) {
        }
        return imageLayout
    }

     fun setData(list: ArrayList<SliderImage>) {
        imageList = list
        notifyDataSetChanged()
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view == `object`
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}