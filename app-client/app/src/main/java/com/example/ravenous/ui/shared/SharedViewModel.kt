package com.example.ravenous.ui.shared

import android.app.Application
import android.location.Address
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ravenous.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    private val yelpRepo = YelpRepository(app)
    private val recommendationRepo = RecommendationRepository(app)

    val businessData = yelpRepo.businessData
    val recommendedBusinesses = recommendationRepo.recommendedBusinesses
    val selectedBusiness = MutableLiveData<Business>()

    val searchForm = MutableLiveData<SearchForm>()
    val lastLocation = MutableLiveData<Address>()
    val rating = MutableLiveData<RequestRating>()

    fun getSearchResults() {
        yelpRepo.getSearchResults(searchForm.value)
    }

    fun getRecommendations() {
        recommendationRepo.getRecommendations(lastLocation.value)
    }

    fun postRating() {
        recommendationRepo.postBusinessRating(rating.value)
    }
}