package com.android.hre.response.pcns

data class PCN(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    class Data(
        val area: String,
        val city: String,
        val client_name: String,
        val pcn: String,
        val state: String,
        val typeof_work: String
    )
}