package com.ubec.ubecapp.ui.details

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.forntoh.livedata_validation.validation.LiveDataValidator
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.CodeListModel
import com.ubec.ubecapp.databinding.DetailsFragmentBinding
import com.ubec.ubecapp.network.ApiAdapter
import com.ubec.ubecapp.network.RequestHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.content.ContentResolver
import android.content.SharedPreferences
import android.webkit.MimeTypeMap








class DetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private val sharedPrefFile = "IdentityData"
    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var binding: DetailsFragmentBinding
    private var id :String? = "0"
    private var lgaList:CodeListModel?=null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        id = arguments?.getString("id")

        return with(DetailsFragmentBinding.inflate(inflater, container, false)) {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
            binding = this
            root
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()
        loadRecord(id!!.toInt())
        binding.imageView1.setImageURI(viewModel.imagePath1.value)
        binding.imageView2.setImageURI(viewModel.imagePath2.value)
        binding.imageView3.setImageURI(viewModel.imagePath3.value)
        binding.imageView4.setImageURI(viewModel.imagePath4.value)

        binding.btnSubmit.setOnClickListener(){
            it.isEnabled = false
            ///uploadData(id!!.toInt())
            uploadDataMultiPart(id!!.toInt())
        }
        if(viewModel.row?.status == "Submitted"){
            binding.btnSubmit.isEnabled=false
        //    binding.btnSubmit.isActivated=false
        //    binding.btnSubmit.visibility=INVISIBLE
        }
        LiveDataValidator(requireContext()).observe {
            lifecycleOwner(viewLifecycleOwner)
            viewModel(viewModel)
            attachTo(binding.root)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun loadRecord(id:Int){
        //creating the instance of DatabaseHandler class
        val db: DBHelper = DBHelper(requireContext(),null)
        viewModel.row = db.getReports(id)
        var row= viewModel.row
        viewModel.projectRow = db.getProjects2(row?.projectId)

        viewModel.project.value = row?.projectId
        viewModel.location.value = row?.location
        viewModel.lga.value = row?.lga
        viewModel.coordinate.value = row?.coordinate
        viewModel.completionStage.value = row?.completionStage
        viewModel.completionDesc.value = row?.completionDesc
        viewModel.projectQuality.value = row?.quality
        viewModel.hasDefects.value = row?.hasDefects
        viewModel.defectDesc.value = row?.defectDesc
        viewModel.imagePath1.value= Uri.parse(row?.attachment1)
        viewModel.imagePath2.value= Uri.parse(row?.attachment2)
        viewModel.imagePath3.value= Uri.parse(row?.attachment3)
        viewModel.imagePath4.value= Uri.parse(row?.attachment4)
        viewModel.contractor.value = row?.contractor
        viewModel.description.value = row?.description
        viewModel.inspectionDate.value = row?.inspectionDate

        lgaList = db.getCodeListByName(row?.lga)


    }
    private fun uploadData(id:Int){
        val requestHelper: RequestHelper = RequestHelper()
        var row = viewModel.row
        val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        val currentDate = sdf.format(Date())

        val image1 = readBytes(requireContext(), Uri.parse(row?.attachment1.toString()))
        val image2 = readBytes(requireContext(), Uri.parse(row?.attachment2.toString()))
        // create part for file (photo, video, ...)
        val imgPath1: MultipartBody.Part = requestHelper.prepareFilePart(requireContext(),"Attachment1", Uri.parse(row?.attachment1.toString()))
        val imgPath2: MultipartBody.Part = requestHelper.prepareFilePart(requireContext(),"Attachment2", Uri.parse(row?.attachment2.toString()))
        ////row?.inspectionDate.toString(),
        MainScope().launch {
            val response = withContext(Dispatchers.IO) {
                ApiAdapter.apiClient.reportStatus(
                    transactionRef = row?.transactionId.toString(),
                    projectId = row?.pId!!.toInt(),
                    location = row?.location.toString(),
                    coordinate =  row?.coordinate.toString(),
                    lga =lgaList?.code.toString(),
                    completionStage = row?.completionStage.toString(),
                    completionDesc =  row?.completionDesc.toString(),
                    quality = row?.quality.toString(),
                    hasDefect = row?.hasDefects.toString(),
                    defectDesc = row?.defectDesc.toString(),
                    contractSum = 0.00, status = "draft",
                    modifiedBy = row?.modifiedBy.toString(),
                    inspectionDate = currentDate,
                    image1 = image1,
                    image2 = image2
                )
            }
            // Check if response was successful.
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                if(body!!.IsSuccessful){
                    val db: DBHelper = DBHelper(requireContext(),null)
                    row?.status = "Submitted"
                    db.updateReport(row!!)
                    Toast.makeText(
                        requireContext(),
                        "${row?.serialNo} Status has been uploaded",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(R.id.action_nav_details_to_nav_reports,null,null)
                }
            }
        }
    }

    private fun uploadDataMultiPart(id:Int){
        //val requestHelper: RequestHelper = RequestHelper()
        var row = viewModel.row
        var project = viewModel.projectRow
        //val p =
        val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        val currentDate = sdf.format(Date())

        //val image1 = readBytes(requireContext(), Uri.parse(row?.attachment1.toString()))
        //val image2 = readBytes(requireContext(), Uri.parse(row?.attachment2.toString()))
        // create part for file (photo, video, ...)
        val imgPath1: MultipartBody.Part = prepareFilePart("File_1", Uri.parse(row?.attachment1.toString()))
        val imgPath2: MultipartBody.Part = prepareFilePart("File_2", Uri.parse(row?.attachment2.toString()))
        val imgPath3: MultipartBody.Part = prepareFilePart("File_3", Uri.parse(row?.attachment3.toString()))
        val imgPath4: MultipartBody.Part = prepareFilePart("File_4", Uri.parse(row?.attachment4.toString()))

        // create a map of data to pass along
        val l = getLicense()
        if(l == null){
            Toast.makeText(
                requireContext(),
                "${row?.serialNo} -Status report upload failed, please logout and login and try again",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val license: RequestBody =  createPartFromString(l.toString())
        val transactionRef: RequestBody = createPartFromString(row?.transactionId.toString())
        val projectId: RequestBody = createPartFromString(project?.projectId.toString())
        val location: RequestBody = createPartFromString(row?.location.toString())
        val coordinate: RequestBody = createPartFromString(row?.coordinate.toString())
        val lga: RequestBody = createPartFromString(lgaList?.code.toString())
        val completionStageId: RequestBody = createPartFromString(row?.completionStageId.toString())
        val completionStage: RequestBody = createPartFromString(row?.completionStage.toString())
        val completionDesc: RequestBody = createPartFromString(row?.completionDesc.toString())
        val quality: RequestBody = createPartFromString(row?.quality.toString())
        val hasDefect: RequestBody = createPartFromString(row?.hasDefects.toString())
        var defectDesc: RequestBody
        if(row?.defectDesc == null){
            defectDesc = createPartFromString("Not Applicable")
        }else {
            defectDesc = createPartFromString(row?.defectDesc.toString())
        }
        //val contractSum: RequestBody = createPartFromString("0.00")
        val modifiedBy: RequestBody = createPartFromString(row?.modifiedBy.toString())
        //val inspectionStatus: RequestBody = createPartFromString(row?.status.toString())
        val inspectionStatus: RequestBody = createPartFromString("Submitted")
        val inspectionDate: RequestBody = createPartFromString(currentDate)
        //val image1: RequestBody = createPartFromString("")
        //val image2: RequestBody = createPartFromString("")

        val map: HashMap<String, RequestBody> = HashMap()
        map["License"] = license
        map["TransactionId"] = transactionRef
        map["ProjectId"] = projectId
        map["Location"] = location
        map["Coordinate"] = coordinate
        map["LGAId"] = lga
        map["StageOfCompletionId"] = completionStageId
        map["StageOfCompletion"] = completionStage
        map["DescriptionOfCompletion"] = completionDesc
        map["ProjectQuality"] = quality
        map["HasDefect"] = hasDefect
        map["DescriptionOfDefect"] = defectDesc
        //map["DescriptionOfDefect"] = contractSum
        map["Modifiedby"] = modifiedBy
        map["Status"] = inspectionStatus
        map["inspectionDate"] = inspectionDate
        //map["image1"] = image1
        //map["image2"] = image2

        ////row?.inspectionDate.toString(),
        MainScope().launch {
            val response = withContext(Dispatchers.IO) {
                ApiAdapter.apiClient.reportStatusPathMap(map,imgPath1,imgPath2,imgPath3,imgPath4)
            }
            // Check if response was successful.
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                if(body!!.IsSuccessful){
                    val db: DBHelper = DBHelper(requireContext(),null)
                    row?.status = "Submitted"
                    db.updateReport(row!!)
                    Toast.makeText(
                        requireContext(),
                        "${row?.serialNo} -Status report has been uploaded",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(R.id.action_nav_details_to_nav_reports,null,null)
                }else{
                    binding.btnSubmit.isEnabled=true
                    Toast.makeText(
                        requireContext(),
                        "${row?.serialNo} -Status report upload error please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else{
                binding.btnSubmit.isEnabled=true
                Toast.makeText(
                    requireContext(),
                    "${row?.serialNo} -Status report upload failed please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }


    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, descriptionString)
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        return try{
            // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
            // use the FileUtils to get the actual file by uri
            val file: File = File(fileUri.path)

            // create RequestBody instance from file
            val requestFile: RequestBody = RequestBody.create(
                MediaType.parse(getMimeType(requireContext(),fileUri)),
                file
            )
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
            val newName = "$partName.$fileExtension"
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part.createFormData(partName, newName, requestFile)
        }catch (ex:Exception){
            ex
            MultipartBody.Part.createFormData(null,null,null)
        }

    }
    private fun getMimeType(context: Context, uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                .toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault()))
        }
        return mimeType
    }

    private fun getUsername():String?{
        //implementing remember me with shared preferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString("userName",null)
    }
    private fun getLicense():String?{
        //implementing remember me with shared preferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString("license",null)
    }
}