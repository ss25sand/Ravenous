package com.example.ravenous.data

import retrofit2.Response
import retrofit2.http.*

interface RecommendationService {
    @GET("{user_id}/ratings/top/{count}")
    suspend fun getTopRecommendations(
        @Path("user_id") userId: String,
        @Path("count") count: Int,
        @Query("city") city: String,
        @Query("state") state: String
    ): Response<List<String>>

    @POST("{user_id}/rating")
    suspend fun postBusinessRating(
        @Path("user_id") userId: String,
        @Body rating: RequestRating
    )
}