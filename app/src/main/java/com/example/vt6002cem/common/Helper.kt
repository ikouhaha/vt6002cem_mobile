package com.example.vt6002cem.common


import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.Config
import com.example.vt6002cem.model.EncryptedMessage
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object Helper {

    private const val SHARED_PREFS_FILENAME = "biometric_prefs"

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

    fun setStoreBoolean(context: Context?,key:String,value:Boolean,prefs: String?="store") {
        ensureNotNull(context)
        context!!.getSharedPreferences(prefs, Context.MODE_PRIVATE)
            .edit().putBoolean(key, value).apply()
    }
    fun getStoreBoolean(context: Context?,key:String,prefs: String?="store"):Boolean {
        ensureNotNull(context)
        return context!!.getSharedPreferences(prefs, Context.MODE_PRIVATE).getBoolean(key,false)
    }

    fun storeEncryptedMessage(
        context: Context,
        prefKey: String,
        encryptedMessage: EncryptedMessage
    ) {
        val json = Gson().toJson(encryptedMessage)
        context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE)
            .edit()
            .putString(prefKey, json).apply()
    }

    fun getEncryptedMessage(
        context: Context,
        prefKey: String
    ): EncryptedMessage? {
        val json = context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE)
            .getString(prefKey, null)
        return Gson().fromJson(json, EncryptedMessage::class.java)
    }

    fun Date.toDateString(format: String="yyyy/MM/dd HH:mm:ss", locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun clearEncryptedMessage(
        context: Context,
        prefKey: String
    ){
         context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE)
             .edit().remove(prefKey).apply()
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

    fun convertToBase64(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

}