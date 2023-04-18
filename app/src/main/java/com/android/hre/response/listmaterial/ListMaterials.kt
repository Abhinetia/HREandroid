package com.android.hre.response.listmaterial

data class ListMaterials(
    val `data`: List<Data>,
    val message: String,
    val status: Int

){
    class Data
        (val name:String) {

        }
}