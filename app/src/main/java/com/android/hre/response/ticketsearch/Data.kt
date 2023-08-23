package com.android.hre.response.ticketsearch

data class Data(
    val category: String,
    val created_on: String,
    val creator_emplid: String,
    val creator_name: String,
    val creator_role: String,
    val filename: List<String>,
    val filepath: String,
    val message: String,
    val pcn: String,
    val pcn_detail: String,
    val priority: String,
    val status: String,
    val ticket_creator: String,
    val ticket_id: Int,
    val ticket_no: String
)