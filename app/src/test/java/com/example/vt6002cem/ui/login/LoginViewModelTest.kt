package com.example.vt6002cem.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.http.UserApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.User
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.repositroy.UserRepository
import com.example.vt6002cem.ui.post.CreatePostViewModel
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

class LoginViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var user: User = User()
    private lateinit var userService: UserApiService
    private lateinit var repository: UserRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        //userService = Mockito.mock(ProductsApiService.getInstance("")::class.java)
        //repository = UserRepository(userService)
        viewModel = LoginViewModel()
        user.email = "ikouhaha888@gmail.com"
        user.password = "ikouhaha765"

    }

    @Test
    fun getPassword() {
        val expect = user.password
        setPassword()
        assertEquals(expect, viewModel.password.value)
    }

    @Test
    fun setPassword() {
        val expect = user.password
        viewModel.password.postValue(expect)
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
    fun isFormValid() {
        setUser()
        val result = viewModel.isFormValid()
        assertEquals(true, result)
    }
}