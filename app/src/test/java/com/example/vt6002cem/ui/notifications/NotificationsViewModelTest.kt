package com.example.vt6002cem.ui.notifications

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

class NotificationsViewModelTest {

    var comment: Comment = Comment()
    var commentList: ArrayList<Comment> = arrayListOf()
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setUp() {
        viewModel = NotificationsViewModel()
        comment.avatar = "https://lh3.googleusercontent.com/a/AATXAJyzetG1ehaZy7LI0Wanz3LIuL87iETNNtLrIxPo=s96-c"
        comment.key = "-N4LgJORls54RcuOToes"
        comment.comment = "test"
        comment.commentById = "Hzo7gmhqXOPlsVR8yq5NBovg3FK2"
        comment.commentDate = "2022/06/12 14:54:28"
        comment.productId = 27
        commentList.add(comment)
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
}