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
    var price: Number? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.price)
        }

    @get:Bindable
    var companyCode: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyCode)
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
            //otifyPropertyChanged(BR.imageBase64)
        }

    @get:Bindable
    var canEdit: Boolean? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.canEdit)
        }

    @get:Bindable
    var canDelete: Boolean? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.canDelete)
        }

    @Bindable
    fun getPriceString(): String? {
        if(price==null){
            return null
        }
        return price.toString()
    }

    fun setPriceString(value: String?) {
        if(value.isNullOrEmpty()){
            this.price = null
        }else {
            this.price = value.toBigDecimal()
        }
        notifyPropertyChanged(BR.priceString)

    }
}