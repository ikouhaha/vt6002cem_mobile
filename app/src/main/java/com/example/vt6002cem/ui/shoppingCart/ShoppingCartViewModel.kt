package com.example.vt6002cem.ui.shoppingCart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import kotlinx.coroutines.*

class ShoppingCartViewModel (private val repository: ProductRepository) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    var productList= MutableLiveData<ArrayList<Product>>()
    var ids= MutableLiveData<Array<Int>>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    fun getProducts(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.getProdcutByIds(ids.value!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        productList.postValue(it)
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