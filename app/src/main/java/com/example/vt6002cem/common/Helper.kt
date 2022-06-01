package com.example.vt6002cem.common


import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import com.example.vt6002cem.R
import com.google.android.gms.tasks.Tasks
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Response


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

    fun block(context: Context?){
        ensureNotNull(context)
        (context as Activity).getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun unblock(context: Context?){
        ensureNotNull(context)
        (context as Activity).getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    fun getErrorMsg(response: Response<Object>){


    }

}