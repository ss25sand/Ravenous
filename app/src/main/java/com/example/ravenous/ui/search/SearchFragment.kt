package com.example.ravenous.ui.search


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ravenous.LOG_TAG
import com.example.ravenous.R
import com.example.ravenous.data.BusinessRepository
import com.example.ravenous.ui.SharedBusinessViewModel

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private lateinit var viewModel: SharedBusinessViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host)
        viewModel = ViewModelProvider(requireActivity()).get(SharedBusinessViewModel::class.java)
        viewModel.businessData.observe(viewLifecycleOwner, Observer {
            Log.i(LOG_TAG, it.toString())
        })
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


}
