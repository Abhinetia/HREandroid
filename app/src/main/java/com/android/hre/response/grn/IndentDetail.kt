package com.android.hre.response.grn

data class IndentDetail(
    val brand: String,
    val information: Information,
    val material_name: String,
    val quantity_pending: String,
    val quantity_raised: String,
    val quantity_received: String
)