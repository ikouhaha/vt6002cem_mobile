package com.example.vt6002cem.common

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock

import java.security.KeyStore


class CryptographyUtilTest {

    @Mock
    var context: Context? = mock(Context::class.java)

    @Mock
    var keyStoreMock: KeyStore = mock(KeyStore::class.java);


    @Before
    @Throws(Exception::class)
    fun setUp() {
        //MockitoAnnotations.initMocks(this) //create all @Mock objetcs
//        PowerMockito.mockStatic(KeyStore::class.java)
//        `when`(KeyStore.getInstance(anyString())).thenReturn(keyStoreMock)
    }

    @Test
    fun getOrCreateSecretKey() {
        //test flow work only
        var scKey = CryptographyUtil.getOrCreateSecretKey("login")

    }

    @Test
    fun getCipher() {
    }

    @Test
    fun getInitializedCipherForEncryption() {
    }

    @Test
    fun getInitializedCipherForDecryption() {
    }

    @Test
    fun encryptData() {
    }

    @Test
    fun decryptData() {
    }
}