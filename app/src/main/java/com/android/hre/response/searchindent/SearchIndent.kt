package com.android.hre.response.searchindent

data class SearchIndent(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val created_on: String,
        val indent_id: Int,
        val indent_no: String,
        val pcn: String,
        val pcn_detail: String,
        val status: String
    )
}