package com.android.hre.response

data class NewRepsonse(
    val `data`: DataXX,
    val message: String,
    val status: Int
) {
    data class DataXX(
        val indent_no: String,
        val pcn: String,
        val pcn_details: String
    )
}