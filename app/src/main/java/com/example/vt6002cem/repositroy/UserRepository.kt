package com.example.vt6002cem.repositroy;

import com.example.vt6002cem.adpater.ProductsApiService;
import com.example.vt6002cem.adpater.UserApiService
import com.example.vt6002cem.model.ProductFilters
import com.example.vt6002cem.model.User

class UserRepository constructor(private val retrofitService: UserApiService) {

        suspend fun createUser(user: User) = retrofitService.createUser(user)
                
}