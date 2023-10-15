package com.ubec.ubecapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.databinding.FragmentHomeBinding
import com.ubec.ubecapp.databinding.ReportsFragmentBinding
import com.ubec.ubecapp.ui.reports.ReportsViewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = with(FragmentHomeBinding.inflate(inflater, container, false)) {
        lifecycleOwner = viewLifecycleOwner
        viewmodel = viewModel
        binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = DBHelper(requireContext(), null)
        val projects = db.getProjects()
        val supplies = db.getSupplies()

        binding.projectCounts.text = projects.count().toString()
        binding.supplyCounts.text = supplies.count().toString()

        navController = view.findNavController()

        binding.btnSubmit.setOnClickListener(){
            navController.navigate(R.id.action_nav_home_to_nav_new_report,null,null)
        }
        binding.btnAddSupply.setOnClickListener(){
            navController.navigate(R.id.action_nav_home_to_nav_new_supply,null,null)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }
}