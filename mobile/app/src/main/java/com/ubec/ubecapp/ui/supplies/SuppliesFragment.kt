package com.ubec.ubecapp.ui.supplies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.databinding.FragmentSuppliesListBinding

/**
 * A fragment representing a list of Items.
 */
class SuppliesFragment : Fragment() {

    private var _binding: FragmentSuppliesListBinding? = null
    private lateinit var navController: NavController
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSuppliesListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        val db = DBHelper(requireActivity(), null)
        val supplies = db.getSupplies()
        binding.list.adapter = MySupplyRecyclerViewAdapter(supplies,navController){ item ->
            val bundle = bundleOf("id" to item.id )
            navController.navigate(R.id.action_nav_report_supplies_to_nav_details_supply ,bundle,null)
        }
        binding.btnAddSupply.setOnClickListener(){
            navController.navigate(R.id.action_nav_report_supplies_to_nav_new_supply ,null,null)
        }
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            SuppliesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}