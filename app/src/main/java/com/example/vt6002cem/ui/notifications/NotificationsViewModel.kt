package com.example.vt6002cem.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vt6002cem.model.Comment
import com.example.vt6002cem.model.Product
import kotlinx.coroutines.*

class NotificationsViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val product = MutableLiveData<Product>()
    val comment = MutableLiveData<Comment>(Comment())
    var commentList= MutableLiveData<ArrayList<Comment>>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}