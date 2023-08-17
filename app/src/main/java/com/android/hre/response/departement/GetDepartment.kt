package com.android.hre.response.departement

data class GetDepartment(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val category: String,
        val department :String
    )
}