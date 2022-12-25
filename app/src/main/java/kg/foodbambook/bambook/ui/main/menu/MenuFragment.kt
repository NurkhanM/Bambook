package kg.foodbambook.bambook.ui.main.menu

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kg.foodbambook.kg.App

import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Dish
import kg.foodbambook.bambook.model.Category
import kg.foodbambook.bambook.retrofit.BambookAPI
import kg.foodbambook.bambook.ui.OnItemClickListener
import kg.foodbambook.bambook.ui.main.category.CategoryFragmentArgs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val CATEGORY_ITEM = "category_item"
class MenuFragment : Fragment(), View.OnClickListener {

    private var TAG = MenuFragment::class.java.simpleName
    private var mainCategoryItem: Category? = null
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var adapter: MenuAdapter? = null
    private lateinit var api: BambookAPI

    private lateinit var searchButton: ImageView
    private lateinit var searchEditView: EditText

    private var list: List<Dish>? = null

    companion object {
        fun newInstance() = MenuFragment()
        fun newInstance(item: Category) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CATEGORY_ITEM, item)
                }
            }
    }

    private lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val safeArgs = CategoryFragmentArgs.fromBundle(it)
            mainCategoryItem = safeArgs.categoryItem
        }

        api = App.getClient(context!!)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.sub_category_fragment, container, false)

        initViews(v)
        setupRV()

        if (mainCategoryItem != null && list.isNullOrEmpty()){
            getDishes()
            Log.e(TAG, "dishes")
        }
        return v
    }

    private fun initViews(v: View){
        searchEditView = v.findViewById(R.id.search_text)
        searchButton = v.findViewById(R.id.search_button)
        searchButton.setOnClickListener(this)

        recyclerView = v.findViewById(R.id.rv)

        v.findViewById<TextView>(R.id.category).text = mainCategoryItem!!.name

    }
    private fun setupRV() {
        val layoutManger = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManger
        if (adapter == null)
            adapter = MenuAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        adapter!!.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {

                val bundle = Bundle().apply {
                    putSerializable("dish", adapter!!.itemList[position])
                }
                findNavController().navigate(R.id.action_menuFragment_to_dishFragment, bundle)
            }

            override fun onItemLongClick(position: Int) {

            }
        })
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun getDishes(){
        api.getDishes(mainCategoryItem!!.id).enqueue(object: Callback<List<Dish>?> {
            override fun onFailure(call: Call<List<Dish>?>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Dish>?>, response: Response<List<Dish>?>) {
                Log.e(TAG, response.body().toString())
                if (response.code() == 200) {
                    list = response.body()
                    if (!list.isNullOrEmpty()) {
                        Log.e("MENUDD",list!!.size.toString())
                        adapter!!.setDataList(list!!)
                    }

                }else {
                    Log.e(TAG, "code $response.code()")
                }

            }
        })
    }

    override fun onClick(v: View?) {

        if(v == null) return

        when (v.id) {
            R.id.search_button -> {


                val txt = searchEditView.text.toString()
                if (txt.isNotEmpty()) {
                    val bundle = Bundle().apply {
                        putString("search_text", txt)
                        putSerializable(CATEGORY_ITEM, mainCategoryItem!!)
                    }
                    findNavController().navigate(R.id.searchFragment, bundle)
                }
            }
        }
    }
}
