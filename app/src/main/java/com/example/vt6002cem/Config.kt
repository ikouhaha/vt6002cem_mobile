package com.example.vt6002cem

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object Config {
    var apiUrl:String = "http://10.0.2.2:10888/api/v1/"


    var httpClient = OkHttpClient.Builder()
    .callTimeout(2, TimeUnit.MINUTES)
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}