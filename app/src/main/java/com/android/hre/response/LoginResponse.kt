package com.android.hre.response

data class  LoginResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)