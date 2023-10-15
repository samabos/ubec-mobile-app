package com.ubec.ubecapp.ui.newsupplyitems

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.forntoh.livedata_validation.validation.LiveDataValidator
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.SupplyItemModel
import com.ubec.ubecapp.databinding.UpdateFragmentBinding
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    companion object {
        fun newInstance() = UpdateFragment()
    }

    private val sharedPrefFile = "IdentityData"
    private lateinit var navController: NavController
    private val viewModel: UpdateViewModel by viewModels()
    private lateinit var binding: UpdateFragmentBinding
    private fun randomUUID() = UUID.randomUUID().toString()

    private var id :String? = "0"
    private var supplyId :String? = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        id = arguments?.getString("id")
        supplyId = arguments?.getString("supplyId")

        return with(UpdateFragmentBinding.inflate(inflater, container, false)) {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
            binding = this
            root
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()
        viewModel.id.value = id
        viewModel.supplyId.value = supplyId
        val db = DBHelper(requireContext(), null)
        if(id != null){
        val supply = db.getSupplyItem(id!!.toInt())
        viewModel.description.value = supply?.description
        viewModel.quantityDelivered.value = supply?.quantityDelivered
        viewModel.quantityOrdered.value = supply?.quantityOrdered
        viewModel.serialNo.value = supply?.serialNo
        viewModel.remarks.value = supply?.remarks
        }

        LiveDataValidator(requireContext()).observe {
            lifecycleOwner(viewLifecycleOwner)
            viewModel(viewModel)
            attachTo(binding.root)
        }

        binding.btnSubmitItem.setOnClickListener {
            onUpdate()

            val bundle = bundleOf("id" to viewModel.supplyId.value)
            navController.navigate(R.id.action_nav_supplt_item_to_nav_new_supply_items ,bundle,null)
        }
    }


    private fun getUsername():String?{
        //implementing remember me with shared preferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString("userName",null)
    }

    private fun onUpdate():Long{
        val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        //viewModel.verificationDate.value = sdf.format(Date())
        //var transactionId:String? = randomUUID()
        //var coordinate:String? = ""//getLocation()
        //var status:String? = "Not Submitted"
        var modifiedBy:String? = getUsername()
        //viewModel.verificationOfficer.value = modifiedBy
        // below we have created
        // a new DBHelper class,
        // and passed context to it
        val db = DBHelper(requireContext(), null)

        // calling method to add
        //val arr = viewModel.project.value?.split("-")
        return db.updateSupplyItem(SupplyItemModel(
            viewModel.id.value,
            viewModel.supplyId.value,
            viewModel.serialNo.value,
            viewModel.description.value,
            viewModel.quantityOrdered.value,
            viewModel.quantityDelivered.value,
            viewModel.remarks.value,
            modifiedBy
        ))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(UpdateViewModel::class.java)
        // TODO: Use the ViewModel
    }

}