package com.ubec.ubecapp.ui.details

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forntoh.livedata_validation.validation.ValidatorViewModel
import com.ubec.ubecapp.data.model.ProjectModel
import com.ubec.ubecapp.data.model.ReportModel
import com.ubec.ubecapp.ui.newreport.Item

class DetailsViewModel : ValidatorViewModel() {
    var row: ReportModel? = null
    var projectRow: ProjectModel? = null
    var uri:Uri = Uri.parse("")
    var imagePath1=MutableLiveData(uri)
    var imagePath2=MutableLiveData(uri)
    var imagePath3=MutableLiveData(uri)
    var imagePath4=MutableLiveData(uri)


    val project = MutableLiveData("")
    val contractor = MutableLiveData("")
    val description = MutableLiveData("")
    val location = MutableLiveData("")
    val lga = MutableLiveData("")
    val projectQuality = MutableLiveData("")
    val completionStage = MutableLiveData("")
    val completionDesc = MutableLiveData("")
    val hasDefects = MutableLiveData("")
    val defectDesc = MutableLiveData("")
    //val serialNo = MutableLiveData("")
    val coordinate = MutableLiveData("")
    var inspectionDate = MutableLiveData("")
    var attachment = MutableLiveData("")
}