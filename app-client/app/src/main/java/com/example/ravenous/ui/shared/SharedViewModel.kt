package com.example.ravenous.ui.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ravenous.data.Business
import com.example.ravenous.data.BusinessRepository
import com.example.ravenous.data.SearchForm
import com.example.ravenous.sortByOptions

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    private val dataRepo = BusinessRepository(app)

    val businessData = dataRepo.businessData
    val selectedBusiness = MutableLiveData<Business>()

    val searchForm = MutableLiveData<SearchForm>()

    fun getData() {
        dataRepo.getData(searchForm.value)
    }
}