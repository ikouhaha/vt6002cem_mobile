package com.example.vt6002cem.common

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


object Validations {

    fun email(input: String?):String? {
        var regexStr:String = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$"
        if(input.isNullOrEmpty()){
            return "Please fill the email field";
        }
        if(!input.matches(regexStr.toRegex())){
            return "Please input validate email"
        }
        return null
    }

    fun password(input: String?):String? {
        var regexStr:String = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$"
        if(input.isNullOrEmpty()){
            return "Please fill the password field";
        }
        if(!input.matches(regexStr.toRegex())){
            return "Minimum eight characters, at least one letter and one number"
        }
        return null
    }

    fun confirmPassword(input: String?, pwd:String?):String? {

        if(input.isNullOrEmpty()){
            return "Please fill the password field"
        }
        if (pwd!=input) {
            return "Please match with password"
        }
        return null
    }

    fun name(input:String?):String? {
        var regexStr:String = "^[a-z ,.'-]+\$"

        if(input.isNullOrEmpty()){
            return "Please fill the name field"
        }

        if(!input.matches(regexStr.toRegex())){
            return "Please input validate name"
        }

        return null;
    }

    fun text(input:String?):String? {
        if(input.isNullOrEmpty()){
            return "Please fill the field"
        }


        return null;
    }


    fun number(input: Number?):String? {

        if(input==null){
            return "Please fill the number field"
        }
        // if(!input.toString().matches(regexStr.toRegex())){
        //     return "Please input validate number"
        // }
        return null;
    }








}