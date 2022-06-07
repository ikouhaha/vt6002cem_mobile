package com.example.vt6002cem.ui.post

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import kotlinx.coroutines.*

class CreatePostViewModel (private val repository: ProductRepository) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val product = MutableLiveData<Product>()
    var job: Job? = null
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

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}