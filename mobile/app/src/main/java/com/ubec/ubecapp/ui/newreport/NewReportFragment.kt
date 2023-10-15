package com.ubec.ubecapp.ui.newreport

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.ReportModel
import com.ubec.ubecapp.network.contracts.v1.responses.CodeList
import com.forntoh.livedata_validation.validation.LiveDataValidator
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText
import com.ubec.ubecapp.databinding.NewReportFragmentBinding
import com.ubec.ubecapp.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent.getIntent
import android.graphics.BitmapFactory
import android.util.Size
import android.view.View.INVISIBLE
import android.widget.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ubec.ubecapp.ui.reports.ReportsFragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NewReportFragment : Fragment() {

    companion object {
        fun newInstance() = NewReportFragment()
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    private lateinit var client: FusedLocationProviderClient
    private val viewModel: NewReportViewModel by viewModels()
    private lateinit var binding: NewReportFragmentBinding
    private val sharedPrefFile = "IdentityData"
    private lateinit var projectAdapter:ArrayAdapter<Item>
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        client = LocationServices.getFusedLocationProviderClient(getActivity())
        getLastLocation()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        //startCamera()

        return with(NewReportFragmentBinding.inflate(inflater, container, false)) {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
            binding = this
            root
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        ){
            val locationManager:LocationManager = requireContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                client.lastLocation.addOnCompleteListener(requireActivity()){
                        task ->
                    if (task.isSuccessful && task.result != null) {

                        var location:Location? = task.result
                        //Log.w("LOCATION",location?.latitude.toString())
                        val coordinateLayout = binding.textLayoutCoordinate
                        (coordinateLayout?.editText as? TextInputEditText)?.setText(location?.latitude.toString()+","+location?.longitude.toString())
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()
        //val adapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.items)

        val qualityAdapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.qualityItems)
        val defectAdapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.defectItems)
        var attachmentAdapter = ArrayAdapter(requireContext(),R.layout.list_item,viewModel.attachmentItems)
        //product Quality List
        val productQualityLayout =  binding.textLayoutProductQuality
        (productQualityLayout?.editText as? AutoCompleteTextView)?.setAdapter(qualityAdapter)

        //Defect List
        val defectLayout =  binding.textLayouthasDefects
        (defectLayout?.editText as? AutoCompleteTextView)?.setAdapter(defectAdapter)
        //Attachment List
        var attachmentLayout = binding.textLayoutAttachment
        (attachmentLayout?.editText as? AutoCompleteTextView)?.setAdapter(attachmentAdapter)

        getProjects()
        getStates()
        //getLGAs()

        // Set up the listener for take photo button
        binding.cameraStartButton.setOnClickListener { startCamera() }
        binding.cameraCaptureButton.setOnClickListener { takePhoto() }

        var hd = (binding.textLayouthasDefects.editText as? AutoCompleteTextView)
        hd!!.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            var dd = (binding.textLayoutDescDefects.editText as? TextInputEditText)
            dd!!.isEnabled = selectedItem != "No"
        }

        var st = (binding.textLayoutState.editText as? AutoCompleteTextView)
        st!!.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            getLGAs(selectedItem)
        }

        var pr = (binding.textLayoutProject.editText as? AutoCompleteTextView)
        pr!!.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position) as? Item
            val db = DBHelper(requireActivity(), null)
            val milestones = db.getMilestones(selectedItem?.projectType)
            //Stage of Completion List
            milestones.forEach{
                        viewModel.percentageItems.add(Item(id=it.id!!.toInt(),name = (it.percentage+"-"+it.description)))

            }
            val percentageAdapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.percentageItems)
            val stageOfCompletionLayout =  binding.textLayoutStageCompletion
            (stageOfCompletionLayout?.editText as? AutoCompleteTextView)?.setAdapter(percentageAdapter)
        }

        var sc = (binding.textLayoutStageCompletion.editText as? AutoCompleteTextView)
        sc!!.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
          var arrItem = selectedItem.split('-')
            //var desc = (binding.textLayoutDescCompletion.editText as? TextInputEditText)
            //desc!!. = arrItem[1]
            var item = viewModel.percentageItems[position]
            //viewModel.completionStage.value = arrItem[0]
            viewModel.completionDesc.value = arrItem[1]
            viewModel.MilestoneId = item.id
        }



        LiveDataValidator(requireContext()).observe {
            lifecycleOwner(viewLifecycleOwner)
            viewModel(viewModel)
            attachTo(binding.root)
        }

        binding.btnSubmit.setOnClickListener {
            if(viewModel.ImagePath1 != null && viewModel.ImagePath2 !=null && viewModel.ImagePath3!=null&&viewModel.ImagePath4!=null){
                val resp = onAdd()
                if(resp != -1L){
                    // Toast to message on the screen
                    Toast.makeText(requireContext(), viewModel.project.value + " added to database", Toast.LENGTH_LONG).show()
                    binding.btnSubmit.isActivated = false
                    binding.btnSubmit.visibility = INVISIBLE
                    val bundle = bundleOf("id" to resp.toInt() )
                    navController.navigate(R.id.action_nav_new_report_to_nav_reports,bundle,null)

                }
            }else{
                Toast.makeText(requireContext(), "Please upload all attachments/pictures ", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun randomUUID() = UUID.randomUUID().toString()
    private fun onAdd():Long{
        val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        var transactionId:String? = randomUUID()
        //var coordinate:String? = ""//getLocation()
        var status:String? = "Not Submitted"
        var modifiedBy:String? = getUsername()
        // below we have created
        // a new DBHelper class,
        // and passed context to it
        val db = DBHelper(requireContext(), null)

        // calling method to add
        val arr = viewModel.project.value?.split("-")
        val arrStage = viewModel.completionStage.value?.split("-")
        return db.addReport(ReportModel(
            null,
            arr?.first(),
            viewModel.location.value,
            viewModel.lga.value,
            viewModel.projectQuality.value,
            viewModel.MilestoneId,
            arrStage?.first(),
            viewModel.completionDesc.value,
            viewModel.hasDefects.value,
            viewModel.defectDesc.value,
            transactionId,
            viewModel.coordinate.value,
            status,
            currentDate,
            modifiedBy,
            viewModel.ImagePath1,
            viewModel.ImagePath2,
            null,
            null,
            null,
            null,null,
            viewModel.ImagePath3,
            viewModel.ImagePath4
        ))
    }

    fun getLGAs(state:String) {
        viewModel.lgaItems = ArrayList<Item>()
        try {
            val db = DBHelper(requireActivity(), null)
            val lga = db.getCodeList("lga")
            lga.forEach{
                if(it.name != ""){
                    val s = it.name?.split('-')
                    if(s?.first() == state){
                        viewModel.lgaItems.add(Item(id=it.code!!.toInt(),name = it.name.toString()))
                    }
                }
            }
            val lgaAdapter:ArrayAdapter<Item> = ArrayAdapter<Item>(requireContext(),R.layout.list_item,viewModel.lgaItems)
            //LGA List
            val lgaLayout =  binding.textLayoutLGA
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
            val stateAdapter:ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),R.layout.list_item,states.distinct())
            //State List
            val stateLayout =  binding.textLayoutState
                    val actS = (stateLayout?.editText as? AutoCompleteTextView)
            actS?.setAdapter(stateAdapter)
            actS?.threshold = 1
        } catch (e: Exception){
            Log.w("DBError: ",e.message.toString())
        }
    }

    fun getProjects() {
        try {
            val db = DBHelper(requireActivity(), null)
            val rows = db.getProjects()
            rows.forEach{
                viewModel.projectItems.add(Item(id=it.projectId!!.toInt(),name = (it.projectId+"-"+it.serialNo+"-"+it.description),projectType = it.projectType))
            }
            projectAdapter = ArrayAdapter<Item>(requireContext(),R.layout.list_item,viewModel.projectItems)
            //Project List
            val projectLayout =  binding.textLayoutProject
            val act = (projectLayout?.editText as? AutoCompleteTextView)
            act?.setAdapter(projectAdapter)
            act?.threshold = 1
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
                    it.setSurfaceProvider(binding.imgAttachment1.surfaceProvider)
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
                        "Project symbol" -> {
                            binding.imageView1.setImageURI(savedUri)
                            viewModel.ImagePath1 = savedUri.toString()
                        }
                        "Front view" -> {
                            binding.imageView2.setImageURI(savedUri)
                            viewModel.ImagePath2 = savedUri.toString()
                        }
                        "Back view" -> {
                            binding.imageView3.setImageURI(savedUri)
                            viewModel.ImagePath3 = savedUri.toString()
                        }
                        "Inside view" -> {
                            binding.imageView4.setImageURI(savedUri)
                            viewModel.ImagePath4 = savedUri.toString()
                        }
                        else -> {
                            val msg = "Please select an attachment option (Project symbol,Front view,Back view,Inside view)"
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
        //viewModel = ViewModelProvider(this).get(NewReportViewModel::class.java)
        // TODO: Use the ViewModel
        getLastLocation()


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }
    override fun onDestroyView() {
        super.onDestroyView()
       // binding = null
        cameraExecutor.shutdown()
    }

}



