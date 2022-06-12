package com.example.vt6002cem.ui.editPost

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.post.CreatePostViewModel
import com.example.vt6002cem.ui.post.EditPostViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.stubbing.OngoingStubbing

class EditPostViewModelTest {

    var product: Product = Product()
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    private lateinit var productService: ProductsApiService
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: EditPostViewModel

//    @Mock
//    var viewModel = mock(EditPostViewModel::class.java)



    @Before
    fun setUp() {

        productService = mock(ProductsApiService.getInstance("")::class.java)
        repository = ProductRepository(productService)
        viewModel = EditPostViewModel(repository)
        product.id = 3
        product.companyCode = "111"
        product.imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mPMSkkxBgAD2QFnQD1bxwAAAABJRU5ErkJggg=="
        product.canDelete = true
        product.canEdit = true
        product.about = "aaaaaaaa"
        product.name = "Qipao 3"
        product.price = 27000
//        `when`(productMock).thenReturn(product)
//        `when`(viewModelMock?.createPost())
//            .thenReturn(Observable.just)
        //viewModel = CreatePostViewModel(repository)

    }

    @Test
    fun getErrorMessage() {
        var expect = "error"
        viewModel.errorMessage.postValue(expect)
        assertEquals(expect, viewModel.errorMessage.value)
    }

    @Test
    fun getLoading() {
        viewModel.loading.postValue(true)
        assertEquals(true, viewModel.loading.value)
    }

    @Test
    fun getProduct() {
        val expect = product
        viewModel.product.postValue(expect)
        assertEquals(expect, viewModel.product.value)
    }

    @Test
    fun getFormErrors() {
        val expect = "error1"
        viewModel.formErrors.add(expect)
        assertEquals(expect, viewModel.formErrors[0])
    }

    @Test
    fun getActionTextToast() {
        var expect = "save successsfully"
        viewModel.actionTextToast.postValue(expect)
        assertEquals(expect, viewModel.actionTextToast.value)
    }

    @Test
    fun testGetProduct() {
        viewModel.getProduct(1)
        assertEquals(viewModel.product.value, viewModel.product.value)

    }

    @Test
    fun editPost() {
       viewModel.editPost(1)
    }

    @Test
    fun isFormValid() {
        viewModel.product.postValue(product)
        var result = viewModel.isFormValid()
        assertEquals(true, result)
    }


}
