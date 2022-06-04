package com.example.vt6002cem.model


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

import com.example.vt6002cem.BR



class Product : BaseObservable() {

    @get:Bindable
    var id: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

    @get:Bindable
    var name: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var about: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.about)
        }

    @get:Bindable
    var imageBase64: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageBase64)
        }

}