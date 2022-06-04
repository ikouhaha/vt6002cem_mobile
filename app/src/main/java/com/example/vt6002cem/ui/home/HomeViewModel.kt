package com.example.vt6002cem.ui.home

import android.text.Editable
import androidx.lifecycle.*
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.ProductFilters
import kotlinx.coroutines.*

class HomeViewModel constructor(private val repository: HomeRepository): ViewModel() {

    var productList= MutableLiveData<ArrayList<Product>>()
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

                    response.body()?.let {
                        if(productList.value.isNullOrEmpty()){
                            productList.postValue(it)
                        }else{
                            val list: ArrayList<Product> = ArrayList(productList.value!!)
                            list.addAll(it)
                            productList.postValue(list)
                            //productList.apply { value?.addAll(it) }
                        }

                    }
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun loadMore(){
        filters.apply {
            value?.page = value?.page!!+1
        }
        getProducts()
    }

    fun clearList(){
        filters.apply {
            value = ProductFilters()
        }
        productList.value?.clear()
    }
    fun search(s: Editable){
        filters.value?.searchText = if (s.isNullOrEmpty()) null else s.toString()
        getProducts()
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