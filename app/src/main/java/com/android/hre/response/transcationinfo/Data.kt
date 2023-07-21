package com.android.hre.response.transcationinfo

data class Data(
    val bill_date: String,
    val bill_submission_date: String,
    val comments: String,
    val filename: List<String>,
    val filepath: String,
    val isapproved: String,
    val pcn: String,
    val purpose: String,
    val remarks: String,
    val utilised_amount: String
)