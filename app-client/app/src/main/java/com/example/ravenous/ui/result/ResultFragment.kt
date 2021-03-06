package com.example.ravenous.ui.result


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ravenous.LOG_TAG
import com.example.ravenous.R
import com.example.ravenous.data.Business
import com.example.ravenous.databinding.FragmentResultBinding
import com.example.ravenous.ui.shared.SharedViewModel
import com.example.ravenous.utils.PrefsHelper

/**
 * A simple [Fragment] subclass.
 */
class ResultFragment : Fragment(), ResultRecyclerAdapter.BusinessItemListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var navController: NavController
    private lateinit var adapter: ResultRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        val binding = FragmentResultBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView
        val layoutStyle = PrefsHelper.getItemType(requireContext())
        recyclerView.layoutManager = if (layoutStyle == "grid") {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }

        swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener { viewModel.getSearchResults() }

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel.businessData.observe(viewLifecycleOwner, Observer {
            adapter = ResultRecyclerAdapter(requireContext(), it.businesses, this)
            recyclerView.adapter = adapter
            swipeLayout.isRefreshing = false
        })

        return binding.root
    }

    override fun onBusinessItemClick(business: Business) {
        Log.i(LOG_TAG, "Selected business: ${business.name}")
        viewModel.selectedBusiness.value = business
        navController.navigate(R.id.action_nav_result_to_detail)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_result, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
            R.id.action_view_grid -> {
                PrefsHelper.setItemType(requireContext(), "grid")
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                recyclerView.adapter = adapter
            }
            R.id.action_view_list -> {
                PrefsHelper.setItemType(requireContext(), "list")
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
