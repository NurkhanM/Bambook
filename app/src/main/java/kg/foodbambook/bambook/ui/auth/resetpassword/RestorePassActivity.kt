package kg.foodbambook.bambook.ui.auth.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kg.foodbambook.bambook.*
import kg.foodbambook.bambook.Constants.STATE_CANCEL
import kg.foodbambook.bambook.Constants.STATE_CODE_SENT
import kg.foodbambook.bambook.Constants.STATE_NO_INTERNET
import kg.foodbambook.bambook.Constants.STATE_NUMBER_VERIFIED
import kotlinx.android.synthetic.main.activity_restore_pass.*

class RestorePassActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var  mViewModel:RestorePassViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_pass)
        phone_number_edit_text.cursorToEnd()
        mViewModel = ViewModelProviders.of(this).get(RestorePassViewModel::class.java)
        mViewModel.getState().observe(this, Observer {
            when (it!!) {
                Constants.STATE_SUCCESS -> {
                    finish()
                }
                Constants.STATE_LOADING -> {
                    progress.visible()
                    hideKeyboard()
                }
                Constants.STATE_INCORRECT_CODE-> {
                    progress.gone()
                    code_field.error = "Неправильно введен код"
                }
                Constants.STATE_CODE_SENT -> {
                    progress.gone()
                    updateUI(STATE_CODE_SENT)
                }
                Constants.STATE_NUMBER_VERIFIED -> {
                    progress.gone()
                    updateUI(STATE_NUMBER_VERIFIED)
                }
                Constants.STATE_NUMBER_ALREADY_REGISTERED ->{
                    progress.gone()
                    phone_number_edit_text.error = "Пользователь с таким номером не сущестует"
                }
                Constants.STATE_NO_INTERNET -> {
                    progress.gone()
                    updateUI(STATE_NO_INTERNET)
                }
            }
        })

        restore_button.setOnClickListener(this)
        confirm_button.setOnClickListener(this)
        save_button.setOnClickListener(this)
        cancel.setOnClickListener(this)
        resendCodeButton.setOnClickListener(this)

    }

    private fun updateUI(state: Int) {
        when (state) {
            Constants.STATE_CODE_SENT ->{
                start_form.gone()
                verify_phone_layout.visible()
                restore_pass_layout.gone()
            }
            Constants.STATE_NUMBER_VERIFIED -> {
                start_form.gone()
                verify_phone_layout.gone()
                restore_pass_layout.visible()
            }

            Constants.STATE_CANCEL -> {
                if (start_form.visibility==View.VISIBLE){
                    finish()
                }else {
                    start_form.visible()
                    verify_phone_layout.gone()
                    restore_pass_layout.gone()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if(v == null) return

        when (v.id) {
            restore_button.id ->{
                if (validatePhone()) {
                    mViewModel.checkNumber(this, phone_number_edit_text.text.toString())
                }
            }

            confirm_button.id -> {
                if (code_field.validate({s -> s.isNotEmpty()}, "Введите код из СМС"))
                    mViewModel.verifyPhoneNumberWithCode(code_field.text.toString())
                //updateUI(STATE_RESTORED_SUCCESS)
            }

            save_button.id -> {
                if(validatePassword())
                    mViewModel.sendNewPassword(this, password1.text.toString(), password2.text.toString(), phone_number_edit_text.text.toString())
            }

            cancel.id -> {
                mViewModel.signOut()
                updateUI(STATE_CANCEL)
            }

            resendCodeButton.id -> {
                if (validatePhone()) {
                    mViewModel.resendVerificationCode(this, phone_number_edit_text.text.toString())
                }
            }
        }
    }


    private fun validatePhone(): Boolean{
        return phone_number_edit_text.validate({s -> s.isNotEmpty()}, "Введите номер")
    }

    private fun validatePassword(): Boolean {
        return (
                password1.validate({s-> s.isNotEmpty()}, "Введите пароль") &&
                        password2.validate({s -> s == password1.text.toString() }, "Пароли не совпадают"))
    }

}
