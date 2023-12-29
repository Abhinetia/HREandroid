package com.android.hre.api

import com.android.hre.response.*
import com.android.hre.response.ApprovalPettyCash.AprrovalPettyCash
import com.android.hre.response.asignticket.ticketAssign
import com.android.hre.response.attenda.LoginpageAttendance
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.countupdate.CountList
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.getconve.Conversation
import com.android.hre.response.grn.GrnList
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.response.homeresponse.DashbardData
import com.android.hre.response.listmaterial.ListMaterials
import com.android.hre.response.newindentrepo.NewIndents
import com.android.hre.response.newticketReponse.TikcetlistNew
import com.android.hre.response.newvault.NewVaultDetiailsMainFolder
import com.android.hre.response.pcns.PCN
import com.android.hre.response.pettycashDetails.PettyCashDetails
import com.android.hre.response.pettycashfirstscreen.PettyCashFirstScreen
import com.android.hre.response.searchindent.SearchIndent
import com.android.hre.response.searchmaterialIndents.searchMaterial
import com.android.hre.response.searchpcndata.SearchPCNDataN
import com.android.hre.response.statementNew.NewStatment
import com.android.hre.response.statment.StatementListData
import com.android.hre.response.ticketreposnenewCreate.TicketReposneNewCreated
import com.android.hre.response.tickets.TicketList
import com.android.hre.response.ticketsearch.SearchTicket
import com.android.hre.response.transcationinfo.TranscationInfoDetails
import com.android.hre.response.vaults.VaultDetails
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
        @Field("password") password:String,
        @Field("device_id") device_id:String
    ) :Call<Any>

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
       @Field("rejected") rejected:String,
       @Field("comment") comment:String
   ) :Call<CountList>

/*
   @FormUrlEncoded
   @POST("get-indents")
   fun getIndents(
       @Field("user_id") user_id :String
   ) :Call<GetIndentsHome>
*/

    @FormUrlEncoded
    @POST("get-indents")
    fun getIndents(
        @Field("user_id") user_id :String
    ) :Call<NewIndents>


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
        @Part file : List<MultipartBody.Part>,
       // @Part file: MultipartBody.Part
    ): Call<TicketReposneNewCreated>

    @Multipart
    @POST("update-ticket")
    fun updateticket(
        @Part("user_id") userId:RequestBody,
        @Part("subject") subject:RequestBody,
        @Part("issue") issue:RequestBody,
        @Part("ticket_no") ticket_no:RequestBody,
        @Part("priority") priority:RequestBody,
    ) :Call<TicketCreated>

    @Multipart
    @POST("update-ticket")
    fun updateticketwithimage(
        @Part("user_id") userId:RequestBody,
        @Part("subject") subject:RequestBody,
        @Part("issue") issue:RequestBody,
        @Part("ticket_no") ticket_no:RequestBody,
        @Part("priority") priority:RequestBody,
        @Part file: MultipartBody.Part
    ) :Call<TicketCreated>
    @Multipart
    @POST("upload-pettycash_bill")
    fun uploadPettycashBill(
        @Part("user_id") userId:RequestBody,
        @Part("bill_number") bill_number:RequestBody,
        @Part("spent_amount")  spent_amount:RequestBody,
        @Part("comments") comment:RequestBody,
        @Part("bill_date") bill_date:RequestBody,
        @Part("purpose") purpose:RequestBody,
        @Part("pcn") pcn:RequestBody,
        @Part file : List<MultipartBody.Part>,
       // @Part file: MultipartBody.Part
    ) :Call<TicketCreated>

    @Multipart
    @POST("add-conversation")
    fun addConversationForTicket(
        @Part("ticket_id") ticket_id:RequestBody,
        @Part("ticket_no") ticket_no: RequestBody,
        @Part("message") message :RequestBody,
        @Part("user_id") user_id:RequestBody,
        @Part("recipient") recipient:RequestBody
       // @Part file: MultipartBody.Part
    ) :Call<TicketCreated>

    @Multipart
    @POST("add-conversation")
    fun addConversationForTicketWithImage(
        @Part("ticket_id") ticket_id:RequestBody,
        @Part("ticket_no") ticket_no: RequestBody,
        @Part("message") message :RequestBody,
        @Part("user_id") user_id:RequestBody,
        @Part("recipient") recipient:RequestBody,
        @Part file: MultipartBody.Part
    ) :Call<TicketCreated>

   @FormUrlEncoded
   @POST("get-employees")
    fun getEmployee(
        @Field("user_id") user_id :String
    ) : Call<EmployeeList>

    @FormUrlEncoded
    @POST("get-mypettycash")
    fun getpettycashDetails(
        @Field("user_id") user_id:String
    ) :Call<PettyCashFirstScreen>

    @FormUrlEncoded
    @POST("get-pettycash_details")
     fun getPettyCashapproval(
        @Field("user_id") user_id:String,
        @Field("pettycash_id") pettycash_id :String
     ) :Call<AprrovalPettyCash>

    @FormUrlEncoded
    @POST("get-tickets")
    fun getTickets(
        @Field("user_id") user_id :String
    )  :Call<TikcetlistNew>


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
        @Field("lattitude") lattitude:Double,
        @Field("longitude") longitude:Double,
        @Field("address") address:String
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

    @FormUrlEncoded
    @POST("get-pettycash_summary")
    fun getStatmnet(
        @Field("user_id") user_id:String,
        @Field("from_date") start_date:String,
        @Field("to_date") end_date:String
    ) :Call<StatementListData>


    @FormUrlEncoded
    @POST("get-pettycash_bills-summary")
    fun getStatmnetNew(
        @Field("user_id") user_id:String,
        @Field("from_date") start_date:String,
        @Field("to_date") end_date:String
    ) :Call<NewStatment>

    @FormUrlEncoded
    @POST("vault")
    fun getVault(
        @Field("user_id") user_id:String
    ) : Call<VaultDetails>

    @FormUrlEncoded
    @POST("get-app-data")
    fun getappData(
        @Field("user_id") user_id:String
    ) :Call<AppDetails>

    @FormUrlEncoded
    @POST("my-dashboard-details")
    fun getDashbaordDetais(
        @Field("user_id") user_id:String
    ) :Call<DashbardData>

   @Multipart
    @POST("update-ticket-status")
    fun CompletTicket(
        @Part("ticket_id") ticket_id:RequestBody,
        @Part("ticket_no") ticket_no: RequestBody,
        @Part("message") message :RequestBody,
        @Part("user_id") user_id:RequestBody,
        @Part("action") recipient:RequestBody,
      //  @Part file: MultipartBody.Part
        @Part file : List<MultipartBody.Part>
    ) :Call<Completelist>

    @Multipart
    @POST("update-ticket-status_2")
    fun CompletTicket2(
        @Part("ticket_id") ticket_id:RequestBody,
        @Part("ticket_no") ticket_no: RequestBody,
        @Part("message") message :RequestBody,
        @Part("user_id") user_id:RequestBody,
        @Part("action") recipient:RequestBody,
        //  @Part file: MultipartBody.Part
        @Part file : List<MultipartBody.Part>
    ) :Call<Completelist>

    @FormUrlEncoded
    @POST("get-pettycash_details")
    fun getTranscationInfo(
        @Field("user_id") user_id:String
    ) : Call<TranscationInfoDetails>

    @FormUrlEncoded
    @POST("search-indent")
    fun getindents(
        @Field("user_id") user_id:String,
        @Field("search") search:String,
        @Field("role") role:String
    )  :Call<NewIndents>

    @FormUrlEncoded
    @POST("search-grn")
    fun searchGRNlits(
        @Field("user_id") user_id:String,
        @Field("search") search:String
    )  :Call<GrnList>
    @FormUrlEncoded
    @POST("search-ticket")
    fun getticketsearch(
        @Field("user_id") user_id:String,
        @Field("search") search:String,
        @Field("role") role:String
    ) :Call<TikcetlistNew>

//    @Headers("Content-Type: application/json")
//    @POST("create-indent")
//     fun createIndent(@Body request: CreateIndentRequest): Call<IndentResponse>

    @FormUrlEncoded
    @POST("search-material")
    fun searchMaterialData(
        @Field("search") search:String
    ) : Call<searchMaterial>


    @FormUrlEncoded
    @POST("assign-ticket")
    fun assignTicket(
        @Field("user_id") user_id :String,
        @Field("ticket_id") ticket_id :String,
        @Field("status")  status:String,
        @Field("comment") comment:String,
        @Field("recipient") recipient:String,
        @Field("priority") priority:String,
        @Field("tat") tat:String
    ) :Call<ticketAssign>


    @FormUrlEncoded
    @POST("new-vault")
    fun newVault(
        @Field("user_id") user_id :String
        ) :Call<NewVaultDetiailsMainFolder>

    @FormUrlEncoded
    @POST("new-vault-level-1")
    fun newValut1(
        @Field("user_id") user_id :String,
        @Field("f1") f1 :String
    ) :Call<NewVaultDetiailsMainFolder>
    @FormUrlEncoded
    @POST("new-vault-level-2")
    fun newValut2(
        @Field("user_id") user_id :String,
        @Field("f1") f1 :String,
        @Field("f2") f2 :String
    ):Call<NewVaultDetiailsMainFolder>
    @FormUrlEncoded
    @POST("new-vault-level-3")
    fun newValut3(
        @Field("user_id") user_id :String,
        @Field("f1") f1 :String,
        @Field("f2") f2 :String,
    @Field("f3") f3 :String
    ):Call<NewVaultDetiailsMainFolder>
    @FormUrlEncoded
    @POST("new-vault-level-4")
    fun newValut4(
        @Field("user_id") user_id :String,
        @Field("f1") f1 :String,
        @Field("f2") f2 :String,
        @Field("f3") f3 :String,
        @Field("f4") f4 :String
    ):Call<NewVaultDetiailsMainFolder>
    @FormUrlEncoded
    @POST("new-vault-level-5")
    fun newValut5(
        @Field("user_id") user_id :String,
        @Field("f1") f1 :String,
        @Field("f2") f2 :String,
        @Field("f3") f3 :String,
        @Field("f4") f4 :String,
        @Field("f5") f5 :String
    ) :Call<NewVaultDetiailsMainFolder>


    @FormUrlEncoded
    @POST("search-pcn")
    fun searchPcn(
        @Field("user_id") user_id:String,
        @Field("search") search:String
    ) :Call<PCN>




    @Headers("Content-Type: application/json")
    @POST("create-indent")
    fun createIndent(@Body request: CreateIndentRequest): Call<NewRepsonse>
}


