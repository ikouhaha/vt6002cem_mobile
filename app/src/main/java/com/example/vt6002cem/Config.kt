package com.example.vt6002cem


object Config {
    var apiUrl:String = "http://10.0.2.2:10888/api/v1/"
    var imageUrl:String = apiUrl+"products/image/"
    var firebaseRDBUrl:String = "https://vt6002cem-286e8-default-rtdb.asia-southeast1.firebasedatabase.app/"
    var callTimeout:Long = 120
    var connectionTimeout:Long = 2
    var readTimeout:Long = 30
    var writeTimeout:Long = 30


}