package com.android.hre.response.homeresponse

data class DashbardData(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        val attendance: String,
        val grn_count: String,
        val indents_count: Int,
        val tickets_count: Int,
        val pettycash :String
    )
}