package com.android.hre.response.tickets

data class TicketList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val created_on: String,
        val message: String,
        val pcn: String,
        val status: String,
        val category: String,
        val ticket_id: String,
        val ticket_no: String
    )
}
