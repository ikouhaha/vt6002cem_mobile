package com.example.vt6002cem.ui.productDetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vt6002cem.http.ProductsApiService
import com.example.vt6002cem.model.Comment
import com.example.vt6002cem.model.Product
import com.example.vt6002cem.repositroy.ProductRepository
import com.example.vt6002cem.ui.post.CreatePostViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

class ProductDetailViewModelTest {

    var product: Product = Product()
    var comment: Comment = Comment()
    var commentList: ArrayList<Comment> = arrayListOf()
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var productService: ProductsApiService
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: ProductDetailViewModel

    @Before
    fun setUp() {

        comment.avatar = "https://lh3.googleusercontent.com/a/AATXAJyzetG1ehaZy7LI0Wanz3LIuL87iETNNtLrIxPo=s96-c"
        comment.key = "-N4LgJORls54RcuOToes"
        comment.comment = "test"
        comment.commentById = "Hzo7gmhqXOPlsVR8yq5NBovg3FK2"
        comment.commentDate = "2022/06/12 14:54:28"
        comment.productId = 27
        commentList.add(comment)
        productService = Mockito.mock(ProductsApiService.getInstance("")::class.java)
        repository = ProductRepository(productService)
        viewModel = ProductDetailViewModel(repository)
        product.id = 3
        product.companyCode = "111"
        product.imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mPMSkkxBgAD2QFnQD1bxwAAAABJRU5ErkJggg=="
        product.canDelete = true
        product.canEdit = true
        product.about = "aaaaaaaa"
        product.name = "Qipao 3"
        product.price = 27000
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
    fun getProduct() {
        val expect = product
        viewModel.product.postValue(expect)
        assertEquals(expect, viewModel.product.value)
    }

    @Test
    fun getComment() {
        val expect = comment
        viewModel.comment.postValue(expect)
        assertEquals(expect, viewModel.comment.value)
    }

    @Test
    fun getCommentList() {
        val expect = commentList
        setCommentList()
        assertEquals(expect, viewModel.commentList.value)
    }

    @Test
    fun setCommentList() {
        val expect = commentList
        viewModel.commentList.postValue(expect)
    }


    @Test
    fun testGetProduct() {
        viewModel.getProduct(1)
    }
}