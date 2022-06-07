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
    var price: Float? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.price)
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

    @Bindable
    fun getPriceString(): String {
        return price.toString()
    }

    fun setPriceString(value: String) {
        val v = value.toFloat()
        this.price = v
    }
}