package kg.foodbambook.bambook.ui.main.basket


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.santalu.maskedittext.MaskEditText
import kg.foodbambook.bambook.*
import kg.foodbambook.bambook.model.Contacts

import kg.foodbambook.bambook.model.PayMethod
import kg.foodbambook.bambook.model.User
import kotlinx.android.synthetic.main.fragment_order.*
import java.lang.NumberFormatException

private const val BONUS = "bonus"

class OrderFragment : Fragment(), View.OnClickListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var paymentInfoTextView: TextView
    private lateinit var changesSumTextView: TextView
    private lateinit var submitButton: Button

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: MaskEditText
    private lateinit var addressEditText: EditText
    private lateinit var homeEditText: EditText
    private lateinit var flatNoEditText: EditText
    private lateinit var entranceEditText: EditText
    private lateinit var floorEditText: EditText
    private lateinit var commentEditText: EditText

    private lateinit var cutleryQuantityTextView: TextView
    private lateinit var incButton: Button
    private lateinit var decButton: Button

    private var contacts: Contacts? = null


    private var payMethod = PayMethod.cash
    private var bonus = 0

    private lateinit var viewModel: OrderViewModel

    companion object {
        fun newInstance() = OrderFragment()
        fun newInstance(bonus: Int) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putInt(BONUS, bonus)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            bonus = it.getInt(BONUS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_order, container, false)
        initViews(v)
        return v
    }

    private fun initViews(v: View){
        nameEditText = v.findViewById(R.id.name)
        phoneEditText = v.findViewById(R.id.et_auth)
        addressEditText = v.findViewById(R.id.address)
        homeEditText = v.findViewById(R.id.home_no)
        flatNoEditText = v.findViewById(R.id.flat_no)
        entranceEditText = v.findViewById(R.id.entrance)
        floorEditText = v.findViewById(R.id.floor_no)
        commentEditText = v.findViewById(R.id.comment)
        cutleryQuantityTextView = v.findViewById(R.id.cutleryCount)
        incButton = v.findViewById(R.id.count_inc)
        incButton.setOnClickListener(this)
        decButton = v.findViewById(R.id.count_dec)
        decButton.setOnClickListener(this)


        tabLayout = v.findViewById(R.id.payment_tab)
        paymentInfoTextView = v.findViewById(R.id.changes_text)
        changesSumTextView = v.findViewById(R.id.changes_sum)
        submitButton = v.findViewById(R.id.submit)
        submitButton.setOnClickListener(this)

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateTabSelections(tab!!.position)
            }
        })
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        viewModel.getContacts().observe(requireActivity(), Observer { contacts=it })
        viewModel.showDialog.observe(requireActivity(), Observer { showDialog(it.peekContent()) })
        viewModel.getUser().observe(requireActivity(), Observer { if (it != null)setFields(it) })
        viewModel.orderImpossible.observe(requireActivity(), Observer {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    fun updateTabSelections(tabPos: Int) {
        when (tabPos) {
            0 -> {
                payMethod = PayMethod.cash
                paymentInfoTextView.text = requireContext().getString(R.string.changes)
                paymentInfoTextView.gravity = Gravity.START
                changesSumTextView.visible()
            }
            1 ->{
                payMethod = PayMethod.cash
                paymentInfoTextView.text = requireContext().getString(R.string.courier_with_pos)
                paymentInfoTextView.gravity = Gravity.CENTER
                changesSumTextView.gone()

            }
            2 ->{
                payMethod = PayMethod.elsom
                paymentInfoTextView.text = requireContext().getString(R.string.number_elsom,
                    contacts?.elsom
                )
                paymentInfoTextView.gravity = Gravity.CENTER
                changesSumTextView.gone()
            }
            3 ->{
                payMethod = PayMethod.balance
                paymentInfoTextView.text = requireContext().getString(R.string.number_balans,
                    contacts?.balance_kg
                )
                paymentInfoTextView.gravity = Gravity.CENTER
                changesSumTextView.gone()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.submit ->{
                submit.isEnabled = false
                // fix validate function
                Log.e("kspdf","Submit")
                if (!validate()) return
                var change: Int?

                var cutlery: Int?
                try {
                    change = changesSumTextView.text.toString().toInt()
                    cutlery  = cutleryQuantityTextView.text.toString().toInt()
                }catch (e: NumberFormatException){
                    change = null
                    cutlery = null
                }
                viewModel.createOrder(requireContext(), nameEditText.text.toString(), phoneEditText.text.toString(), addressEditText.text.toString(),
                    payMethod, homeEditText.text.toString(), flatNoEditText.text.toString(),
                    entranceEditText.text.toString(), floorEditText.text.toString(), commentEditText.text.toString(),
                    bonus, change, cutlery)
            }

            R.id.count_inc ->{
                var i = cutleryQuantityTextView.text.toString().toInt()
                i++
                cutleryQuantityTextView.text = i.toString()
            }

            R.id.count_dec ->{
                var i = cutleryQuantityTextView.text.toString().toInt()
                if (i <= 0) return
                i--
                cutleryQuantityTextView.text = i.toString()
            }
        }
    }

    var dialog:AlertDialog? = null

    @SuppressLint("SetTextI18n")
    private fun showDialog(msg: String) {

        Log.e("TAGHHHHHH",msg)
        submit.isEnabled = true
        if (dialog==null) {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            val view = layoutInflater.inflate(R.layout.dialog_ordered, null)

            view.findViewById<TextView>(R.id.order_confirmation).text =
                "Ожидайте подтверждения заказа.  Бонусы к зачислению: $msg"

            builder.setView(view)

            dialog = builder.create()


            dialog!!.setCanceledOnTouchOutside(true)
            dialog!!.setOnCancelListener {
                App.emptyCart()
                findNavController().navigateUp()
            }



            dialog!!.show()
        }
    }

    // fix validate function
    private fun validate(): Boolean{
        val flag = true

        nameEditText.validate({s -> s.isNotEmpty() }, "Имя не может быть пустым!")
        phoneEditText.validate({s -> (s.isNotEmpty() and (s.length >= 10))}, "+996 XXX XX XX XX")
        addressEditText.validate({s -> s.isNotEmpty() }, "Введите адрес")
        homeEditText.validate({s -> s.isNotEmpty() }, "Введите номер дома")
        changesSumTextView

        return flag
    }


    private fun setFields(user: User) {
        nameEditText.setText(user.firstName)
        phoneEditText.setText(user.phone)
        addressEditText.setText(user.address)
        homeEditText.setText(user.building)
        flatNoEditText.setText(user.flat)
    }

}
