package com.ubec.ubecapp.network

import com.ubec.ubecapp.network.contracts.v1.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    //Login Method for mobile user login
    @POST("login")
    @FormUrlEncoded
    suspend  fun apiLogin(@Field("username") username: String,
                          @Field("password") password: String): Response<LoginResponse>
    //Method to get all list of Contractors
    @GET("GetContractors")
    suspend fun getContractors():Response<ContractorsResponse>

    //Method to get all list of Local Government Areas
    @GET("GetLGAs")
    suspend fun getLGAs():Response<LGAsResponse>
    //Method to get list of Projects Allocated
    @GET("GetProjects")
    suspend fun getProjects():Response<ProjectsResponse>
    //Method to get list of Milestones categories based on selected project
    @GET("GetMilestone")
    suspend fun getMilestones():Response<MilestoneResponse>

    //Method to submit project report status
    @POST("reportprojectstatus")
    @FormUrlEncoded
    suspend  fun reportStatus(
        @Field("TransactionId") transactionRef: String,
        @Field("ProjectId") projectId: Int,
        @Field("Location") location: String,
        @Field("Coordinate") coordinate: String,
        @Field("LGAId") lga: String,
        @Field("StageOfCompletion") completionStage: String,
        @Field("DescriptionOfCompletion") completionDesc: String,
        @Field("ProjectQuality") quality: String,
        @Field("HasDefect") hasDefect: String,
        @Field("DescriptionOfDefect") defectDesc: String,
        @Field("ContractSum") contractSum: Double,
        @Field("Status") status: String,
        @Field("Modifiedby") modifiedBy: String,
        @Field("InspectionDate") inspectionDate: String,
        @Field("Attachment1") image1: ByteArray?,
        @Field("Attachment2") image2: ByteArray?,
    ): Response<RecordStatusResponse>

    //Method to submit project report status with associated supporting documents
    @Multipart
    @POST("reportprojectstatusmultipart")
    suspend  fun reportStatusPathMap(@PartMap() partMap:Map<String,  @JvmSuppressWildcards RequestBody>,
                                     @Part file_1: MultipartBody.Part ,
                                     @Part file_2: MultipartBody.Part,
                                     @Part file_3: MultipartBody.Part,
                                     @Part file_4: MultipartBody.Part ): Response<RecordStatusResponse>

    //Method to submit verification status report with associated supporting documents
    @Multipart
    @POST("ReportSupplyStatusMultipart")
    suspend  fun supplyStatuspartPathMap(@PartMap() partMap:Map<String,  @JvmSuppressWildcards RequestBody>,
                                     @Part file_1: MultipartBody.Part ,
                                     @Part file_2: MultipartBody.Part,
                                     @Part file_3: MultipartBody.Part,
                                     @Part file_4: MultipartBody.Part ): Response<SupplyResponse>

}