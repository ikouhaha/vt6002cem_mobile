package com.example.vt6002cem.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.User

class HomeViewModel : ViewModel() {

    var productList: MutableLiveData<List<Product>> = MutableLiveData()

}