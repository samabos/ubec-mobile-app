package com.ubec.ubecapp.ui.newsupply

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.forntoh.livedata_validation.validation.LiveDataValidator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.ReportModel
import com.ubec.ubecapp.data.model.SupplyModel
import com.ubec.ubecapp.databinding.SupplyFragmentBinding
import com.ubec.ubecapp.ui.newreport.Item
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class SupplyFragment : Fragment() {

    companion object {
        fun newInstance() = SupplyFragment()
    }
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val sharedPrefFile = "IdentityData"
    private lateinit var navController: NavController
    private lateinit var client: FusedLocationProviderClient

    private val viewModel: SupplyViewModel by viewModels()
    private lateinit var binding: SupplyFragmentBinding

    private fun randomUUID() = UUID.randomUUID().toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        client = LocationServices.getFusedLocationProviderClient(getActivity())
        getLastLocation()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        return with(SupplyFragmentBinding.inflate(inflater, container, false)) {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
            binding = this
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()
        //val adapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.items)

        //val qualityAdapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.qualityItems)
        //val defectAdapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.defectItems)
        val attachmentAdapter = ArrayAdapter(requireContext(),R.layout.list_item,viewModel.attachmentItems)
        //product Quality List
        //val productQualityLayout =  binding.textLayoutProductQuality
        //(productQualityLayout?.editText as? AutoCompleteTextView)?.setAdapter(qualityAdapter)

        //Defect List
        //val defectLayout =  binding.textLayouthasDefects
        //(defectLayout?.editText as? AutoCompleteTextView)?.setAdapter(defectAdapter)

        //Attachment List
        var attachmentLayout = binding.textLayoutSupplyAttachment
        (attachmentLayout?.editText as? AutoCompleteTextView)?.setAdapter(attachmentAdapter)

        //getProjects()
        getStates()
        getContractors()
        //getLGAs()

        // Set up the listener for take photo button
        binding.supplyCameraStartButton.setOnClickListener { startCamera() }
        binding.supplyCameraCaptureButton.setOnClickListener { takePhoto() }

        var st = (binding.textLayoutSupplyState.editText as? AutoCompleteTextView)
        st!!.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            getLGAs(selectedItem)
        }

        LiveDataValidator(requireContext()).observe {
            lifecycleOwner(viewLifecycleOwner)
            viewModel(viewModel)
            attachTo(binding.root)
        }

        binding.btnSubmitSupply.setOnClickListener {
            if(viewModel.ImagePath1 != null && viewModel.ImagePath2 !=null && viewModel.ImagePath3!=null&&viewModel.ImagePath4!=null){
                val resp = onAdd()
                if(resp != -1L){
                    // Toast to message on the screen
                    Toast.makeText(requireContext(), viewModel.contractor.value + " verification record has been added", Toast.LENGTH_LONG).show()
                    binding.btnSubmitSupply.isActivated = false
                    binding.btnSubmitSupply.visibility = View.INVISIBLE
                    val bundle = bundleOf("id" to resp.toString() )
                    navController.navigate(R.id.action_nav_new_supply_to_nav_new_supply_items,bundle,null)

                }
            }else{
                Toast.makeText(requireContext(), "Please upload all attachments/pictures ", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun onAdd():Long{
        val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        viewModel.verificationDate.value = sdf.format(Date())
        var transactionId:String? = randomUUID()
        //var coordinate:String? = ""//getLocation()
        var status:String? = "Not Submitted"
        var modifiedBy:String? = getUsername()
        viewModel.verificationOfficer.value = modifiedBy
        // below we have created
        // a new DBHelper class,
        // and passed context to it
        val db = DBHelper(requireContext(), null)

        // calling method to add
        //val arr = viewModel.project.value?.split("-")
        return db.addSupply(SupplyModel(
            null,
            transactionId,
            "1",
            viewModel.location.value,
            viewModel.lga.value,
            viewModel.contractor.value,
            viewModel.coordinate.value,
            viewModel.verificationDate.value,
            viewModel.verificationOfficer.value,
            viewModel.representative.value,
            viewModel.representativeDesg.value,
            viewModel.representativePhone.value,
            modifiedBy,
            status,
            viewModel.ImagePath1,
            viewModel.ImagePath2,
            viewModel.ImagePath3,
            viewModel.ImagePath4
        ))
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
        ){
            val locationManager: LocationManager = requireContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER)){
                client.lastLocation.addOnCompleteListener(requireActivity()){
                        task ->
                    if (task.isSuccessful && task.result != null) {

                        var location: Location? = task.result
                        //Log.w("LOCATION",location?.latitude.toString())
                        val coordinateLayout = binding.textLayoutSupplyCoordinate
                        (coordinateLayout?.editText as? TextInputEditText)?.setText(location?.latitude.toString()+","+location?.longitude.toString())
                    }
                }
            }
        }
    }
    fun getLGAs(state:String) {
        try {
            val db = DBHelper(requireActivity(), null)
            val lga = db.getCodeList("lga")
            var lgaList : ArrayList<String?> = ArrayList<String?>()
            lga.forEach{
                if(it.name != ""){
                    val s = it.name?.split('-')
                    if(s?.first() == state){
                        //viewModel.lgaItems.add(Item(id=it.code!!.toInt(),name = it.name.toString()))
                        lgaList.add(it.name.toString())
                    }
                }
            }
            val lgaAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
                R.layout.list_item,lgaList)
            //LGA List
            val lgaLayout =  binding.textLayoutSupplyLGA
            val actL = (lgaLayout?.editText as? AutoCompleteTextView)
            actL?.setAdapter(lgaAdapter)
            actL?.threshold = 1
        } catch (e: Exception){
            Log.w("DBError: ",e.message.toString())
        }
    }
    fun getStates() {
        try {
            val db = DBHelper(requireActivity(), null)
            val lga = db.getCodeList("lga")
            var states : ArrayList<String?> = ArrayList<String?>()
            lga.forEach{
                //Extract State Names
                if(it.name != ""){
                    val s = it.name?.split('-')
                    states.add(s?.first())
                }
                //viewModel.lgaItems.add(Item(id=it.code!!.toInt(),name = it.name.toString()))
            }
            states.distinct()
            val stateAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
                R.layout.list_item,states.distinct())
            //State List
            val stateLayout =  binding.textLayoutSupplyState
            val actS = (stateLayout?.editText as? AutoCompleteTextView)
            actS?.setAdapter(stateAdapter)
            actS?.threshold = 1
        } catch (e: Exception){
            Log.w("DBError: ",e.message.toString())
        }
    }
    fun getContractors() {
        try {
            val db = DBHelper(requireActivity(), null)
            val contractors = db.getCodeList("contractor")
            var contractorList : ArrayList<String?> = ArrayList<String?>()
            contractors.forEach{
                contractorList.add(it.code+'-'+it.name)
            }
            val cAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
                R.layout.list_item,contractorList.distinct())
            val cLayout =  binding.textLayoutSupplyContractor
            val actC = (cLayout?.editText as? AutoCompleteTextView)
            actC?.setAdapter(cAdapter)
            actC?.threshold = 1
        } catch (e: Exception){
            Log.w("DBError: ",e.message.toString())
        }
    }
    private fun getUsername():String?{
        //implementing remember me with shared preferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString("userName",null)
    }
    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.imgAttachmentSupply.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(800,480)).build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("CAMERAERROR", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    when (viewModel.attachment.value) {
                        "Picture 1" -> {
                            binding.imageViewSupply1.setImageURI(savedUri)
                            viewModel.ImagePath1 = savedUri.toString()
                        }
                        "Picture 2" -> {
                            binding.imageViewSupply2.setImageURI(savedUri)
                            viewModel.ImagePath2 = savedUri.toString()
                        }
                        "Picture 3" -> {
                            binding.imageViewSupply3.setImageURI(savedUri)
                            viewModel.ImagePath3 = savedUri.toString()
                        }
                        "Picture 4" -> {
                            binding.imageViewSupply4.setImageURI(savedUri)
                            viewModel.ImagePath4 = savedUri.toString()
                        }
                        else -> {
                            val msg = "Please select an attachment option (Picture 1,..4)"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)
                }
            })
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(SupplyViewModel::class.java)
        // TODO: Use the ViewModel
    }
}