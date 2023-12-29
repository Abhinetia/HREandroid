package com.android.hre.response.newvault

data class NewVaultDetiailsMainFolder(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        val `data`: List<DataX>,
        val folders: List<String>
    )
    data class DataX(
        val created_at: String,
        val deleted_at: Any,
        val filename: String,
        val folder: String,
        val id: Int,
        val name: String,
        val sub_folders: String,
        val type: String,
        val updated_at: String
    )
}