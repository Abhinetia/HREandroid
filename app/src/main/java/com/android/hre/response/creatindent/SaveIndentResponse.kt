package com.android.hre.response.creatindent

import org.json.JSONObject

data class SaveIndentResponse(
    val inputs: JSONObject,
    val message: String,
    val status: Int
)