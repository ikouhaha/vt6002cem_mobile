package com.example.vt6002cem.ui.home

import android.text.Editable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.model.ProductFilters
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.post.CreatePostViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*

class HomeViewModelTest {

    var product: Product = Product()
    var filters: ProductFilters = ProductFilters()
    var productList = arrayListOf<Product>()
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    private lateinit var productService: ProductsApiService
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: HomeViewModel

    @Mock
    var viewModelMock = mock(HomeViewModel::class.java)

    @Before
    fun setUp() {
        productService = mock(ProductsApiService.getInstance("")::class.java)
        repository = ProductRepository(productService)
        viewModel = HomeViewModel(repository)
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
    fun getProductList() {
        var expect = this.productList
        setProductList()
        assertEquals(expect, viewModel.productList.value)
    }

    @Test
    fun setProductList() {
        var expect = this.productList
        viewModel.productList.postValue(expect)
    }

    @Test
    fun getFilters() {
        var expect = this.filters
        setFilters()
        assertEquals(expect, viewModel.filters.value)
    }

    @Test
    fun setFilters() {
        var expect = this.filters
        viewModel.filters.postValue(expect)
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
    fun getNextPage() {
        var expect = 2
        viewModel.nextPage.postValue(expect)
        assertEquals(expect, viewModel.nextPage.value)
    }

    @Test
    fun isDelete() {
        var expect = true
        viewModel.isDelete.postValue(expect)
        assertEquals(expect, viewModel.isDelete.value)
    }

    @Test
    fun getProducts() {
        viewModel.getProducts()
    }

    @Test
    fun deletePost() {
        viewModel.deletePost(1,"111")
    }

    @Test
    fun loadMore() {
        viewModel.loadMore()
    }

    @Test
    fun clearList() {
        viewModel.clearList()
    }

    @Test
    fun refreshList() {
        viewModel.refreshList()
    }

    @Test
    fun search() {
        val mock = mock(Editable::class.java)
        viewModel.search(mock)
    }
}