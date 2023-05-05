package com.android.hre.response.homeindents

data class GetIndentsHome(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val created_on: String,
        val indent_id: Int,
        val indent_no: String,
        val pcn: String,
        val status: String
    )
}