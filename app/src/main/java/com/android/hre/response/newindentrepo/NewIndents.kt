package com.android.hre.response.newindentrepo

data class NewIndents(
    val `data`: Data,
    val message: String,
    val status: Int
){
    data class Data(
        val counts: Counts,
        val myindents: List<Myindent>
    )
    data class Counts(
        val Active: Int,
        val Completed: Int
    )
    data class Myindent(
        val created_on: String,
        val indent_id: Int,
        val indent_no: String,
        val pcn: String,
        val pcn_detail: String,
        val status: String
    )
}