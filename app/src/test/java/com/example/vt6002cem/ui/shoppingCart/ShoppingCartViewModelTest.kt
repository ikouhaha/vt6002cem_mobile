package com.example.vt6002cem.ui.shoppingCart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.post.CreatePostViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

class ShoppingCartViewModelTest {

    var product: Product = Product()
    var productList: ArrayList<Product> = arrayListOf()
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    private lateinit var productService: ProductsApiService
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: ShoppingCartViewModel

    @Before
    fun setUp() {

        productService = Mockito.mock(ProductsApiService.getInstance("")::class.java)
        repository = ProductRepository(productService)
        viewModel = ShoppingCartViewModel(repository)
        product.id = 3
        product.companyCode = "111"
        product.imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mPMSkkxBgAD2QFnQD1bxwAAAABJRU5ErkJggg=="
        product.canDelete = true
        product.canEdit = true
        product.about = "aaaaaaaa"
        product.name = "Qipao 3"
        product.price = 27000
        productList.add(product)
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
        val expect = true
        viewModel.loading.postValue(expect)
        assertEquals(expect, viewModel.loading.value)
    }

    @Test
    fun getProductList() {
        var expect = productList
        setProductList()
        assertEquals(expect, viewModel.productList.value)

    }

    @Test
    fun setProductList() {
        var expect = productList
        viewModel.productList.postValue(expect)

    }

    @Test
    fun getDeleteIds() {
        var expect = arrayListOf(1,2)
        setDeleteIds()
        assertEquals(expect, viewModel.deleteIds.value)
    }

    @Test
    fun setDeleteIds() {
        var expect = arrayListOf(1,2)
        viewModel.deleteIds.postValue(expect)
    }

    @Test
    fun getProducts() {
        viewModel.getProducts(arrayOf(1,2))
    }
}