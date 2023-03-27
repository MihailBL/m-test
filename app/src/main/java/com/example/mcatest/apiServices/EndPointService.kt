package com.example.mcatest.apiServices

import com.example.mcatest.dto.MainDTO
import retrofit2.Call
import retrofit2.http.GET

interface EndPointService {

    @GET("photos")
    fun getData(): Call<List<MainDTO>>
}