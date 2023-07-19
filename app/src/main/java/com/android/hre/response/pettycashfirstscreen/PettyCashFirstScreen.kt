package com.android.hre.response.pettycashfirstscreen

data class PettyCashFirstScreen(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val balance_amount: String,
        val issued_amount: String
    )
}