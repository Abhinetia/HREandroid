package com.android.hre.response.pcns

data class PCN(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    class Data(
        val pcn: String
    )
}