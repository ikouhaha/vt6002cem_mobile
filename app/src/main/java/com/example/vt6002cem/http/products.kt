package com.example.vt6002cem.http

import com.example.vt6002cem.Config
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.model.Product
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

    @GET("products/{id}")
    suspend fun loadProductById(
        @Path("id") id:Int,
    ): Response<Product>

    @GET("products/ids")
    suspend fun loadProductByIds(
        @Query("id") id:Array<Int>,
    ): Response<ArrayList<Product>>

    @POST("products")
    suspend fun create(
        @Body product:Product,
    ): Response<Any>

    @PUT("products/{id}")
    suspend fun edit(
        @Path("id") id:Int,
        @Body product:Product,
    ): Response<Any>

    @DELETE("products/{id}/{companyCode}")
    suspend fun delete(
        @Path("id") id:Int,
        @Path("companyCode") companyCode:String
    ): Response<Any>

    companion object {
        var api: ProductsApiService? = null
        fun getInstance(token: String?): ProductsApiService {
            var client =
                if (token != null) Helper.getHttpTokenClient(token) else Helper.getHttpClient()
            var retrofit = Retrofit.Builder()
                .baseUrl(Config.apiUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            api = retrofit.create(ProductsApiService::class.java)

            return api!!
        }
    }

}
