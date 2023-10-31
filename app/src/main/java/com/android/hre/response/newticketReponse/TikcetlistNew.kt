package com.android.hre.response.newticketReponse

data class TikcetlistNew(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        val counts: List<Count>,
        val tickets: List<Ticket>
    )
    data class Count(
        val Active: Int,
        val Completed: Int,
        val Resolved: Int
    )

    data class Ticket(
        val category: String,
        val created_on: String,
        val filename: List<String>,
        val filepath: String,
        val message: String,
        val pcn: String,
        val pcn_detail :String,
        val priority: String,
        var status: String,
        var comments :String,
        val ticket_creator: String,
        val ticket_id: Int,
        val ticket_no: String
    )
}