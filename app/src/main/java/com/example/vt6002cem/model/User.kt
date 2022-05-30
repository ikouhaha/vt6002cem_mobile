package com.example.vt6002cem.model


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

import com.example.vt6002cem.BR

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*



class User : BaseObservable() {

    @get:Bindable
    var email: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @get:Bindable
    var password: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    @get:Bindable
    var confirmPassword: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.confirmPassword)
        }

}