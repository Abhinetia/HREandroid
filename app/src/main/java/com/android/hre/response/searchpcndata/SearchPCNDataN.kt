package com.android.hre.response.searchpcndata

data class SearchPCNDataN(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val area: String,
        val brand: String,
        val city: String,
        val client_name: String,
        val location: String,
        val pcn: String,
        val pincode: String,
        val state: String,
        val status: String
    )
}