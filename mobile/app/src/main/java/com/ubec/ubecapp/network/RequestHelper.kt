package com.ubec.ubecapp.network

import android.content.Context
import android.net.Uri
import okhttp3.MultipartBody

import okhttp3.RequestBody

import android.os.FileUtils

import androidx.annotation.NonNull
import androidx.core.content.ContentProviderCompat.requireContext
import okhttp3.MediaType
import java.io.File


class RequestHelper {
    fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, descriptionString)
    }

     fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
         return try{
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        val file: File = File(fileUri.path)

        // create RequestBody instance from file
        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse(context.contentResolver.getType(fileUri)),
            file
        )

        // MultipartBody.Part is used to send also the actual file name
         MultipartBody.Part.createFormData(partName, file.name, requestFile)
         }catch (ex:Exception){
             ex
             MultipartBody.Part.createFormData(null,null,null)
         }

    }
}