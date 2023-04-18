package com.android.hre.response

data class Getmaterials(
    val `data`: List<DataX>,
    val status: Int
) {
    data class DataX(
        val brand: String,
      //  val information: Information,
        val material_id: String,
        val name: String,
        val uom: String,
        val information: Map<String,String>
    )

}