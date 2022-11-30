package kg.foodbambook.bambook.ui.main.profile.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kg.foodbambook.bambook.App

import kg.foodbambook.bambook.R
import kg.foodbambook.bambook.checkIfIsInternetProblemAndShowMessage
import kg.foodbambook.bambook.model.OrderHistoryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_order_history, container, false)
        recyclerView = v.findViewById(R.id.rv)
        setupRV()
        getHistory()
        return v
    }

    private fun setupRV() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = HistoryRVAdapter(requireContext())
        recyclerView.adapter = adapter
    }




    private fun getHistory(){
        App.getClient(requireContext())
            .getOrderHistory()
            .enqueue(object: Callback<List<OrderHistoryItem>?> {
                override fun onFailure(call: Call<List<OrderHistoryItem>?>, t: Throwable) {
                    t.checkIfIsInternetProblemAndShowMessage(context!!)
                }

                override fun onResponse(call: Call<List<OrderHistoryItem>?>, response: Response<List<OrderHistoryItem>?>) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        if (list != null) adapter.setDataList(list)
                    }
                }
            })
    }



}
