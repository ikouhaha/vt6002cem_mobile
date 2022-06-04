package com.example.vt6002cem.model


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

import com.example.vt6002cem.BR



class ProductFilters : BaseObservable() {
    @get:Bindable
    var searchText: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.searchText)
        }

    @get:Bindable
    var page: Int = 1
        set(value) {
            field = value
            notifyPropertyChanged(BR.page)
        }

    @get:Bindable
    var limit:Int = 4
        set(value) {
            field = value
            notifyPropertyChanged(BR.limit)
        }

}