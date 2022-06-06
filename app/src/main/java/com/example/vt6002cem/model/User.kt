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
    var id: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

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
    @get:Bindable
    var displayName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.displayName)
        }
    @get:Bindable
    var lastName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }
    @get:Bindable
    var avatarUrl: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.avatarUrl)
        }
    @get:Bindable
    var role: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.role)
        }
    @get:Bindable
    var fid: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.fid)
        }
    @get:Bindable
    var companyCode: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.companyCode)
        }
}