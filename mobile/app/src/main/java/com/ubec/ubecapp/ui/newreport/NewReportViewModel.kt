package com.ubec.ubecapp.ui.newreport

import android.R.attr
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.ubec.ubecapp.R
import com.forntoh.livedata_validation.rule.NotEmptyRule
import com.forntoh.livedata_validation.rule.LengthRangeRule
import com.forntoh.livedata_validation.validation.ValidatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.R.string

import android.R.attr.tag




class NewReportViewModel : ValidatorViewModel() {
    var ImagePath1:String? = null
    var ImagePath2:String? = null
    var ImagePath3:String? = null
    var ImagePath4:String? = null
    var MilestoneId:Int = 0
    var Milestones: ArrayList<Item> = ArrayList<Item>()
    //var codeList:CodeList = CodeList(0,"")
    //var codeArrList = ArrayList<CodeList>()
    //var contractorItems : ArrayList<CodeList> = ArrayList<CodeList>()

    var projectItems : ArrayList<Item> = ArrayList<Item>()
    var lgaItems : ArrayList<Item> = ArrayList<Item>()
    val percentageItems : ArrayList<Item> = ArrayList<Item>()
    //val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
    val qualityItems = listOf("Poor", "Fair","Good")
    //val percentageItems = listOf("5%", "10%","15%","20%","25%","30%","35%",
    //    "40%","45%","50%","55%","60%","65%","70%","75%","80%","85%","90%","95%","100%")
    val defectItems = listOf("No", "Yes")
    val attachmentItems = listOf("Project symbol", "Front view", "Back view", "Inside view")

    val project = MutableLiveData("")
    //val contractor = MutableLiveData("")
    //val description = MutableLiveData("")
    val location = MutableLiveData("")
    val lga = MutableLiveData("")
    val state = MutableLiveData("")
    val projectQuality = MutableLiveData("")
    val completionStage = MutableLiveData("")
    val completionDesc = MutableLiveData("")
    val hasDefects = MutableLiveData("")
    val defectDesc = MutableLiveData("")
    //val serialNo = MutableLiveData("")
    val coordinate = MutableLiveData("")
    var inspectionDate = MutableLiveData("")
    var attachment = MutableLiveData("")


    override fun validate() {
        validator

            //.addField(contractor, R.id.textLayoutContractor, NotEmptyRule("Contractor name required"))
            //.addField(description, R.id.textLayoutDescription, NotEmptyRule("Project Description required"))
            .addField(location, R.id.textLayoutLocation, NotEmptyRule("Location required"))
            .addField(lga, R.id.textLayoutLGA, NotEmptyRule("LGA name required"))
            .addField(state, R.id.textLayoutState, NotEmptyRule("State name required"))
            .addField(projectQuality, R.id.textLayoutProductQuality, NotEmptyRule("Project Quality required"))
            .addField(completionStage, R.id.textLayoutStageCompletion, NotEmptyRule("Completion Stage required"))
            .addField(completionDesc, R.id.textLayoutDescCompletion, NotEmptyRule("Completion Description required"))
            //.addField(defectDesc, R.id.textLayoutDescDefects)
            .addField(hasDefects, R.id.textLayouthasDefects, NotEmptyRule("Has Defect required"))
            //.addField(serialNo, R.id.textLayoutSerialNo, NotEmptyRule("Serial No. required"))
            .addField(coordinate, R.id.textLayoutCoordinate, NotEmptyRule("Coordinate required"))
            //.addField(inspectionDate,R.id.textLayoutInspectionDate,NotEmptyRule("Inspection Date required"))
            .addField(project, R.id.textLayoutProject, NotEmptyRule("Project name required"))
    }


}


 class Item(var id: Int,var name: String,var projectType:String?="" ) {
    override fun toString(): String {
        return name
    }
}