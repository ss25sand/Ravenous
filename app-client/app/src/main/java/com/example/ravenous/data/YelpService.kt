package com.example.ravenous.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpService {
    @GET("businesses/search")
    suspend fun getBusinesses(
        @Query("term") term: String,
        @Query("location") location: String,
        @Query("sort_by") sortBy: String
    ): Response<Businesses>

    @GET("businesses/{id}")
    suspend fun getBusiness(
        @Path("id") id: String
    ): Response<Business>
}