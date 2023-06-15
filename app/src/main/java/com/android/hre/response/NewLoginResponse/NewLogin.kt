package com.android.hre.response.NewLoginResponse

data class NewLogin(
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