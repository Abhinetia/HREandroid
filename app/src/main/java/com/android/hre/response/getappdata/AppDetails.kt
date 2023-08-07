package com.android.hre.response.getappdata

data class AppDetails(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        val app_version: String,
        val isloggedin: String,
        val need_update: String
    )
}