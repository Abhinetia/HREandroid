package com.android.hre.api

import com.android.hre.response.*
import com.android.hre.response.attenda.LoginpageAttendance
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.countupdate.CountList
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.getconve.Conversation
import com.android.hre.response.grn.GrnList
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.response.listmaterial.ListMaterials
import com.android.hre.response.pcns.PCN
import com.android.hre.response.tickets.TicketList
import com.android.hre.response.viewmoreindent.ViewMoreIndent
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.*


interface Api {

    @FormUrlEncoded
    @POST("validate-login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password:String
    ) :Call<LoginResponse>

    @FormUrlEncoded
    @POST("get-material-details")
   fun getUserMaterial(
       @Field("material_id") material_id:String,
       @Field("material_name") material_name:String,
       @Field("material_brand") material_brand :String
   ) :Call<Getmaterials>

   @FormUrlEncoded
   @POST("get-material-list")
   fun getListMaterial(
        @Field("user_id") user_id:String
   ) : Call<ListMaterials>


   @GET("grn")
   fun getGRnDetails(
       @Query("user_id") user_id: String
   ) :Call<GrnList>

   @FormUrlEncoded
   @POST("update-grn")
   fun updateGrn(
       @Field("user_id") user_id: String,
       @Field("grn") grn :String,
       @Field("approved") approved:String,
       @Field("rejected") rejected:String
   ) :Call<CountList>

   @FormUrlEncoded
   @POST("get-indents")
   fun getIndents(
       @Field("user_id") user_id :String
   ) :Call<GetIndentsHome>


    @FormUrlEncoded
    @POST("pcns")
    fun getAllPcns(
       @Field("user_id") user_id: String
    ) : Call<PCN>


    @FormUrlEncoded
    @POST("get-indent-details")
    fun getViewMoreIndent(
        @Field("user_id") user_id :String,
        @Field("indent_id") indent_id :String
    )  :Call<ViewMoreIndent>

    @Multipart
    @POST("create-ticket")
    fun uploadData(
        @Part("user_id") userId: RequestBody,
        @Part("pcn") pcn: RequestBody,
        @Part("priority") priority: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("issue") issue: RequestBody,
        @Part("recipient") recipient: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<TicketCreated>

   @FormUrlEncoded
   @POST("get-employees")
    fun getEmployee(
        @Field("user_id") user_id :String
    ) : Call<EmployeeList>

    @FormUrlEncoded
    @POST("get-tickets")
    fun getTickets(
        @Field("user_id") user_id :String
    )  :Call<TicketList>


    @FormUrlEncoded
    @POST("get-conversation")
    fun getConverstaion(
        @Field("user_id") user_id :String,
        @Field("ticket_id") ticket_id: String,
        @Field("ticket_no") ticket_no :String
    ) :Call<Conversation>


    @FormUrlEncoded
    @POST("attendance")
    fun getattendance(
        @Field("user_id") user_id:String,
        @Field("action") action:String,
        @Field("time") time:String,
        @Field("lattitude") lattitude:String,
        @Field("longitude") longitude:String
    ) :Call<LoginpageAttendance>

    @FormUrlEncoded
    @POST("get-departments")
    fun getdepartment(
        @Field("user_id") user_id:String
    ) :Call<GetDepartment>

    @FormUrlEncoded
    @POST("myattendance")
    fun getattendance(
        @Field("user_id") user_id:String,
        @Field("start_date") start_date:String,
        @Field("end_date") end_date:String
    ) :Call<AttendanceListData>


    @Headers("Content-Type: application/json")
    @POST("create-indent")
     fun createIndent(@Body request: CreateIndentRequest): Call<IndentResponse>
}


