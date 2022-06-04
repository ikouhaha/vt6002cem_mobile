package com.example.vt6002cem.adpater

import com.example.vt6002cem.Config
import com.example.vt6002cem.common.Helper
import com.example.vt6002cem.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface UserApiService {
    @POST("users")
    suspend fun createUser(@Body user:User): Response<Object>


    companion object {
        var api: UserApiService? = null
        fun getInstance() : UserApiService {
            if (api == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Config.apiUrl)
                    .client(Helper.getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                api = retrofit.create(UserApiService::class.java)
            }
            return api!!
        }

    }

}
