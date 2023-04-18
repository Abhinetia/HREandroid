package com.android.hre.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//class Intends(s: String, s1: String, s2: String) {
//    lateinit var material_id: String
//    lateinit var description:String
//    lateinit var quantity :String
//}

@Parcelize
data class Intends(val material_id: String, val description: String,val quantity: String) : Parcelable
