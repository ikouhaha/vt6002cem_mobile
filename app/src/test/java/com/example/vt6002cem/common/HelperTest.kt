package com.example.vt6002cem.common

import android.content.Context
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class HelperTest {

    @Test
    fun ensureNotNull() {
        val context = Mockito.mock(Context::class.java)
        Helper.ensureNotNull(context)

    }
    @Test
    fun getStringFromName() {
    }

    @Test
    fun getStoreString() {
    }

    @Test
    fun setStoreString() {
    }

    @Test
    fun setStoreBoolean() {
    }

    @Test
    fun getStoreBoolean() {
    }

    @Test
    fun storeEncryptedMessage() {
    }

    @Test
    fun getEncryptedMessage() {
    }

    @Test
    fun toDateString() {
    }

    @Test
    fun getCurrentDateTime() {
    }

    @Test
    fun clearEncryptedMessage() {
    }

    @Test
    fun getHttpTokenClient() {
    }

    @Test
    fun getHttpClient() {
    }

    @Test
    fun convertToBase64() {
    }
}