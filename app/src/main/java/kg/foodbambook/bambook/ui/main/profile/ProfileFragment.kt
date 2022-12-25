package kg.foodbambook.bambook.ui.main.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

import kg.foodbambook.kg.R
import kg.foodbambook.bambook.model.User
import kg.foodbambook.bambook.model.UserDto
import kg.foodbambook.bambook.show
import kg.foodbambook.kg.ui.main.MainActivity
import kg.foodbambook.bambook.validate
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var orderStory: Button
    private lateinit var signOutButton: View

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var buildingEditText: EditText
    private lateinit var flatEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var progress: View

    companion object {
        fun newInstance() = ProfileFragment()

        fun newInstance(instance: Int): ProfileFragment {
            val fragment = ProfileFragment()
            return fragment
        }
    }

    private lateinit var viewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.profile_fragment, container, false)
        initView(v)

        return v
    }

    private fun initView(v: View){
        orderStory = v.findViewById(R.id.order_story)
        orderStory.setOnClickListener(this)

        signOutButton = v.findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener(this)

        nameEditText = v.findViewById(R.id.name)
        phoneEditText = v.findViewById(R.id.phone_number)
        phoneEditText.setOnClickListener(this)
        addressEditText = v.findViewById(R.id.address)
        buildingEditText = v.findViewById(R.id.home_number)
        flatEditText = v.findViewById(R.id.flat_no)

        passwordEditText = v.findViewById(R.id.password)
        passwordEditText.setOnClickListener(this)
        saveButton = v.findViewById(R.id.save_button)
        saveButton.setOnClickListener(this)

        progress = v.findViewById(R.id.progress)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)


        viewModel.progress.observe(requireActivity(), Observer {
            progress.show(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser().observe(this, Observer {
            if (it != null) {
                Log.e("USER",it.toString())
                updateView(it)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.signOutButton -> {
                viewModel.signOut(requireContext())
                requireContext().startActivity(
                    Intent(
                        context,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }

            orderStory.id -> {
                findNavController().navigate(R.id.orderHistoryFragment)
            }

            saveButton.id ->{
                if (validate()) {
                    viewModel.updateProfile(requireContext(), UserDto(
                        nameEditText.text.toString(),
                        addressEditText.text.toString(),
                        buildingEditText.text.toString(),
                        flatEditText.text.toString())
                    )
                }
            }

            phoneEditText.id -> {

            }

            passwordEditText.id -> {
                showChangeDialog()
            }
        }
    }

    private fun showChangeDialogFalse() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        builder.setCancelable(false)
        builder
            .setView(view)
            .setPositiveButton("Изменить пароль") { dialog, _ ->
                val oldPass: TextInputEditText = view.findViewById(R.id.old_password)
                val newPass: TextInputEditText = view.findViewById(R.id.new_password)
                val newPass2: TextInputEditText = view.findViewById(R.id.new_password2)

                if (oldPass.validate({s -> s.isNotEmpty()}, "Пароль не может быть пустым")
                    && newPass.validate({s -> s.isNotEmpty()}, "Пароль не может быть пустым")
                    && newPass2.validate({s -> TextUtils.equals(s, newPass.text)}, "Пароли не совпадают")){
                    viewModel.changePassword(requireContext(), dialog, oldPass.text.toString(), newPass.text.toString())
                }


            }
            .setNegativeButton("Отмена") { _, _ ->

            }

        var dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            positiveButton.setTextColor(resources.getColor(R.color.colorSecondary, null))
        else positiveButton.setTextColor(resources.getColor(R.color.colorSecondary))

        positiveButton.textSize = 14f
    }


    private fun showChangeDialog(){
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Изменить пароль", null)
            .setNegativeButton("Отмена") { _, _ -> }
            .show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))

        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val oldPass: TextInputEditText = view.findViewById(R.id.old_password)
            val newPass: TextInputEditText = view.findViewById(R.id.new_password)
            val newPass2: TextInputEditText = view.findViewById(R.id.new_password2)

            if (oldPass.validate({ s -> s.isNotEmpty() }, "Пароль не может быть пустым")
                && newPass.validate({ s -> s.isNotEmpty() }, "Пароль не может быть пустым")
                && newPass2.validate(
                    { s -> TextUtils.equals(s, newPass.text) },
                    "Пароли не совпадают"
                )
            ) {
                viewModel.changePassword(
                    requireContext(),
                    dialog!!,
                    oldPass.text.toString(),
                    newPass.text.toString()
                )
            }


        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            positiveButton.setTextColor(resources.getColor(R.color.colorSecondary, null))
        else positiveButton.setTextColor(resources.getColor(R.color.colorSecondary))

        positiveButton.textSize = 14f
    }



    private fun validate(): Boolean{
        return nameEditText.validate({ s -> s.isNotEmpty()}, "Имя пользователя не может быть пустым")
    }


    @SuppressLint("SetTextI18n")
    private fun updateView(user: User) {
        nameEditText.setText(user.firstName)
        phoneEditText.setText(user.phone)
        my_bonuses.text = "Мои бонусы ${user.bonuses} баллов"
        if (user.address!= null)addressEditText.setText(user.address)
        if (user.building != null)buildingEditText.setText(user.building)
        if (user.flat != null)flatEditText.setText(user.flat)

    }
}
