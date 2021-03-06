package com.example.vt6002cem.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vt6002cem.http.UserApiService
import com.example.vt6002cem.model.User
import com.example.vt6002cem.repositroy.UserRepository
import com.example.vt6002cem.ui.login.LoginViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*

class RegisterViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var user: User = User()
    private lateinit var userService: UserApiService
    private lateinit var repository: UserRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        userService = mock(UserApiService.getInstance("")::class.java)
        repository = UserRepository(userService)
        viewModel = RegisterViewModel(repository)
        user.email = "ikouhaha888@gmail.com"
        user.password = "ikouhaha765"
        user.confirmPassword = "ikouhaha765"
    }

    @Test
    fun getUser() {
        val expect = user
        setUser()
        assertEquals(expect, viewModel.user.value)
    }

    @Test
    fun setUser() {
        val expect = user
        viewModel.user.postValue(expect)
    }

    @Test
    fun getFormErrors() {
        val expect = "error1"
        viewModel.formErrors.add(expect)
        assertEquals(expect, viewModel.formErrors[0])
    }

    @Test
    fun getLoading() {
        val expect = true
        viewModel.loading.postValue(expect)
        assertEquals(expect, viewModel.loading.value)
    }

    @Test
    fun getErrorMessage() {
        var expect = "error"
        viewModel.errorMessage.postValue(expect)
        assertEquals(expect, viewModel.errorMessage.value)
    }

    @Test
    fun isSuccessRegister() {
        var expect = true
        viewModel.isSuccessRegister.postValue(expect)
        assertEquals(expect, viewModel.isSuccessRegister.value)
    }

    @Test
    fun isFormValid() {
        setUser()
        val result = viewModel.isFormValid()
        assertEquals(true, result)
    }

    @Test
    fun signUp() {
        viewModel.signUp(user)
    }

    @Test
    fun googleSiupUp() {
        viewModel.googleSiupUp(user)
    }
}