package com.android.hre.api

import com.android.hre.models.Intends
import com.android.hre.response.CreateIntendObject
import com.android.hre.response.Getmaterials
import com.android.hre.response.LoginResponse
import com.android.hre.response.creatindent.SaveIndentResponse
import com.android.hre.response.listmaterial.ListMaterials
import com.android.hre.response.pcns.PCN
import com.google.gson.JsonObject
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

//    @FormUrlEncoded
//    @POST("create-indent")
//    fun sendReq(
//        @Field("material_id") material_id:String,
//        @Field("description") description:String,
//        @Field("quantity") quantity:String,
//
//
//        @Field("user_id") user_id: String,
//        @Field("pcn") pcn:String,
//
//        @Field("quantity") quantity:String,
//        @Field("description") description:String,
//    ) : Call<SaveResponse>  //@Body requestModel: RequestModel

    @FormUrlEncoded
    @POST("pcns")
    fun getAllPcns(
       @Field("user_id") user_id: String
    ) : Call<PCN>


//    fun sendReq(jsonArray: JSONArray, s: String, s1: String): Any {
//     @Field("indents")  learning_objective_uuids :ArrayList<String>,
//    @Field("user_id") userId :String,
//    @Field("pcn") pcn: String,
//
//    }


//        @FormUrlEncoded
//    @POST("create-indent")


//    @FormUrlEncoded
//    @POST("create-intend")
//    fun createIntend(@Body myJsonObject: CreateIntendObject): Call<SaveIndentResponse>
//

    fun sendReq(
        @Field("user_id") s: String,
        @Field("pcn") pcn: String,
        @Field("indents") indents: List<Intends>): Call<SaveIndentResponse>

//        @FormUrlEncoded
//    @POST("create-indent")
//    fun sendReq(
//        @Field("user_id") user_id: String,
//        @Field("pcn") pcn: String,
//        @Field("indents") indents: List<Intends>
//    ): Call<SaveIndentResponse>



//    @FormUrlEncoded
//    @POST("create-indent")
//    fun sendReq(@FieldMap ): Call<SaveResponse>

//    @POST("create-indent")
//    Call<SaveResponse> sendRequest(@Body RequestBody json )

//    @FormUrlEncoded
//    @POST("create-indent")
//    fun sendReq(
//        @Field("user_id") userid: String,
//        @Field("pcn") pcn: String,
//        @Field("indents") obj: JSONArray
//    ): Call<SaveIndentResponse>
//
    
}
