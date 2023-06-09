package com.android.hre.response

data class  LoginResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val employee_id: String,
        val role: String,
        val role_name: String,
        val user_id: String,
        val username: String
    )
}