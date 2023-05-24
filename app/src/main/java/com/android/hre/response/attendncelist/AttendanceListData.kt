package com.android.hre.response.attendncelist

data class AttendanceListData(
    val `data`: List<Data>,
    val message: String,
    val status: Int,
    val total_minute: Int
) {
    data class Data(
        val date: String,
        val login: String,
        val login_location: Any,
        val logout: String,
        val logout_location: Any,
        val working_minutes: String
    )
}