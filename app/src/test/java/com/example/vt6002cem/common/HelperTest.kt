package com.example.vt6002cem.common

import android.content.Context
import android.content.SharedPreferences
import com.example.vt6002cem.Config
import com.example.vt6002cem.common.Helper.toDateString
import com.example.vt6002cem.model.EncryptedMessage
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(JUnit4::class)
class HelperTest {

    @Mock
    var context: Context? = mock(Context::class.java)

    @Mock
    var sharedPrefs: SharedPreferences? = null

    @Mock
    var sharedPrefsEditor: SharedPreferences.Editor? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this) //create all @Mock objetcs
        `when`(context?.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(sharedPrefs)
        `when`(sharedPrefs?.edit()).thenReturn(sharedPrefsEditor)
        `when`(sharedPrefsEditor?.putString(anyString(), anyString()))
            .thenReturn(sharedPrefsEditor)
        `when`(sharedPrefsEditor?.putBoolean(anyString(), anyBoolean()))
            .thenReturn(sharedPrefsEditor)
    }

    @Test
    fun ensureNotNull() {
        Helper.ensureNotNull(context)

    }

    //set first
    @Test
    fun setStoreString() {
        val userId = "A12345678"
        val preKey = "USER_ID"
        Helper.setStoreString(context, preKey, userId)
        verify(sharedPrefsEditor)?.putString(argThat { key -> key == preKey },
            argThat { value -> value == userId })
        verify(sharedPrefsEditor)?.apply()
    }

    @Test
    fun getStoreString() {
        val userId = "A12345678"
        val preKey = "USER_ID"
        `when`(sharedPrefs!!.getString(preKey, "")).thenReturn(userId)
        val result = Helper.getStoreString(context, preKey)
        assertEquals(userId, result);
    }


    @Test
    fun setStoreBoolean() {
        val testValue = true
        val preKey = "IS_SAVE"
        Helper.setStoreBoolean(context, preKey, testValue)
        verify(sharedPrefsEditor)?.putBoolean(
            argThat { key -> key == preKey },
            booleanThat { value -> value == testValue })
        verify(sharedPrefsEditor)?.apply()
    }

    @Test
    fun getStoreBoolean() {
        val value = true
        val preKey = "canSave"
        `when`(sharedPrefs!!.getBoolean(preKey, false)).thenReturn(value)
        val result = Helper.getStoreBoolean(context, preKey)
        assertEquals(value, result)
    }

    @Test
    fun storeEncryptedMessage() {


        val encryptedMessage = EncryptedMessage("byte1".toByteArray(),"byte2".toByteArray())
        val keyname = "key_name"
        Helper.storeEncryptedMessage(
            context!!,
            keyname,
            encryptedMessage
        )
        verify(sharedPrefsEditor)?.putString(argThat { key -> key == keyname },
            argThat { value -> value == Gson().toJson(encryptedMessage) })
        verify(sharedPrefsEditor)?.apply()

    }

    @Test
    fun getEncryptedMessage() {
        val expect = EncryptedMessage("byte1".toByteArray(),"byte2".toByteArray())
        var expectString = Gson().toJson(expect)
        val keyname = "key_name"
        `when`(sharedPrefs!!.getString(keyname, null)).thenReturn(expectString)
        val result = Helper.getEncryptedMessage(context!!, keyname)
        assertEquals(expect, result)
    }

    @Test
    fun toDateString() {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val expect = "2022/06/11 19:40:01"
        val date = sdf.parse(expect)
        val result = date.toDateString()
        assertEquals(expect, result)

    }

    @Test
    fun getCurrentDateTime() {
        val result = Helper.getCurrentDateTime()
        val expect = result
        assertEquals(expect, result)
    }


    @Test
    fun getHttpTokenClient() {

    }

    @Test
    fun getHttpClient() {
        //check build success
        var result = Helper.getHttpClient()
        assertEquals(Config.callTimeout*1000*60, result.callTimeoutMillis.toLong()) //minutes
        assertEquals(Config.connectionTimeout*1000, result.connectTimeoutMillis.toLong()) //seconds
        assertEquals(Config.readTimeout*1000, result.readTimeoutMillis.toLong())//seconds
        assertEquals(Config.writeTimeout*1000, result.writeTimeoutMillis.toLong())//seconds

    }

}