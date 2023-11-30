package com.android.hre.response.statementNew

data class NewStatment(
    val `data`: Data,
    val message: String,
    val status: Int
){
    data class Data(
        val closing: Int,
        val opening: Int,
        val summary: List<Summary>
    )
    data class Summary(
        val amount: String,
        val bill_submission_date: String,
        val comment: String,
        val mode: String,
        val ref: String,
        val transaction_date: String,
        val type: String
    )
}