package com.ubec.ubecapp.ui.newsupply

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forntoh.livedata_validation.rule.NotEmptyRule
import com.forntoh.livedata_validation.validation.ValidatorViewModel
import com.ubec.ubecapp.R

class SupplyViewModel : ValidatorViewModel() {
    var ImagePath1:String? = null
    var ImagePath2:String? = null
    var ImagePath3:String? = null
    var ImagePath4:String? = null

    val attachmentItems = listOf("Picture 1", "Picture 2", "Picture 3", "Picture 4")

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

    override fun validate() {
        validator
            .addField(contractor, R.id.textLayoutSupplyContractor, NotEmptyRule("Contractor name required"))
            .addField(location, R.id.textLayoutSupplyLocation, NotEmptyRule("Location required"))
            .addField(lga, R.id.textLayoutSupplyLGA, NotEmptyRule("LGA name required"))
            .addField(state, R.id.textLayoutSupplyState, NotEmptyRule("State name required"))
            .addField(coordinate, R.id.textLayoutSupplyCoordinate, NotEmptyRule("Coordinate required"))
            .addField(representative, R.id.textLayoutSupplyRep, NotEmptyRule("Representative full-name is required"))
            .addField(representativeDesg, R.id.textLayoutSupplyRepDesg, NotEmptyRule("Representative designation is required"))
            .addField(representativePhone, R.id.textLayoutSupplyRepPhone, NotEmptyRule("Representative phone-number is required"))
    }


}