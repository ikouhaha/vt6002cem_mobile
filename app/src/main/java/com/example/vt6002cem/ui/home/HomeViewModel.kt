package com.example.vt6002cem.ui.home

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.*
import com.example.vt6002cem.Config
import com.example.vt6002cem.MainActivity
import com.example.vt6002cem.adpater.ProductsApiService
import com.example.vt6002cem.adpater.UserApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.ProductFilters
import com.example.vt6002cem.model.User
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel : ViewModel() {

    var productList: MutableLiveData<ArrayList<Product>> = MutableLiveData(ArrayList())
    var filters: MutableLiveData<ProductFilters> = MutableLiveData(ProductFilters())
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    private val api = Retrofit.Builder()
        .baseUrl(Config.apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(Config.httpClient)
        .build()
        .create(ProductsApiService::class.java)

    fun onCreated() {
        loadProducts(true)
    }

    fun loadProducts(isClear:Boolean=false) {
        if(isClear){
            productList.value?.clear()
        }

        viewModelScope.launch {
            api.loadProducts(
                filters.value?.name,
                filters.value?.about,
                filters.value!!.page,
                filters.value!!.limit
            ).let { response ->
                if (response.isSuccessful) {
                    var response:ArrayList<Product> = response.body()!!
                    productList.value!!.addAll(response)


                } else {
                    errorMessage.value = response.message()
                }
            }
        }


    }
}