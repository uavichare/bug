package com.example.buglibrary.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.buglibrary.data.PoiLocation
import com.example.buglibrary.data.PoiMedia

class Converter {
    @TypeConverter
    fun fromPoi(value: String?): ArrayList<String>? {
        val listType = object : TypeToken<ArrayList<String>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToString(list: ArrayList<String>?): String {
        return Gson().toJson(list)
    }

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

    @TypeConverter
    fun fromPoiMedia(value: String?): List<PoiMedia?>? {
        val listType =
            object : TypeToken<List<PoiMedia?>?>() {}.type
        return Gson().fromJson<List<PoiMedia?>>(value, listType)
        // return value == null ? null : new Date(value);
    }

    @TypeConverter
    fun mediaListToString(list: List<PoiMedia?>?): String? {
        return Gson().toJson(list)
        // return date == null ? null : date.getTime();
    }
}