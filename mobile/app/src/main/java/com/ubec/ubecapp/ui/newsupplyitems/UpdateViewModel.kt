package com.ubec.ubecapp.ui.newsupplyitems

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forntoh.livedata_validation.rule.NotEmptyRule
import com.forntoh.livedata_validation.validation.ValidatorViewModel
import com.ubec.ubecapp.R

class UpdateViewModel : ValidatorViewModel() {

    val id = MutableLiveData("")
    val supplyId = MutableLiveData("")
    val serialNo = MutableLiveData("")
    val description = MutableLiveData("")
    val quantityOrdered = MutableLiveData("")
    val quantityDelivered = MutableLiveData("")
    val remarks = MutableLiveData("")

    override fun validate() {
        validator
            .addField(supplyId, R.id.textLayoutSupplyContractor, NotEmptyRule("Supply identifiction number is required"))
            .addField(serialNo, R.id.textLayoutSupplyLocation, NotEmptyRule("Item serial number is required"))
            .addField(description, R.id.textLayoutSupplyLGA, NotEmptyRule("Item description is required"))
            .addField(quantityOrdered, R.id.textLayoutSupplyState, NotEmptyRule("Quantity ordered is required"))
            .addField(quantityDelivered, R.id.textLayoutSupplyCoordinate, NotEmptyRule("Quantity delivered is required"))
            .addField(remarks, R.id.textLayoutSupplyRep, NotEmptyRule("Remark is required"))
    }

}