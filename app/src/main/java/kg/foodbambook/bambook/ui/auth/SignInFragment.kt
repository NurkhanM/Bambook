package kg.foodbambook.bambook.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.santalu.maskedittext.MaskEditText
import kg.foodbambook.bambook.Constants
import kg.foodbambook.kg.R
import kg.foodbambook.bambook.cursorToEnd
import kg.foodbambook.kg.ui.auth.resetpassword.RestorePassActivity
import kg.foodbambook.kg.ui.main.MainActivity
import kg.foodbambook.bambook.ui.utils.KeyboardUtil.Companion.hideKeyboard
import kg.foodbambook.bambook.utils.ToastUtils
import kotlinx.android.synthetic.main.sign_in_fragment.*


class SignInFragment : BaseFragment(), View.OnClickListener {


    override val title: String
        get() = "Вход"

    companion object {
        fun newInstance() = SignInFragment()
        private val TAG = SignInFragment::class.java.simpleName
    }


    private lateinit var phoneEditText: MaskEditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var restorePass: TextView

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val v = inflater.inflate(R.layout.sign_in_fragment, container, false)

        phoneEditText = v.findViewById(R.id.et_auth)
        phoneEditText.cursorToEnd()
        passwordEditText = v.findViewById(R.id.password_edit_text)
        signInButton = v.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener(this)
        restorePass = v.findViewById(R.id.restore_pass)
        restorePass.setOnClickListener(this)
        val skipAuth: TextView = v.findViewById(R.id.cancel)
        skipAuth.setOnClickListener(this)

        return v
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        init()
    }

    private fun init() {
        viewModel.getState().observe(requireActivity(), Observer {
            when (val value = it!!) {
                Constants.STATE_SIGNIN_SUCCESS -> {
                    val i = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(i)
                    activity?.finish()
                }
                Constants.STATE_LOADING -> {
                    progress.visibility = View.VISIBLE
                    hideKeyboard(requireActivity())
                }
                Constants.STATE_AUTH_ERROR -> {
                    progress.visibility = View.GONE
                }
                Constants.STATE_NO_INTERNET -> {
                    progress.visibility = View.GONE
                    updateUI(value)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in_button -> {
                if (!validate()) return
                Log.e(TAG, "signInButtonCLicked")
                val phone = phoneEditText.text.toString().replace("(", "").replace(")", "").replace(" ", "")
                Log.e(SignInViewModel.TAG, "onFailure pp = $phone")
                Log.e(SignInViewModel.TAG, "onFailure t = ${passwordEditText.text}")
                viewModel.login(requireContext(), phone, passwordEditText.text.toString())


            }

            R.id.cancel -> {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

            R.id.restore_pass -> {
                startActivity(Intent(requireContext(), RestorePassActivity::class.java))
            }
        }
    }

    private fun validate(): Boolean {
        var phone = phoneEditText.text.toString()
        val password = passwordEditText.text.toString()
        phone = phone.replace("(", "")
        phone = phone.replace(")", "")
        phone = phone.replace(" ", "")
        var check = true

        if (phone.isEmpty()) {
            phoneEditText.error = "Вы не ввели номер телефона"
            check = false
        } else if (phone.length < 10 || phone.length > 13) {
            phoneEditText.error = "Введите валидный номер"
            check = false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Пароль не может быть пустым"
            check = false
        }
        return check
    }

    private fun updateUI(state: Int) {
        when (state) {
            Constants.STATE_NO_INTERNET -> {
                ToastUtils.showToast(requireContext(), "No Internet Connection.")
            }
        }
    }
}
