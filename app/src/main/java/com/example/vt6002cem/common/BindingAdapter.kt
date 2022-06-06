package com.example.vt6002cem.adpater

import androidx.databinding.BindingAdapter
import com.example.vt6002cem.common.Validations
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("emailValidator")
fun emailValidator(view: TextInputLayout, input:String?) {
    if(input!=null){
        view.error  = Validations.email(input)
    }

}
@BindingAdapter("textValidator")
fun textValidator(view: TextInputLayout, input:String?) {
    if(input!=null){
        view.error  = Validations.text(input)
    }

}
@BindingAdapter("passwordValidator")
fun passwordValidator(view: TextInputLayout, input:String?) {
    if(input!=null){
        view.error = Validations.password(input)
    }

}

@BindingAdapter(value=["confirmPasswordValidator","passwordValidator"], requireAll = true)
fun confirmPasswordValidator(view: TextInputLayout, confirmPwd:String?, pwd:String?) {
    if(confirmPwd!=null){
        view.error = Validations.confirmPassword(confirmPwd,pwd)
    }

}

