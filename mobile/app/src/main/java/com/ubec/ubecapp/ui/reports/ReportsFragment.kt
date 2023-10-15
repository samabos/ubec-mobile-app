package com.ubec.ubecapp.ui.reports

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.ReportModel
import com.ubec.ubecapp.databinding.ReportsFragmentBinding
import android.widget.AdapterView.OnItemClickListener
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ubec.ubecapp.R
import com.ubec.ubecapp.ui.details.DetailsFragment


class ReportsFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = ReportsFragment()
    }
    private val viewModel: ReportsViewModel by viewModels()

    private lateinit var binding: ReportsFragmentBinding
    private lateinit var navController: NavController
    private lateinit var myListAdapter: MyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = with(ReportsFragmentBinding.inflate(inflater, container, false)) {
        lifecycleOwner = viewLifecycleOwner
        viewmodel = viewModel
        binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val adapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.items)
        viewRecord();
        navController = view.findNavController()
        binding.listView.setOnItemClickListener{ parent, view, position, id ->
            val element = myListAdapter.getId(position) // The item that was clicked

            val bundle = bundleOf("id" to element )
            navController.navigate(R.id.action_nav_reports_to_nav_details,bundle,null)
        }
    }

     override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.listView-> navController.navigate(R.id.action_nav_reports_to_nav_details)
        }}

    private fun viewRecord(){
        //creating the instance of DatabaseHandler class
        val db: DBHelper= DBHelper(requireContext(),null)
        //calling the getReport method of DatabaseHandler class to read the records
        val report: List<ReportModel> = db.getReports()
        val empArrayId = Array<String>(report.size){"0"}
        val empArrayProject = Array<String?>(report.size){"null"}
        val empArrayContractor = Array<String?>(report.size){"null"}
        val empArrayStatus= Array<String?>(report.size){"null"}
        var index = 0
        for(e in report){
            empArrayId[index] = e.id.toString()
            empArrayProject[index] = e.serialNo
            empArrayContractor[index] = e.contractor
            empArrayStatus[index] = e.status
            index++
        }
        //creating custom ArrayAdapter
        myListAdapter = MyListAdapter(requireActivity(),empArrayId,empArrayProject,empArrayContractor,empArrayStatus)
        binding.listView.adapter = myListAdapter


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // binding = null
    }

}