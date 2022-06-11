package com.example.vt6002cem.http

import com.example.vt6002cem.Config
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface UserApiService {
    @POST("users")
    suspend fun createUser(@Body user:User): Response<Any>
    @PUT("users/p/{id}")
    suspend fun changePwd(@Path("id") id:Int,@Body user:User): Response<Any>
    @PUT("users/{id}")
    suspend fun changeProfile(@Path("id") id:Int,@Body user:User): Response<Any>
    @GET("users/profile")
    suspend fun getProfile(): Response<User>

    companion object {
        var api: UserApiService? = null
        fun getInstance(token: String?): UserApiService {

            var client =
                if (token != null) Helper.getHttpTokenClient(token) else Helper.getHttpClient()
            val retrofit = Retrofit.Builder()
                .baseUrl(Config.apiUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            api = retrofit.create(UserApiService::class.java)

            return api!!
        }
    }

}
