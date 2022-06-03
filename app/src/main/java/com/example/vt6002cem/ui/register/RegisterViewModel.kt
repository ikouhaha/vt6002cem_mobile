package com.example.vt6002cem.ui.register



import android.util.Log
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.example.vt6002cem.adpater.AuthApiService
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.common.Validations
import com.example.vt6002cem.model.User
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterViewModel constructor(private val repository: RegisterRepository) : ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData(User())
    val formErrors = ObservableArrayList<String>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val isSuccessRegister = MutableLiveData<Boolean>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    var job: Job? = null

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

    fun signUp(view: View){
        if(isFormValid()){

        }
    }

    fun googleSiupUp(user:User){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.createUser(user)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    loading.value = false
                    isSuccessRegister.value = true
                } else {
                    isSuccessRegister.value = false
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }


    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


}