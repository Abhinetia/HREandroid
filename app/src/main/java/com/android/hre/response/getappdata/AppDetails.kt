package com.android.hre.response.getappdata

data class AppDetails(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        var app_version: String,
        val isloggedin: String,
        var need_update: String
    )
}