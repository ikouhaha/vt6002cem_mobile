package com.example.vt6002cem.repositroy;

import com.example.vt6002cem.http.UserApiService
import com.example.vt6002cem.model.User

class UserRepository constructor(private val retrofitService: UserApiService) {

        suspend fun createUser(user: User) = retrofitService.createUser(user)
        suspend fun getProfile() = retrofitService.getProfile()
        suspend fun changePwd(id:Int,user:User) = retrofitService.changePwd(id,user)
        suspend fun changeProfile(id:Int,user:User) = retrofitService.changeProfile(id,user)

}