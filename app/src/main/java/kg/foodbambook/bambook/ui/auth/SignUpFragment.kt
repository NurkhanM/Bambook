package kg.foodbambook.bambook.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.santalu.maskedittext.MaskEditText
import kg.foodbambook.bambook.Constants
import kg.foodbambook.bambook.Constants.STATE_AUTH_CANCEL
import kg.foodbambook.bambook.Constants.STATE_CODE_SENT
import kg.foodbambook.bambook.Constants.STATE_NO_INTERNET

import kg.foodbambook.bambook.R
import kg.foodbambook.bambook.cursorToEnd
import kg.foodbambook.bambook.toast
import kg.foodbambook.bambook.ui.main.MainActivity
import kg.foodbambook.bambook.ui.utils.KeyboardUtil.Companion.hideKeyboard
import kg.foodbambook.bambook.utils.ToastUtils
import kotlinx.android.synthetic.main.sign_up_fragment.*

class SignUpFragment : BaseFragment(), View.OnClickListener {


    private lateinit var viewModel: SignUpViewModel
    private lateinit var nameEditText: EditText
    private lateinit var phoneNumberEditText: MaskEditText
    private lateinit var passwordEditText: EditText
    private lateinit var password2EditText: EditText
    private lateinit var resendCodeButton: View
    private lateinit var fieldVerificationCode: EditText
    private lateinit var skipAuthButton: View
    private lateinit var signUpButton: Button
    private lateinit var buttonverifyPhone: Button
    private lateinit var cancelButton: TextView

    override val title: String
        get() = "Регистрация"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.sign_up_fragment, container, false)

        nameEditText = v.findViewById(R.id.name_edit_text)
        phoneNumberEditText = v.findViewById(R.id.et_auth)
        phoneNumberEditText.cursorToEnd()
        passwordEditText = v.findViewById(R.id.password_edit_text)
        password2EditText = v.findViewById(R.id.password2_edit_text)
        skipAuthButton = v.findViewById(R.id.skip_auth_button)
        resendCodeButton = v.findViewById(R.id.resendCodeButton)
        fieldVerificationCode = v.findViewById(R.id.code_field)
        signUpButton = v.findViewById(R.id.sign_up_button)
        buttonverifyPhone = v.findViewById(R.id.confirm_button)
        cancelButton = v.findViewById(R.id.cancel)
        cancelButton.setOnClickListener(this)
        signUpButton.setOnClickListener(this)
        buttonverifyPhone.setOnClickListener(this)
        skipAuthButton.setOnClickListener(this)
        return v
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        viewModel.signOut()
        init()
    }

    private fun init() {
        viewModel.getState().observe(requireActivity(), Observer {
            when (it!!) {
                Constants.STATE_SIGNIN_SUCCESS -> {
                    context?.startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                }
                Constants.STATE_LOADING -> {
                    progress.visibility = View.VISIBLE
                    hideKeyboard(requireActivity())
                }
                Constants.STATE_STOP_LOADING -> {
                    progress.visibility = View.GONE
                    hideKeyboard(requireActivity())
                }
                Constants.STATE_AUTH_ERROR-> {
                    progress.visibility = View.GONE
                    fieldVerificationCode.error = "Неправильно введен код"
                }
                Constants.STATE_CODE_SENT -> {
                    progress.visibility = View.GONE
                    updateUI(STATE_CODE_SENT)
                }
                Constants.STATE_NO_INTERNET ->
                    updateUI(STATE_NO_INTERNET)

                Constants.STATE_NUMBER_ALREADY_REGISTERED->{
                    activity?.toast("Пользователь с таким номером уже сущестует")
                }
            }
        })
    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.sign_up_button -> {
                if (!validateAuthForm()) {
                    return
                }
                var phoneNumber = phoneNumberEditText.text.toString()
                val password = passwordEditText.text.toString()
                phoneNumber = phoneNumber.replace("(", "")
                phoneNumber = phoneNumber.replace(")", "")
                phoneNumber = phoneNumber.replace(" ", "")
                Log.e("phoneNumber", phoneNumber)
                viewModel.checkNumber(requireActivity(), phoneNumber, password)
            }

            R.id.confirm_button -> {
                val code = fieldVerificationCode.text.toString()
                if (TextUtils.isEmpty(code)) {
                    fieldVerificationCode.error = "Поле не может быть пустым."
                    return
                }
                viewModel.verifyPhoneNumberWithCode(code)
            }

            R.id.resendCodeButton -> {
                viewModel.resendVerificationCode(phoneNumberEditText.text.toString(), requireActivity())
            }

            skipAuthButton.id -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }

            R.id.cancel -> {
                updateUI(STATE_AUTH_CANCEL)
            }
        }
    }

    private fun validateAuthForm(): Boolean {
        var phoneNumber = phoneNumberEditText.text.toString()
        val userName = nameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val password2 = password2EditText.text.toString()
        phoneNumber = phoneNumber.replace("(", "")
        phoneNumber = phoneNumber.replace(")", "")
        phoneNumber = phoneNumber.replace(" ", "")
        Log.e("phoneNumber", phoneNumber)

        if (phoneNumber.length<13) {
            phoneNumberEditText.error = "Введите номер телефона!"
            return false
        }

        if (TextUtils.isEmpty(userName)) {
            nameEditText.error = "Введите Имя!"
            return false
        }

        if (TextUtils.isEmpty(password) or TextUtils.isEmpty(password2)) {
            passwordEditText.error = "!"
            password2EditText.error = "!"
            return false
        }

        if (!TextUtils.equals(password, password2)) {

            passwordEditText.error = "!"
            password2EditText.error = "Пароли не совпадают"
            return false
        }

        return true
    }


    private fun updateUI(uiState: Int) {
        when (uiState) {
            STATE_CODE_SENT -> {
                sign_up_form.visibility = View.GONE
                verify_phone_layout.visibility = View.VISIBLE
            }


            STATE_NO_INTERNET -> {
                ToastUtils.showToast(requireContext(), "Интернет недоступен!")
                resetAuthUI()
            }

            STATE_AUTH_CANCEL -> {
                resetAuthUI()
            }
        }

    }

    private fun resetAuthUI(){
        resetEditText(phoneNumberEditText, nameEditText, passwordEditText, password2EditText, code_field)
        sign_up_form.visibility = View.GONE
        verify_phone_layout.visibility = View.VISIBLE
    }

    private fun resetEditText(vararg views: EditText){
        for (v in views) {
            v.setText("")
        }
    }




    companion object {
        fun newInstance() = SignUpFragment()

    }
}
