package com.android.hre.response.pettycashDetails

data class PettyCashDetails(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val date: String,
        val pettycash_id :String,
        val purpose: String,
        val remaining_cash: String,
        val spended_cash: String,
        val total_amount: String
    )
}