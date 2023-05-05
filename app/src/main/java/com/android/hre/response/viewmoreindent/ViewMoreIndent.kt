package com.android.hre.response.viewmoreindent

data class ViewMoreIndent(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val created_on: String,
        val decription: String,
        val indent_id: String,
        val last_update: String,
        val material_brand: String,
        val material_id: String,
        val material_info: Map<String,String>,
        val material_name: String,
        val pending: String,
        val quantity: String,
        val recieved: String,
        val status: String
    )
}