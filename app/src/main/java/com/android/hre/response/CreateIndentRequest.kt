package com.android.hre.response

import com.google.gson.annotations.SerializedName

data class CreateIndentRequest(
    @SerializedName("user_id") val userId: String,
    val pcn: String,
    val indents: List<Indent>
)

data class Indent(
    @SerializedName("material_id") val materialId: String,
    val description: String,
    val quantity: String
)
