package com.example.ravenous.data

data class Location (
    val address1: String,
    val city: String,
    val state: String,
    val zip_code: String
) {
    val fullAddress get() = "$address1\n$city, $state\n$zip_code"
}