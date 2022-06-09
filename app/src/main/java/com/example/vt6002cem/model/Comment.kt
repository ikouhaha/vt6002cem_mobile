package com.example.vt6002cem.model


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

import com.example.vt6002cem.BR
import java.util.*


class Comment : BaseObservable() {

    @get:Bindable
    var productId: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.productId)
        }

    @get:Bindable
    var avatar: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.avatar)
        }


    @get:Bindable
    var comment: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }

    @get:Bindable
    var commentDate: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentDate)
        }


    @get:Bindable
    var commentBy: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentBy)
        }
    @get:Bindable
    var commentById: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentById)
        }
}