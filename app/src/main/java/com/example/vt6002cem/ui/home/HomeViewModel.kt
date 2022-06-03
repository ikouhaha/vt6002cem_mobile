package com.example.vt6002cem.ui.home

import androidx.lifecycle.*
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.ProductFilters
import kotlinx.coroutines.*

class HomeViewModel constructor(private val repository: HomeRepository): ViewModel() {

    var productList= MutableLiveData<List<Product>>()
    var filters= MutableLiveData<ProductFilters>(ProductFilters())
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    fun getProducts(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.getProducts(filters.value!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    productList.postValue(response.body())
                    loading.value = false
                } else {
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