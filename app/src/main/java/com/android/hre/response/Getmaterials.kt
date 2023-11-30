package com.android.hre.response

data class Getmaterials(
    val `data`: List<DataX>,
    val status: Int
) {
    data class DataX(
        var brand: String,
      //  val information: Information,
        var material_id: String,
        var name: String,
        var uom: String,
        val information: Map<String,String>
    )

}