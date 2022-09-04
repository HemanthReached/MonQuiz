package com.monquiz.db.daos.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.monquiz.response.superover.join.Option

class Deliverytypeconverter {
    @TypeConverter
    fun listToJson(value: List<Option>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Option>::class.java).toList()
}