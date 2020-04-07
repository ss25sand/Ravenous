package com.example.ravenous.data

import android.app.Application
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.ravenous.API_KEY
import com.example.ravenous.LOG_TAG
import com.example.ravenous.YELP_WEB_SERVICE_URL
import com.example.ravenous.utils.FileHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class YelpRepository(override val app: Application) : BusinessRepository(app) {
    val businessData = MutableLiveData<Businesses>()
    private var fileName: String = ""

    private fun getYelpService(): YelpService {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder()
                        .addHeader("Authorization", "Bearer $API_KEY")
                        .addHeader("origin", "")
                        .build()
                )
            }
        val retrofit = Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(YELP_WEB_SERVICE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(YelpService::class.java)
    }

    fun getSearchResults(searchForm: SearchForm?) {
        searchForm?.let {
            fileName = "${it.foodCategory}+${it.location}+${it.sortBy}.json"
            val cacheData = readDataFromCache()
            if (cacheData == null) {
                CoroutineScope(Dispatchers.IO).launch { getSearchResultsFromWeb(it) }
            } else {
                businessData.value = cacheData
            }
        }
    }

    @WorkerThread
    private suspend fun getSearchResultsFromWeb(searchForm: SearchForm) {
        Log.i(LOG_TAG, "Calling web service: ${networkAvailable()}")
        if (networkAvailable()) {
            val serviceRes = getYelpService().getBusinesses(
                searchForm.foodCategory.toLowerCase(Locale.ROOT),
                searchForm.location.toLowerCase(Locale.ROOT),
                searchForm.sortBy
            )
            Log.i(LOG_TAG, serviceRes.toString())
            serviceRes.body()?.let {
                Log.i(LOG_TAG, it.toString())
                businessData.postValue(it)
                saveDataToCache(it)
            }
        }
    }

    @WorkerThread
    suspend fun getBusinessDataFromWeb(businessIds: List<String>): Businesses {
        Log.i(LOG_TAG, "Calling web service: ${networkAvailable()}")
        if (networkAvailable()) {
            val businesses = businessIds.map {
                getYelpService().getBusiness(it).body()
            }
            Log.i(LOG_TAG, businesses.toString())
            return Businesses(businesses.filterNotNull())
        }
        return Businesses(emptyList())
    }

    private fun getJsonAdapter(): JsonAdapter<Businesses> {
        return Moshi.Builder().build().adapter(Businesses::class.java)
    }

    private fun readDataFromCache(): Businesses? {
        Log.i(LOG_TAG, "Reading data from cache!")
        val json = FileHelper.readTextFile(app, fileName)
        return if (json == null) null else getJsonAdapter().fromJson(json)
    }

    private fun saveDataToCache(businessData: Businesses) {
        val json = getJsonAdapter().toJson(businessData)
        FileHelper.saveTextToFile(app, fileName, json)
    }
}