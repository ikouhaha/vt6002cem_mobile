package com.example.vt6002cem.ui.shoppingCart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ShoppingCartViewModel (private val repository: ProductRepository) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    var productList= MutableLiveData<ArrayList<Product>>()
    var deleteIds= MutableLiveData<ArrayList<Int>>(arrayListOf())
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ShoppingCartViewModel", throwable.toString())
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    fun getProducts(ids:Array<Int> = arrayOf() ){
        if(loading.value==true){
            return
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = repository.getProdcutByIds(ids)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        productList.postValue(it)

                        ids.let { array->
                            deleteIds.value?.clear()
                            for(id in array){
                                val find = it.find { product->product.id==id}
                                if(find==null){
                                    deleteIds.value?.add(id)
                                }
                            }
                        }

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