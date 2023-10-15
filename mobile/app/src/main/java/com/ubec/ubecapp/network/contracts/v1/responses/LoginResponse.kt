package com.ubec.ubecapp.network.contracts.v1.responses

import androidx.annotation.Keep

@Keep
 class LoginResponse : StatusMessage(){
  var Role: Array<String?>? = null
 var Username:String? = null
 var UserId:String? = null
 var License:String? = null
 }
