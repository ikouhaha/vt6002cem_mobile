package com.example.vt6002cem.ui.register

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.common.Validations
import com.example.vt6002cem.model.User

class RegisterViewModel : ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData(User())
    val formErrors = ObservableArrayList<String>()


    fun isFormValid(): Boolean {
        formErrors.clear()

        user.value?.apply {
            email = email?:""
            password = password?:""
            confirmPassword = confirmPassword?:""
            Validations.email(email).let { if (it != null) {formErrors.add(it) } }
            Validations.password(password).let { if (it != null) {formErrors.add(it) } }
            Validations.confirmPassword(confirmPassword,password).let { if (it != null) {formErrors.add(it) } }
        }
        // all the other validation you require
        return formErrors.isEmpty()
    }
}