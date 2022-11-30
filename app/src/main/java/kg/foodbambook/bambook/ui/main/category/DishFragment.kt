package kg.foodbambook.bambook.ui.main.category

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kg.foodbambook.bambook.App

import kg.foodbambook.bambook.R
import kg.foodbambook.bambook.model.Dish
import kg.foodbambook.bambook.model.Garnish
import kg.foodbambook.bambook.utils.ToastUtils

class DishFragment : Fragment(), View.OnClickListener {

    private val TAG = DishFragment::class.java.simpleName
    private lateinit var spinner: Spinner
    private lateinit var spinnerSous: Spinner
    private lateinit var addToCartButton: Button
    private lateinit var countTextView: TextView
    private lateinit var dishName: TextView
    private lateinit var dishImage: ImageView
    private lateinit var dishDescription: TextView
    private lateinit var dishPrice: TextView
    private lateinit var dishOutput: TextView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageView


    private lateinit var dish: Dish
    private var count: Int = 1

    companion object {

        fun newInstance(dish: Dish) =
                DishFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("dish", dish)
                    }
                }
    }

    private lateinit var viewModel: DishViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            dish = it.getSerializable("dish") as Dish
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_dish, container, false)
        initViews(v)
        return v
    }

    private fun initViews(v: View){
        spinner = v.findViewById(R.id.garnish_spinner)
        spinnerSous = v.findViewById(R.id.sous_spinner)


        searchButton = v.findViewById(R.id.search_button)
        searchButton.setOnClickListener(this)
        searchEditText = v.findViewById(R.id.search_text)

        dishName = v.findViewById(R.id.dish_name)
        dishImage = v.findViewById(R.id.dish_photo)
        dishDescription = v.findViewById(R.id.dish_description)
        dishPrice = v.findViewById(R.id.price)
        dishOutput = v.findViewById(R.id.output)


        addToCartButton = v.findViewById(R.id.addToCartButton)
        addToCartButton.setOnClickListener(this)


        countTextView = v.findViewById(R.id.cutleryCount)

        updateUI()
        val countInc: Button = v.findViewById(R.id.count_inc)
        countInc.setOnClickListener(this)
        val countDec: Button = v.findViewById(R.id.count_dec)
        countDec.setOnClickListener(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, DishViewModelFactory(requireContext(), dish.id)).get(DishViewModel::class.java)
        viewModel.dish.value = dish
        /*viewModel.getDish().observe(this, Observer {
            updateUI()
        })*/
    }

    override fun onClick(v: View?) {
        if(v == null) return

        when(v.id){
            R.id.addToCartButton -> {
                if (viewModel.dish.value != null) {
                    if (dish.withGarnish || dish.with_additive) {
                        Log.e("Dish",dish.toString())
                        var garnish = ArrayList<Garnish>()
                        var addictive = ArrayList<Garnish>()

                        var garnishText = ""
                        var additiveText = ""
                        if (dish.withGarnish && spinner.selectedItemPosition != 0){
                            garnish.add(dish.garnishes!![spinner.selectedItemPosition - 1])
                            garnishText = " + "+dish.garnishes!![spinner.selectedItemPosition - 1].name
                        }
                        if (dish.with_additive && spinnerSous.selectedItemPosition != 0){
                            addictive.add(dish.additives!![spinnerSous.selectedItemPosition - 1])
                            additiveText = " + "+ dish.additives!![spinnerSous.selectedItemPosition - 1].name
                        }



                        App.addToCart(
                            viewModel.dish.value!!.copy(
                                garnishes = garnish,
                                additives = addictive,
                                name = "${dish.name}$garnishText$additiveText"
                            ), count
                        )

                    }else{ App.addToCart(viewModel.dish.value!!.copy(garnishes = arrayListOf(),additives = arrayListOf()), count)}

                    val msg= if(count ==1){
                        "$count порция ${viewModel.dish.value!!.name} добавлен в корзину"
                    }else "$count порций ${viewModel.dish.value!!.name} добавлен в корзину"

                    ToastUtils.showToast(requireContext(), msg)
                }
            }
            R.id.count_inc -> {
                count++
                countTextView.text = count.toString()
            }

            R.id.count_dec -> {
                if (count <=1) return
                count--
                countTextView.text = count.toString()
            }

            searchButton.id -> {
                val txt = searchEditText.text.toString()
                if (txt.isNotEmpty()){
                    val bundle = Bundle().apply {
                        putString("search_text", txt)
                    }

                    findNavController().navigate(R.id.searchFragment, bundle)
                }
            }
        }
    }


    private fun updateUI(){
        dishName.text = dish.name
        dishDescription.text = dish.description
        dishPrice.text = resources.getString(R.string.price_som, dish.price.toString())
        countTextView.text = count.toString()
        dishOutput.text = dish.output

        Glide.with(this)
            .load(dish.photo)
            .into(dishImage)

        if(dish.withGarnish and !dish.garnishes.isNullOrEmpty()){
            val list: ArrayList<CharSequence> = arrayListOf("+Гарнир на выбор")
            for (i in 0 until dish.garnishes!!.size) {
                list.add(i+1, dish.garnishes!![i].name)
            }

            val adapter: ArrayAdapter<CharSequence> = ArrayAdapter(requireContext(), R.layout.spinner_garnish_item, list)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.visibility = View.VISIBLE
        }
        if(dish.with_additive and !dish.additives.isNullOrEmpty()){
            val list: ArrayList<CharSequence> = arrayListOf("+Соусы на выбор")
            for (i in 0 until dish.additives!!.size) {
                list.add(i+1, dish.additives!![i].name)
            }

            val adapter: ArrayAdapter<CharSequence> = ArrayAdapter(requireContext(), R.layout.spinner_garnish_item, list)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinnerSous.adapter = adapter
            spinnerSous.visibility = View.VISIBLE
        }
    }


}
