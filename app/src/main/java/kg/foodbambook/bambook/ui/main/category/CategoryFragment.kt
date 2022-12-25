package kg.foodbambook.bambook.ui.main.category

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import kg.foodbambook.kg.App

import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Category
import kg.foodbambook.bambook.model.SliderImage
import kg.foodbambook.bambook.retrofit.BambookAPI
import kg.foodbambook.bambook.ui.OnItemClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val TAG = CategoryFragment::class.java.simpleName

class CategoryFragment : Fragment(), View.OnClickListener {
    private lateinit var api: BambookAPI

    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var adapter: MenuAdapter? = null
    private lateinit var sliderViewPager: ViewPager
    private lateinit var sliderAdapter: ImageSlideAdapter

    private lateinit var searchButton: ImageView
    private lateinit var searchTextView: EditText

    private var categoryList: List<Category>? = null
    private var page = 0
    private val handler = Handler()
    private val runnable = object : Runnable {

        override fun run() {
            if (sliderAdapter.count == page-1) {
                page = 0
            } else {
                page++
            }
            sliderViewPager.setCurrentItem(page, true)
            handler.postDelayed(this, 4000)
        }
    }


    private var fragCount: Int = 0
    companion object {
        fun newInstance(instance: Int): CategoryFragment {
            val fragment = CategoryFragment()
            return fragment
        }

    }

    private lateinit var viewModel: CategoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = App.getClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.category_fragment, container, false)

        searchTextView = v.findViewById(R.id.search_text)
        searchButton = v.findViewById(R.id.search_button)
        searchButton.setOnClickListener(this)
        recyclerView = v.findViewById(R.id.rv)
        sliderViewPager = v.findViewById(R.id.slider)

        searchTextView.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                var txt = v.text
                if (txt.isNotEmpty()){
                    val bundle = Bundle().apply {
                        putString("search_text", txt as String?)
                    }
                    val imm =
                        searchTextView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchTextView.windowToken, 0)
                    findNavController().navigate(R.id.searchFragment, bundle)
                }

             true
            }else{
                false
            }


        }

        val list:ArrayList<SliderImage> = arrayListOf()

        sliderAdapter = ImageSlideAdapter(requireContext(), list)
        getSliderImage()
        sliderViewPager.adapter = sliderAdapter

        sliderViewPager.setCurrentItem(0, false)
        page = 0

        sliderViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                page = position
            }
        })
        setupRV()

        if (categoryList.isNullOrEmpty())
            getCategories()
        return v
    }


    private fun getCategories() {
        api.getCategories().enqueue(object: Callback<List<Category>?> {
            override fun onFailure(call: Call<List<Category>?>, t: Throwable) {
                Log.e(TAG, t.message.toString())

            }

            override fun onResponse(call: Call<List<Category>?>, response: Response<List<Category>?>) {

                if (response.code() == 200) {
                    categoryList = response.body()
                    if (!categoryList.isNullOrEmpty()) {

                        adapter!!.setDataList(categoryList!!)
                    }

                }
            }
        })
    }


    private fun getSliderImage(){
        App.getClient(requireContext())
            .getSliderImages()
            .enqueue(object: Callback<ArrayList<SliderImage>?> {
                override fun onFailure(call: Call<ArrayList<SliderImage>?>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ArrayList<SliderImage>?>,
                    response: Response<ArrayList<SliderImage>?>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body()!!
                        sliderAdapter.setData(list)
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            Log.e(TAG, "remove")
            handler.removeCallbacks(runnable)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 4000)
    }
    private fun setupRV() {
        val layoutManger = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManger
        if (adapter == null)
        adapter = MenuAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)
        //adapter.setDataList(list)

        adapter!!.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {

                val bundle = Bundle().apply {
                    putSerializable("category_item", adapter!!.itemList[position])
                }
                findNavController().navigate(R.id.menuFragment, bundle)
            }

            override fun onItemLongClick(position: Int) {

            }
        })
    }

    override fun onClick(v: View?) {
        if(v == null) return

        when (v.id) {
            R.id.search_button -> {

                val txt = searchTextView.text.toString()
                if (txt.isNotEmpty()){
                    val bundle = Bundle().apply {
                        putString("search_text", txt)
                    }
                    val imm =
                        searchTextView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchTextView.windowToken, 0)
                    findNavController().navigate(R.id.searchFragment, bundle)
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)

        if (savedInstanceState != null) {
            recyclerView.layoutManager!!.onRestoreInstanceState(
                savedInstanceState.getParcelable("layoutKey")
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: $fragCount")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (recyclerView.layoutManager != null)
            outState.putParcelable("layoutKey", recyclerView.layoutManager!!.onSaveInstanceState())


    }

}
