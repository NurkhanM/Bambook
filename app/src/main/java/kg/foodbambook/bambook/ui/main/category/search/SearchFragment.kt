package kg.foodbambook.bambook.ui.main.category.search

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
import androidx.recyclerview.widget.RecyclerView
import kg.foodbambook.bambook.*

import kg.foodbambook.bambook.model.Dish
import kg.foodbambook.bambook.model.Category
import kg.foodbambook.bambook.ui.OnItemClickListener
import kg.foodbambook.bambook.ui.main.menu.CATEGORY_ITEM
import kg.foodbambook.bambook.ui.main.menu.MenuAdapter
import kg.foodbambook.kg.App
import kg.foodbambook.kg.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private val TAG = SearchFragment::class.java.simpleName
class SearchFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: SearchViewModel

    private lateinit var backButton: ImageView
    private var searchTxt: String? = null
    private var searchCategoryItem: Category? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var title: TextView
    private lateinit var adapter: MenuAdapter

    private lateinit var searchButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var infoTextView: TextView

    companion object {
        const val SEARCH_TEXT = "search_text"

        fun newInstance(str: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH_TEXT, str)
                }
            }
        fun newInstance(str: String, item: Category) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH_TEXT, str)
                    putSerializable(CATEGORY_ITEM, item)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            searchTxt = it.getString(SEARCH_TEXT)
            searchCategoryItem = it.getSerializable(CATEGORY_ITEM) as? Category

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v =  inflater.inflate(R.layout.search_fragment, container, false)

        initViews(v)
        setupRV()
        if (searchCategoryItem != null) searchDishes(searchTxt!!, searchCategoryItem!!.id.toString())
        else searchDishes(searchTxt!!)
        return v
    }

    private fun initViews(v: View){
        backButton = v.findViewById(R.id.back_button)
        backButton.setOnClickListener(this)
        title = v.findViewById(R.id.category)
        title.text = if (searchCategoryItem != null) "Найдено\n${searchCategoryItem!!.name}"
            else "Найдено"

        recyclerView = v.findViewById(R.id.rv)

        searchEditText = v.findViewById(R.id.search_text)
        searchEditText.setText(searchTxt)

        searchButton = v.findViewById(R.id.search_button)
        searchButton.setOnClickListener(this)

        infoTextView = v.findViewById(R.id.info_text)

    }

    private fun setupRV() {
        val layoutManger = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManger
        adapter = MenuAdapter(requireContext())
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {


                val bundle = Bundle().apply {
                    putSerializable("dish", adapter.itemList[position])
                }
                findNavController().navigate(R.id.dishFragment, bundle)
            }

            override fun onItemLongClick(position: Int) {

            }
        })
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when(v.id){
            backButton.id -> {
                findNavController().navigateUp()
            }
            searchButton.id -> {
                if (searchEditText.validate({ s -> s.isNotEmpty()}, "Введите запрос")){
                    val txt = searchEditText.text.toString()
                    if (searchCategoryItem != null) searchDishes(txt, searchCategoryItem!!.id.toString())
                    else searchDishes(txt)
                }
            }

        }
    }

    private fun searchDishes(searchWrd: String, categoryId: String = ""){
        App.getClient(requireContext())
            .searchDishes(searchWrd, categoryId)
            .enqueue(object: Callback<List<Dish>?> {
                override fun onFailure(call: Call<List<Dish>?>, t: Throwable) {
                    if (t is IOException) {
                        if (adapter.itemCount > 0) {
                            context?.toast(R.string.error_message_bad_internet_connection)
                        } else {
                            infoTextView.text = context!!.getString(R.string.error_message_bad_internet_connection)
                            infoTextView.visible()
                        }
                    } else {
                        context?.toast(R.string.error_message_converting_failed)
                    }
                }

                override fun onResponse(call: Call<List<Dish>?>, response: Response<List<Dish>?>) {
                    Log.e(TAG, response.body().toString())
                    if (response.code() == 200) {

                        val list = response.body()
                        if (!list.isNullOrEmpty()) {
                            adapter.setDataList(list)
                            infoTextView.gone()
                        } else {
                            adapter.setDataList(emptyList())
                            infoTextView.text = context!!.getString(R.string.error_message_no_matches_were_found_for_request, searchEditText.text.toString())
                            infoTextView.visible()
                        }

                    }else {
                        Log.e(TAG, "code $response.code()")
                    }
                }
            })
    }


}
