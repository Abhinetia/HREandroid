package com.android.hre.response.getconve

data class Conversation(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val date: String,
        val filename: List<String>,
        val filepath: String,
        val message: String,
        val recipient: String,
        val sender_id:String,
        val recipient_id:String,
        val sender: String
    )
}