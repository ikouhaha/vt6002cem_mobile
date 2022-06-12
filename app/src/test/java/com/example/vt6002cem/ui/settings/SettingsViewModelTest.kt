package com.example.vt6002cem.ui.settings

import android.view.View
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
import org.mockito.Mockito.mock

class SettingsViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var user: User = User()
    private lateinit var userService: UserApiService
    private lateinit var repository: UserRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        userService = mock(UserApiService.getInstance("")::class.java)
        repository = UserRepository(userService)
        viewModel = SettingsViewModel(repository)
        user.email = "dennisauyeung@legatosolutions.com"
        user.avatarUrl = "https://lh3.googleusercontent.com/a/AATXAJyzetG1ehaZy7LI0Wanz3LIuL87iETNNtLrIxPo=s96-c"
        user.displayName = "Dennis Au-Yeung"
        user.fid = "ah0N45HaVhh13c94SdUL9AS6l6f2"
        user.role = "staff"
        user.id = 27
        user.companyCode = "111"

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
        val expect = "error"
        viewModel.errorMessage.postValue(expect)
        assertEquals(expect, viewModel.errorMessage.value)
    }

    @Test
    fun isSave() {
        val expect = true
        setSave()
        assertEquals(expect, viewModel.isSave.value)
    }

    @Test
    fun setSave() {
        val expect = true
        viewModel.isSave.postValue(expect)
    }


    @Test
    fun isChangePwdFormValid() {
        val user = User()
        user.id = this.user.id
        user.password = "ikouhaha765"
        user.confirmPassword = "ikouhaha765"
        viewModel.user.postValue(user)
        val result = viewModel.isChangePwdFormValid()
        assertEquals(true, result)

    }

    @Test
    fun isChangeProfileFormValid() {
        viewModel.user.postValue(user)
        var result = viewModel.isChangeProfileFormValid()
        assertEquals(true, result)
    }

    @Test
    fun changePwdFormSave() {
        var view = mock(View::class.java)
        viewModel.changePwdFormSave(view)
    }

    @Test
    fun changeProfileFormSave() {
        var view = mock(View::class.java)
        viewModel.changeProfileFormSave(view)
    }

}