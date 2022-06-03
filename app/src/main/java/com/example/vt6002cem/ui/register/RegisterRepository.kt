package com.example.vt6002cem.ui.register;

import com.example.vt6002cem.adpater.ProductsApiService;
import com.example.vt6002cem.adpater.UserApiService
import com.example.vt6002cem.model.ProductFilters
import com.example.vt6002cem.model.User

class RegisterRepository constructor(private val retrofitService: UserApiService) {

        suspend fun createUser(user: User) = retrofitService.createUser(user)
                
}