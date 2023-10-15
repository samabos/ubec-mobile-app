package com.ubec.ubecapp.data.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SupplyItemModel(
    var id:String?,
    var supplyId:String?,
    var serialNo:String?,
    var description : String?,
    var quantityOrdered:String?,
    var quantityDelivered:String?,
    var remarks:String?,
    var modifiedBy:String?,
)
