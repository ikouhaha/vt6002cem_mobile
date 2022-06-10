package com.example.vt6002cem.ui.post

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.common.Validations
import com.example.vt6002cem.databinding.FragmentCreatePostBinding
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import kotlinx.coroutines.*

class EditPostViewModel (private val repository: ProductRepository) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val product = MutableLiveData<Product>(Product())
    var job: Job? = null
    val formErrors = ObservableArrayList<String>()
    val actionTextToast = MutableLiveData<String>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }



    fun getProduct(id:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.getProdcutById(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        product.postValue(it)
                    }
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun createPost(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.create(product.value!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        //product.postValue(it)
                        actionTextToast.postValue("create successfully")
                    }
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun editPost(id:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.edit(id,product.value!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        //product.postValue(it)
                        actionTextToast.postValue("edit successfully")
                    }
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun isFormValid(): Boolean {
        formErrors.clear()

        product.value?.apply {
            name = name?:""
            about = about?:""
            Validations.text(name).let { if (it != null) {formErrors.add(it) } }
            Validations.text(about).let { if (it != null) {formErrors.add(it) } }
            Validations.number(price).let { if (it != null) {formErrors.add(it) } }
        }
        // all the other validation you require
        return formErrors.isEmpty()
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}