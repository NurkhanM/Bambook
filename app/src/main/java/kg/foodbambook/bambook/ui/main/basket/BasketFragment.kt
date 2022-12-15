package kg.foodbambook.bambook.ui.main.basket

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kg.foodbambook.bambook.App
import kg.foodbambook.bambook.Constants
import kg.foodbambook.bambook.R
import kg.foodbambook.bambook.utils.ToastUtils
import kotlinx.android.synthetic.main.basket_fragment.*

class BasketFragment : Fragment(), View.OnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BasketRVAdapter
    private lateinit var orderButton: Button
    private lateinit var totalPrice: TextView
    private lateinit var forPayment: TextView
    private lateinit var bonusEditText: EditText
    private lateinit var emptyListTextView: TextView


    companion object {
        fun newInstance() = BasketFragment()
    }

    private lateinit var viewModel: BasketViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.basket_fragment, container, false)
        initView(v)

        return v
    }

    @SuppressLint("SetTextI18n")
    private fun initView(v: View) {

        emptyListTextView = v.findViewById(R.id.empty_basket_text_view)
        viewModel = ViewModelProviders.of(this).get(BasketViewModel::class.java)

        recyclerView = v.findViewById(R.id.rv)
        orderButton = v.findViewById(R.id.orderButton)
        orderButton.setOnClickListener(this)
        totalPrice = v.findViewById(R.id.totalPrice)
        forPayment = v.findViewById(R.id.for_payment)
        bonusEditText = v.findViewById(R.id.bonus)
        bonusEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s.isNullOrEmpty()) {
                        viewModel.updateSum(0)
                        return
                    }

                    val bonus = "$s".toInt()

                    viewModel.updateSum(bonus)
                } catch (e: NumberFormatException) {
                    ToastUtils.showToast(context!!, "Введите число")
                } catch (e: IllegalArgumentException) {
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        viewModel.user.observe(requireActivity(), Observer {
            if (it != null) {
                try {
                    bonus_size.text = "Снять бонусы (${it.bonuses})"
                }catch (e: NullPointerException){
                    Toast.makeText(requireContext(), "Бонусов еще нет", Toast.LENGTH_SHORT).show()
                }catch (e: IllegalStateException){
                    Toast.makeText(requireContext(), "Сервер не отвечает", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getProfileInfo(requireContext())
    }

    override fun onClick(v: View?) {

        if (v == null) return

        when (v.id) {
            orderButton.id -> {
                viewModel.checkWorkTime(requireContext())
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BasketViewModel::class.java)
        setupRV()
        setupVM()
    }

    private fun setupVM() {
        viewModel.getTotalSum().observe(requireActivity(), androidx.lifecycle.Observer {
            totalPrice.text = requireContext().getString(R.string.total_sum, App.getSum())
            forPayment.text = requireContext().getString(R.string.for_payment, it)
        })
        App.list.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.isNullOrEmpty()) updateUI(Constants.STATE_EMPTY_LIST)
            else updateUI(Constants.STATE_DATA_LOADED)
            adapter.setDataList(it)
        })
        viewModel.checkWorkTimeLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.consume { action ->
                if (action.success) {
                    val bundle = Bundle().apply {
                        try {
                            putInt("bonus", bonusEditText.text.toString().toInt())
                        } catch (e: Exception) {
                            putInt("bonus", 0)
                        }
                    }
                    findNavController().navigate(R.id.orderFragment, bundle)
                } else {
                    Toast.makeText(
                        requireContext(), "${action.error}", Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun setupRV() {

        val layoutManger = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManger
        adapter = BasketRVAdapter(requireContext(), viewModel)
        recyclerView.adapter = adapter

    }

    private fun updateUI(state: Int) {
        when (state) {
            Constants.STATE_EMPTY_LIST -> {
                orderButton.visibility = View.GONE
                recyclerView.visibility = View.GONE
                emptyListTextView.visibility = View.VISIBLE
            }

            Constants.STATE_DATA_LOADED -> {
                orderButton.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                emptyListTextView.visibility = View.GONE

            }
        }
    }

}
