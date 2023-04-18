package com.android.hre.db

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "userinfo")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "materialname") val materialname: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "description") val description :String,
    @ColumnInfo(name = "quantity") val quantity: String
)

data class Description(val qty: String)


class HobbiesTypeConverter {

    @TypeConverter
    fun listToJson(value: List<Description>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Description>::class.java).toList()
}



