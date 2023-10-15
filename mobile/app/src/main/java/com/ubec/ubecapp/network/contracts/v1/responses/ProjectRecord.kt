package com.ubec.ubecapp.network.contracts.v1.responses

data class ProjectRecord (
    var ProjectId:Int = 0,
    var SerialNo:String? = "",
    var Workflow:String? = "",
    var ProjectType:String? = "",
    var Contractor:String? = "",
    var Description:String? = "",
    var OwnedBy:String? = "",
)