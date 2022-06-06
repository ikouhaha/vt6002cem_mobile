package com.example.vt6002cem.repositroy;

import com.example.vt6002cem.http.ProductsApiService;
import com.example.vt6002cem.model.ProductFilters

class ProductRepository constructor(private val retrofitService: ProductsApiService) {

        suspend fun getProducts(filters: ProductFilters) = retrofitService.loadProducts(filters.searchText,filters.page,filters.limit)
        suspend fun getProdcutById(id: Int) = retrofitService.loadProductById(id)
        suspend fun getProdcutByIds(ids: Array<Int>) = retrofitService.loadProductByIds(ids)
                
}