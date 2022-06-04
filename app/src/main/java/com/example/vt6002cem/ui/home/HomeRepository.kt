package com.example.vt6002cem.ui.home;

import com.example.vt6002cem.adpater.ProductsApiService;
import com.example.vt6002cem.model.ProductFilters

class HomeRepository constructor(private val retrofitService: ProductsApiService) {

        suspend fun getProducts(filters: ProductFilters) = retrofitService.loadProducts(filters.searchText,filters.page,filters.limit)
                
}