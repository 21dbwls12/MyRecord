package com.example.editmyrecord

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UriListConverter {
    @TypeConverter
    fun fromUriList(uris: List<Uri>): String {
//        if (uris.isNullOrEmpty()) {
//            return null
//        }
        val gson = Gson()
        return  gson.toJson(uris)
//        return uris.map { it?.toString() }
    }

    @TypeConverter
    fun toUrisList(json: String?): List<Uri> {
//        if (json == null) {
//            return null
//        }
        val gson = Gson()
        val type = object  : TypeToken<List<Uri>>() {}.type
        return gson.fromJson(json, type)
//        val type = object  : TypeToken<List<String>>() {}.type
//        val stringList = gson.fromJson<List<String>>(json, type)
//        return stringList.map { Uri.parse(it) }
    }
}