package com.android.hre.response.grn

data class Data(
    val dispatched: String,
    val grn: String,
    val indent_details: List<IndentDetail>,
    val indent_no: String,
    val pcn: String
)