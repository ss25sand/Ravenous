package com.example.ravenous.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.ravenous.R
import com.example.ravenous.data.SearchForm
import com.example.ravenous.sortByOptions
import com.example.ravenous.ui.shared.SharedViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val foodCategoryInput: TextInputEditText = view.findViewById(R.id.foodInput)
        val locationInput: TextInputEditText = view.findViewById(R.id.locationInput)
        val spinner: Spinner = view.findViewById(R.id.sortBySpinner)
        val searchButton: Button = view.findViewById(R.id.searchButton)

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sortByOptions.keys.toTypedArray()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        searchButton.setOnClickListener {
            viewModel.searchForm.value = SearchForm(
                foodCategoryInput.text.toString().trim(),
                locationInput.text.toString().trim(),
                sortByOptions[sortBySpinner.selectedItem.toString()] ?: error("")
            )
            viewModel.getData()
            Navigation
                .findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_nav_result)
        }

        return view
    }
}
