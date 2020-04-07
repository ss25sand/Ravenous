package com.example.ravenous.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ravenous.LOG_TAG
import com.example.ravenous.R
import com.example.ravenous.data.Business
import com.example.ravenous.data.SearchForm
import com.example.ravenous.databinding.FragmentSearchBinding
import com.example.ravenous.sortByOptions
import com.example.ravenous.ui.result.ResultRecyclerAdapter
import com.example.ravenous.ui.shared.SharedViewModel
import com.example.ravenous.utils.PrefsHelper
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment(), ResultRecyclerAdapter.BusinessItemListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        // Inflate the layout for this fragment
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host)

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sortByOptions.keys.toTypedArray()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortBySpinner.adapter = adapter
        }

        binding.searchButton.setOnClickListener {
            viewModel.searchForm.value = SearchForm(
                binding.foodInput.text.toString().trim(),
                binding.locationInput.text.toString().trim(),
                sortByOptions[sortBySpinner.selectedItem.toString()] ?: error("")
            )
            viewModel.getSearchResults()
            Navigation
                .findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_nav_result)
        }

        val recyclerView = binding.recyclerView
        val layoutStyle = PrefsHelper.getItemType(requireContext())
        recyclerView.layoutManager = if (layoutStyle == "grid") {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }

        viewModel.lastLocation.observe(viewLifecycleOwner, Observer { viewModel.getRecommendations() })

        viewModel.recommendedBusinesses.observe(viewLifecycleOwner, Observer {
            val adapter = ResultRecyclerAdapter(requireContext(), it.businesses, this)
            recyclerView.adapter = adapter
        })

        return binding.root
    }

    override fun onBusinessItemClick(business: Business) {
        Log.i(LOG_TAG, "Selected business: ${business.name}")
        viewModel.selectedBusiness.value = business
        navController.navigate(R.id.action_nav_detail)
    }
}
