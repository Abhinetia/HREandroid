package com.android.hre

import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.io.IOException
import java.util.*
import android.content.Context
import android.util.Log


class LocationHelper(private val context: Context) {


   /* fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText: String? = null
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                val sb = StringBuilder()
                if (address.maxAddressLineIndex >= 0) {
                    sb.append(address.getAddressLine(0))
                }
               // val sb = StringBuilder()
//                for (i in 0 until address.maxAddressLineIndex) {
//                    sb.append(address.getAddressLine(i)).append("\n")
//                    Log.v("location","$address")
//                }
                addressText = sb.toString()
                Log.v("location","$addressText")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressText
    }*/

    fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText: String? = null
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                addressText = formatAddress(address)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressText
    }

    private fun formatAddress(address: Address): String {
        val sb = StringBuilder()
        for (i in 0..address.maxAddressLineIndex) {
            sb.append(address.getAddressLine(i)).append("\n")
        }
        return sb.toString().trim()
    }

}