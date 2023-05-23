package com.android.hre.response.employee

data class EmployeeList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val name: String,
        val recipient: Int,
        val role: String
    )
}