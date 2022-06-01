package com.example.vt6002cem.adpater

import com.example.vt6002cem.model.User
import retrofit2.Response
import retrofit2.http.*


interface UserApiService {
    @POST("users")
    suspend fun createUser(@Body user:User): Response<Object>


}
