package com.example.vt6002cem.common

import android.content.Context


object Helper {

    fun ensureNotNull(context: Context?) {
        requireNotNull(context) { "Context is null." }
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
}