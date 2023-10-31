package com.android.hre.response.grn

data class GrnList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val dispatched: String,
        val grn: String,
        val indent_details: List<IndentDetail>,
        val indent_no: String,
        val pcn: String,
        val pcn_detail :String,
        val dispatch_comment :String,
        val accepting_comment :String,
        val status :String
    )
    data class IndentDetail(
        val brand: String,
     //   val information: Information,
        val material_name: String,
        val quantity_pending: String,
        val quantity_raised: String,
        val quantity_received: String,

        val information: Map<String,String>
    )
}
