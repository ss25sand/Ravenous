package com.example.ravenous.data

import android.app.Application
import android.location.Address
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.ravenous.LOG_TAG
import com.example.ravenous.RECOMMENDATION_WEB_SERVICE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RecommendationRepository(override val app: Application) : BusinessRepository(app) {
    val recommendedBusinesses = MutableLiveData<Businesses>()
    private lateinit var lastLocation: Address
    private var yelpRepository = YelpRepository(app)

    private fun getRecommendationService(): RecommendationService {
        val retrofit = Retrofit.Builder()
            .baseUrl(RECOMMENDATION_WEB_SERVICE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(RecommendationService::class.java)
    }

    fun getRecommendations(location: Address?) {
        location?.let {
            lastLocation = it
            val cityName: String = it.getAddressLine(0)
            val stateName: String = it.getAddressLine(1)
            CoroutineScope(Dispatchers.IO).launch { getRecommendationResultsFromWeb(cityName, stateName) }
        }
    }

    @WorkerThread
    private suspend fun getRecommendationResultsFromWeb(city: String, state: String) {
        Log.i(LOG_TAG, "Getting recommendation results from web: ${networkAvailable()}")
        if (networkAvailable()) {
            val serviceRes = getRecommendationService().getTopRecommendations("u0LXt3Uea_GidxRW1xcsfg", 10, city, state)
            serviceRes.body()?.let {
                Log.i(LOG_TAG, it.toString())
                recommendedBusinesses.postValue(yelpRepository.getBusinessDataFromWeb(it))
            }
        }
    }

    fun postBusinessRating(rating: RequestRating?) {
        rating?.let {
            Log.i(LOG_TAG, it.toString())
            CoroutineScope(Dispatchers.IO).launch { postBusinessRatingToWeb(it) }
        }
    }

    @WorkerThread
    private suspend fun postBusinessRatingToWeb(rating: RequestRating) {
        Log.i(LOG_TAG, "Calling web service: ${networkAvailable()}")
        if (networkAvailable()) {
            getRecommendationService().postBusinessRating("1", rating)
            getRecommendations(lastLocation)
        }
    }
}