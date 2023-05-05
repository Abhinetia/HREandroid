package com.android.hre.response.pcns

data class PCNNEWLIST(
    val `data`: List<DataX>,
    val message: String,
    val status: Int
)