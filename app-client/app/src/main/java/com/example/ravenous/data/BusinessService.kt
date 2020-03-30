package com.example.ravenous.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BusinessService {
    @GET("businesses/search")
    suspend fun getBusinesses(
        @Query("term") term: String,
        @Query("location") location: String,
        @Query("sort_by") sortBy: String
    ): Response<Businesses>
}