package com.example.vt6002cem.http

import com.example.vt6002cem.model.User
import retrofit2.Response
import retrofit2.http.*


interface AuthApiService {


    @POST("auth/firebase/token")
    suspend fun authFirebaseToken(@Header("Authorization") token:String): Response<String>
}
