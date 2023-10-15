package com.ubec.ubecapp.ui.details

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.forntoh.livedata_validation.validation.LiveDataValidator
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.CodeListModel
import com.ubec.ubecapp.databinding.SupplyDetailsFragmentBinding
import com.ubec.ubecapp.network.ApiAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SupplyDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = SupplyDetailsFragment()
    }
    private val sharedPrefFile = "IdentityData"
    private val viewModel: SupplyDetailsViewModel by viewModels()
    private lateinit var binding: SupplyDetailsFragmentBinding
    private var id :String? = "0"
    private var lgaList: CodeListModel?=null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        id = arguments?.getString("id")

        return with(SupplyDetailsFragmentBinding.inflate(inflater, container, false)) {
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
        binding.itemList.adapter = MyItemRecyclerViewAdapter(viewModel.itemRows!!){item->

        }
        binding.imageView1.setImageURI(viewModel.imagePath1.value)
        binding.imageView2.setImageURI(viewModel.imagePath2.value)
        binding.imageView3.setImageURI(viewModel.imagePath3.value)
        binding.imageView4.setImageURI(viewModel.imagePath4.value)

        binding.btnUpload.setOnClickListener(){
            it.isEnabled = false
            ///uploadData(id!!.toInt())
            uploadDataMultiPart(id!!.toInt())
        }
        if(viewModel.row?.status == "Submitted"){
            binding.btnUpload.isEnabled=false
            //    binding.btnSubmit.isActivated=false
            //    binding.btnSubmit.visibility=INVISIBLE
        }
        LiveDataValidator(requireContext()).observe {
            lifecycleOwner(viewLifecycleOwner)
            viewModel(viewModel)
            attachTo(binding.root)
        }
    }
    private fun loadRecord(id:Int){
        //creating the instance of DatabaseHandler class
        val db: DBHelper = DBHelper(requireContext(),null)
        var row = db.getSupply(id)
        viewModel.row = row
        viewModel.itemRows = db.getSupplyItems(id.toString())

        viewModel.contractor.value = row?.contractor
        viewModel.location.value = row?.location
        viewModel.lga.value = row?.lga
        viewModel.coordinate.value = row?.coordinate
        viewModel.imagePath1.value= Uri.parse(row?.attachment1)
        viewModel.imagePath2.value= Uri.parse(row?.attachment2)
        viewModel.imagePath3.value= Uri.parse(row?.attachment3)
        viewModel.imagePath4.value= Uri.parse(row?.attachment4)
        viewModel.representative.value = row?.representative
        viewModel.representativeDesg.value = row?.representativeDesg
        viewModel.representativePhone.value = row?.representativePhone

        lgaList = db.getCodeListByName(row?.lga)


    }

    private fun uploadDataMultiPart(id:Int){
        var row = viewModel.row

        var itemStr = Json.encodeToString(viewModel.itemRows!!.toList())
        val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        val currentDate = sdf.format(Date())

        val imgPath1: MultipartBody.Part = prepareFilePart("File1_5", Uri.parse(row?.attachment1.toString()))
        val imgPath2: MultipartBody.Part = prepareFilePart("File2_5", Uri.parse(row?.attachment2.toString()))
        val imgPath3: MultipartBody.Part = prepareFilePart("File3_5", Uri.parse(row?.attachment3.toString()))
        val imgPath4: MultipartBody.Part = prepareFilePart("File4_5", Uri.parse(row?.attachment4.toString()))

        val cArr = row?.contractor?.split('-');
        // create a map of data to pass along
        val l = getLicense()
        if(l == null){
            Toast.makeText(
                requireContext(),
                "${row?.contractor} - Verification report upload failed, please logout and login and try again",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val license: RequestBody =  createPartFromString(l.toString())
        val transactionRef: RequestBody = createPartFromString(row?.transactionId.toString())
        val contractor: RequestBody = createPartFromString(cArr?.first()!!)
        val location: RequestBody = createPartFromString(row?.location.toString())
        val coordinate: RequestBody = createPartFromString(row?.coordinate.toString())
        val lga: RequestBody = createPartFromString(lgaList?.code.toString())
        val representative: RequestBody = createPartFromString(row?.representative.toString())
        val representativePhone: RequestBody = createPartFromString(row?.representativePhone.toString())
        val representativeDesg: RequestBody = createPartFromString(row?.representativeDesg.toString())
        val verificationOfficer: RequestBody = createPartFromString(row?.verificationOfficer.toString())
        val verificationDate: RequestBody = createPartFromString(row?.verificationDate.toString())
        val modifiedBy: RequestBody = createPartFromString(row?.modifiedBy.toString())
        val status: RequestBody = createPartFromString("Submitted")
        val itemList :RequestBody = createPartFromString(itemStr)

        val map: HashMap<String, RequestBody> = HashMap()
        map["License"] = license
        map["TransactionId"] = transactionRef
        map["Location"] = location
        map["Coordinate"] = coordinate
        map["LGAId"] = lga
        map["Contractor"] = contractor
        map["Representative"] = representative
        map["RepresentativePhone"] = representativePhone
        map["RepresentativeDesg"] = representativeDesg
        map["VerificationOfficer"] = verificationOfficer
        map["VerificationDate"] = verificationDate
        map["Modifiedby"] = modifiedBy
        map["Status"] = status
        map["itemList"] = itemList

        ////row?.inspectionDate.toString(),
        MainScope().launch {
            val response = withContext(Dispatchers.IO) {
                ApiAdapter.apiClient.supplyStatuspartPathMap(map,imgPath1,imgPath2,imgPath3,imgPath4)
            }
            // Check if response was successful.
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                if(body!!.IsSuccessful){
                    val db: DBHelper = DBHelper(requireContext(),null)
                    row?.status = "Submitted"
                    db.updateSupply(row!!)
                    Toast.makeText(
                        requireContext(),
                        "${row?.contractor} - supply verification has been uploaded",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(R.id.action_nav_details_supply_to_nav_report_supplies,null,null)
                }else{
                    binding.btnUpload.isEnabled=true
                    Toast.makeText(
                        requireContext(),
                        "${row?.contractor} - supply verification upload error please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else{
                binding.btnUpload.isEnabled=true
                Toast.makeText(
                    requireContext(),
                    "${row?.contractor} - supply verification upload failed please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }


    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, descriptionString)
    }
    private fun createPartFromString2(descriptionString: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, descriptionString)
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
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(SupplyDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }
    private fun getLicense():String?{
        //implementing remember me with shared preferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString("license",null)
    }

}