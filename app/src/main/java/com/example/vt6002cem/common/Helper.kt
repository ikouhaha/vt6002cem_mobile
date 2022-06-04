package com.example.vt6002cem.common


import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import com.example.vt6002cem.Config
import com.example.vt6002cem.R
import com.google.android.gms.tasks.Tasks
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


object Helper {


    fun ensureNotNull(context: Context?) {
        requireNotNull(context) { "Context is null." }
    }

    fun getStringFromName(context: Context?,name: String):String {
        ensureNotNull(context)
        return context!!.getString(
            context.resources.getIdentifier(
                name,
                "string",
                context.packageName
            ))
    }

    fun getStoreString(context: Context?,key:String, prefs: String?="store"): String {
        ensureNotNull(context)
        return context!!.getSharedPreferences(prefs, Context.MODE_PRIVATE).getString(key,"").toString()
    }

    fun setStoreString(context: Context?,key:String,value:String,prefs: String?="store") {
        ensureNotNull(context)
        context!!.getSharedPreferences(prefs, Context.MODE_PRIVATE)
            .edit().putString(key, value).apply()
    }


    fun getErrorMsg(response: Response<Object>){


    }

    fun getHttpTokenClient(token:String):OkHttpClient{
        var httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                    val request: Request =
                        chain.request().newBuilder().addHeader("Authorization", token).build()
                    chain.proceed(request)
            }
            .callTimeout(Config.callTimeout, TimeUnit.MINUTES)
            .connectTimeout(Config.connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(Config.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(Config.writeTimeout, TimeUnit.SECONDS)
            .build()

        return httpClient
    }

    fun getHttpClient():OkHttpClient{
        var httpClient = OkHttpClient.Builder()
            .callTimeout(Config.callTimeout, TimeUnit.MINUTES)
            .connectTimeout(Config.connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(Config.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(Config.writeTimeout, TimeUnit.SECONDS)
            .build()

        return httpClient
    }

    suspend fun getToken():String {
        if(Firebase.auth.currentUser==null){
            return ""
        }

        var token:String? = Tasks.await(Firebase.auth.currentUser!!.getIdToken(true)).token

        if(token==null){
            return ""
        }
        return token
    }

}