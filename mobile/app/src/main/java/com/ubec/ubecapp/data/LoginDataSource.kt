package com.ubec.ubecapp.data

import android.util.Log
import com.ubec.ubecapp.network.contracts.v1.responses.LoginResponse
import com.ubec.ubecapp.data.model.LoggedInUser
import java.io.IOException
import com.ubec.ubecapp.network.ApiAdapter
import kotlinx.coroutines.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {


    suspend fun login(username: String, password: String): Result<LoggedInUser> {

        return try {
            // TODO: handle loggedInUser authentication
            var task =  CoroutineScope(Dispatchers.IO).async{ apiLogin(username,password)}
            var resp = task.await();
            //authentication
            if(resp?.IsSuccessful == true){
                val realUser = LoggedInUser(resp?.UserId.toString(), resp?.Username.toString(),resp?.License.toString())
                Result.Success(realUser)
            }else{
                Result.Error(IOException("resp?.Message.toString()"))
            }

        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }



    fun logout() {
        // TODO: revoke authentication
    }

     suspend fun apiLogin(username:String,password:String) :LoginResponse?{
         var data:LoginResponse? = null
        // CoroutineScope(Dispatchers.IO).launch {
        // Try catch block to handle exceptions when calling the API.
        try {
            val response = ApiAdapter.apiClient.apiLogin(username,password)
            // Check if response was successful.
            if (response.isSuccessful && response.body() != null) {
                data = response.body()
            }
            } catch (e: Exception){
                Log.w("APIError: ",e.message.toString())
            }
         //}
         return data
    }



}