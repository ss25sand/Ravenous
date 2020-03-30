package com.example.ravenous.data

data class Business(
    val id: String,
    val name: String,
    val image_url: String,
    val review_count: String,
    val rating: Float,
    val location: Location
)