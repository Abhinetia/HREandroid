package com.android.hre.response.statment

data class StatementListData(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val amount: String,
        val bill_submission_date: String,
        val comment: String,
        val mode: String,
        val ref: String,
        val transaction_date: String,
        val type: String
    )
}