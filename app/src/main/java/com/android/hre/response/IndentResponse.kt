package com.android.hre.response

import com.google.gson.annotations.SerializedName

data class IndentResponse(

val status: Int,
val message: String,
//val inputs: Inputs
)

data class Inputs(
    val user_id: String?,
    val pcn: String,
    val indents: List<Indents>
)

data class Indents(
    val material_id: String,
    val description: String,
    val quantity: String
)
