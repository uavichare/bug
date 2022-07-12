package com.example.buglibrary.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.buglibrary.data.PoiLocation

class ListConverter {


    @TypeConverter
    fun fromLocation(value: String?): List<PoiLocation?>? {
        val listType =
            object : TypeToken<List<PoiLocation?>?>() {}.type
        return Gson().fromJson<List<PoiLocation?>>(value, listType)
        // return value == null ? null : new Date(value);
    }

    @TypeConverter
    fun arrayListToString(list: List<PoiLocation?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
        // return date == null ? null : date.getTime();
    }
}