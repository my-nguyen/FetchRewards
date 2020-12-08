package com.nguyen.fetchrewards

import retrofit2.Call
import retrofit2.http.GET

interface AWSService {
    @GET("hiring.json")
    fun fetchHiring() : Call<List<Hire>>
}