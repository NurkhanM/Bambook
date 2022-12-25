package kg.foodbambook.bambook.ui.main.discounts


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kg.foodbambook.kg.App

import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.Stock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = DiscountsFragment::class.java.simpleName
class DiscountsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: DiscountAdapter? = null

    private var list: ArrayList<Stock>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_discounts, container, false)
        recyclerView = v.findViewById(R.id.rv)
        setupRV()
        if(list == null)
        getDiscounts()
        return v
    }


    private fun setupRV() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        if(adapter == null)
        adapter = DiscountAdapter(requireContext())
        recyclerView.adapter = adapter
    }

    private fun getDiscounts(){
        App.getClient(requireContext())
            .getStocks()
            .enqueue(object: Callback<ArrayList<Stock>?> {
                override fun onFailure(call: Call<ArrayList<Stock>?>, t: Throwable) {

                }

                override fun onResponse(call: Call<ArrayList<Stock>?>, response: Response<ArrayList<Stock>?>) {
                    Log.e(TAG, response.body().toString())
                    if (response.isSuccessful){
                        list = response.body()
                        if (response.body() != null) adapter!!.setDataList(response.body()!!)
                    }else{

                    }
                }
            })
    }

}
