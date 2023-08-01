package com.android.hre.response.newticketReponse

data class Ticket(
    val category: String,
    val created_on: String,
    val filename: List<String>,
    val filepath: String,
    val message: String,
    val pcn: String,
    val priority: String,
    val status: String,
    val ticket_creator: String,
    val ticket_id: Int,
    val ticket_no: String
)