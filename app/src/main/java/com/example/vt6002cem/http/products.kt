package com.example.vt6002cem.adpater

import com.example.vt6002cem.Config
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.ProductFilters
import com.example.vt6002cem.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ProductsApiService {
    @GET("products")
    suspend fun loadProducts(
        @Query("searchText") searchText: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ArrayList<Product>>

    @GET("product/{id}")
    suspend fun loadProductById(
        @Path("id") id:Int,
    ): Response<Product>


    companion object {
        var api: ProductsApiService? = null

        fun getInstance(token: String?): ProductsApiService {
            if (api == null) {

                var client =
                    if (token != null) Helper.getHttpTokenClient(token) else Helper.getHttpClient()
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.apiUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                api = retrofit.create(ProductsApiService::class.java)
            }
            return api!!
        }


    }

}
