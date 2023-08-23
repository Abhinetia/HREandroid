package com.android.hre.response.ticketreposnenewCreate

data class TicketReposneNewCreated(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        val ticket_no: String
    )
}