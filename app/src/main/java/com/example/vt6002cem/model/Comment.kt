package com.example.vt6002cem.model


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

import com.example.vt6002cem.BR
import java.util.*


class Comment : BaseObservable() {

    @get:Bindable
    var id: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

    @get:Bindable
    var productId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.productId)
        }

    @get:Bindable
    var comment: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }

    @get:Bindable
    var commentDate: Date? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentDate)
        }

    @get:Bindable
    var commentBy: User? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentBy)
        }

}