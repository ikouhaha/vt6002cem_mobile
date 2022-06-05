package com.example.vt6002cem.ui.shoppingCart;

import com.example.vt6002cem.adpater.ProductsApiService;
import com.example.vt6002cem.model.ProductFilters

class ShoppingCartRepository constructor(private val retrofitService: ProductsApiService) {

        suspend fun getProducts(filters: ProductFilters) = retrofitService.loadProducts(filters.searchText,filters.page,filters.limit)
        suspend fun getProdcutById(id: Int) = retrofitService.loadProductById(id)
                
}