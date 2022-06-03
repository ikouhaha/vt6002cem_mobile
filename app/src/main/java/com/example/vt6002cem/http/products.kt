package com.example.vt6002cem.adpater

import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.ProductFilters
import com.example.vt6002cem.model.User
import retrofit2.Response
import retrofit2.http.*


interface ProductsApiService {
    @GET("products")
    suspend fun loadProducts(@Query("name") name:String?
                             ,@Query("about") about:String?
                             ,@Query("page") page:Int
                             ,@Query("limit") limit:Int
    ): Response<ArrayList<Product>>


}
