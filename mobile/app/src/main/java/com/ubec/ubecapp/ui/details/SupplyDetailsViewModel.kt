package com.ubec.ubecapp.ui.details

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.forntoh.livedata_validation.validation.ValidatorViewModel
import com.ubec.ubecapp.data.model.SupplyItemModel
import com.ubec.ubecapp.data.model.SupplyModel

class SupplyDetailsViewModel : ValidatorViewModel() {
    var row: SupplyModel? = null
    var itemRows: List<SupplyItemModel>? = null
    var uri: Uri = Uri.parse("")
    var imagePath1=MutableLiveData(uri)
    var imagePath2=MutableLiveData(uri)
    var imagePath3=MutableLiveData(uri)
    var imagePath4=MutableLiveData(uri)


    val contractor = MutableLiveData("")
    val location = MutableLiveData("")
    val lga = MutableLiveData("")
    val state = MutableLiveData("")
    val coordinate = MutableLiveData("")
    var verificationDate = MutableLiveData("")
    val verificationOfficer = MutableLiveData("")
    val representative = MutableLiveData("")
    val representativeDesg = MutableLiveData("")
    val representativePhone = MutableLiveData("")
    var attachment = MutableLiveData("")

}