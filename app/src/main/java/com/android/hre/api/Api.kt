package com.android.hre.api

import com.android.hre.models.Intends
import com.android.hre.response.*
import com.android.hre.response.countupdate.CountList
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.creatindent.SaveIndentResponse
import com.android.hre.response.grn.GrnList
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.response.listmaterial.ListMaterials
import com.android.hre.response.pcns.PCN
import com.android.hre.response.viewmoreindent.ViewMoreIndent
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
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
        @Part("indent_no") indentNo: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("issue") issue: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("recipient") recipient: RequestBody
    ): Call<TicketCreated>


    @Headers("Content-Type: application/json")
    @POST("create-indent")
     fun createIndent(@Body request: CreateIndentRequest): Call<IndentResponse>
}


