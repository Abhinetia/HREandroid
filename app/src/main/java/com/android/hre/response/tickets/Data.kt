package com.android.hre.response.tickets

data class Data(
    val created_on: String,
    val message: String,
    val pcn: String,
    val status: String,
    val subject: String,
    val ticket_id: Int,
    val ticket_no: String
)