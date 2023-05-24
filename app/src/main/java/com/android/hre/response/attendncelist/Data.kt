package com.android.hre.response.attendncelist

data class Data(
    val date: String,
    val login: String,
    val login_location: Any,
    val logout: String,
    val logout_location: Any,
    val working_minutes: String
)