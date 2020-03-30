package com.example.ravenous.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.ravenous.API_KEY
import com.example.ravenous.LOG_TAG
import com.example.ravenous.WEB_SERVICE_URL
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

class BusinessRepository(val app: Application) {
    val businessData = MutableLiveData<Businesses>()
    private var fileName: String = ""

    fun getData(searchForm: SearchForm?) {
        if (searchForm == null) return
        fileName = "${searchForm.foodCategory}+${searchForm.location}+${searchForm.sortBy}.json"
        val cacheData = readDataFromCache()
        if (cacheData == null) {
            CoroutineScope(Dispatchers.IO).launch { getDataFromWeb(searchForm) }
        } else {
            businessData.value = cacheData
        }
    }

    private fun networkAvailable(): Boolean {
        val connectivityManager =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }

    private fun getService(): BusinessService {
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
            .baseUrl(WEB_SERVICE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(BusinessService::class.java)
    }

    @WorkerThread
    private suspend fun getDataFromWeb(searchForm: SearchForm) {
        Log.i(LOG_TAG, "Calling web service: ${networkAvailable()}")
        if (networkAvailable()) {
            val serviceRes = getService().getBusinesses(
                searchForm.foodCategory.toLowerCase(Locale.ROOT),
                searchForm.location.toLowerCase(Locale.ROOT),
                searchForm.sortBy
            )
            Log.i(LOG_TAG, serviceRes.toString())
            val serviceData = serviceRes.body()
            if (serviceData != null) {
                Log.i(LOG_TAG, serviceData.toString())
                businessData.postValue(serviceData)
                saveDataToCache(serviceData)
            }
        }
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