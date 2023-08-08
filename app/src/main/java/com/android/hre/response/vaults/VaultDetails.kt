package com.android.hre.response.vaults

data class VaultDetails(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val filename: String,
        val filepath: String,
        val name: String,
        val type: String
    )
}