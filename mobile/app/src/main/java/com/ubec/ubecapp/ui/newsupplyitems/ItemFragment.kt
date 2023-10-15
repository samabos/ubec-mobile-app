package com.ubec.ubecapp.ui.newsupplyitems

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
import com.ubec.ubecapp.databinding.FragmentItemListBinding

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null
    private lateinit var navController: NavController
    private var supplyId :String? = "0"
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
        supplyId = arguments?.getString("id")
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        val db = DBHelper(requireActivity(), null)
        val supply = db.getSupply(supplyId!!.toInt())
        binding.textLayoutItemContractor.editText?.setText(supply?.contractor)
        binding.textLayoutItemLocation.editText?.setText(supply?.location)
        binding.textLayoutItemRep.editText?.setText(supply?.representative)

        val items = db.getSupplyItems(supplyId!!)
        var adapter = MyItemRecyclerViewAdapter(items,requireActivity(),db){ item ->

            val bundle = bundleOf("id" to item.id, "supplyId" to supplyId )
            navController.navigate(R.id.action_nav_new_supply_items_to_nav_supplt_item ,bundle,null)
        };
        binding.itemList.adapter = adapter

        binding.btnAddSupply.setOnClickListener{
            val bundle = bundleOf("id" to null, "supplyId" to supplyId )
            navController.navigate(R.id.action_nav_new_supply_items_to_nav_supplt_item ,bundle,null)
        }
        binding.btnBackSupply.setOnClickListener{
            navController.navigate(R.id.action_nav_new_supply_items_to_nav_report_supplies ,null,null)
        }



    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}