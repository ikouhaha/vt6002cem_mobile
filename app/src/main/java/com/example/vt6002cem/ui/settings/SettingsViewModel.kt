package com.example.vt6002cem.ui.settings

import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.R
import com.example.vt6002cem.common.Validations
import com.example.vt6002cem.model.User
import com.example.vt6002cem.repositroy.UserRepository
import kotlinx.coroutines.*

class SettingsViewModel constructor(private val repository: UserRepository) : ViewModel() {

    var user: MutableLiveData<User> = MutableLiveData(User())
    val formErrors = ObservableArrayList<String>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    var isSave = MutableLiveData<Boolean>()
    private val TAG = "SettingsViewModel"

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    var job: Job? = null

//    fun getProfile(){
//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            loading.postValue(true)
//            val response = repository.getProfile()
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    loading.postValue(false)
//                    user.postValue(response.body())
//                } else {
//                    onError("Error : ${response.message()} ")
//                }
//            }
//        }
//    }

    fun isChangePwdFormValid(): Boolean {
        formErrors.clear()

        user.value?.apply {
            password = password?:""
            confirmPassword = confirmPassword?:""
            Validations.password(password).let { if (it != null) {formErrors.add(it) } }
            Validations.confirmPassword(confirmPassword,password).let { if (it != null) {formErrors.add(it) } }
        }
        // all the other validation you require
        return formErrors.isEmpty()
    }

    fun isChangeProfileFormValid(): Boolean {
        formErrors.clear()

        user.value?.apply {
            displayName = displayName?:""
            role = role?:""
            Validations.text(displayName).let { if (it != null) {formErrors.add(it) } }
            Validations.text(role).let { if (it != null) {formErrors.add(it) } }
            if(role=="staff"){
                companyCode = companyCode?:""
                Validations.text(companyCode).let { if (it != null) {formErrors.add(it) } }
            }
        }
        // all the other validation you require
        return formErrors.isEmpty()
    }

    fun changePwdFormSave(view:View) {
        if(isChangePwdFormValid()){
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                loading.postValue(true)
                val response = repository.changePwd(user.value!!.id!!,user.value!!)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        loading.postValue(false)
                    } else {
                        onError("Error : ${response.message()} ")
                    }
                }
                isSave.postValue(true)
            }
        }
    }

    fun changeProfileFormSave(view:View) {
        if(isChangeProfileFormValid()){
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                loading.postValue(true)
                val response = repository.changeProfile(user.value!!.id!!,user.value!!)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        loading.postValue(false)
                    } else {
                        onError("Error : ${response.message()} ")
                    }
                }

                isSave.postValue(true)
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        Log.d(TAG,message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun selectOption(radioGroup: RadioGroup, radioButton: View)
    {
        if(radioButton.id== R.id.staff){
            user.value?.role = "staff"
        }else if(radioButton.id== R.id.user){
            user.value?.role = "user"
        }
        radioGroup.check(radioButton.id)
    }
}