package com.android.hre.response.ApprovalPettyCash

data class AprrovalPettyCash(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val comments: String,
        val date: String,
        val filename: String,
        val filepath: String,
        val isapproved: String,
        val spent_amount: String
    )
}