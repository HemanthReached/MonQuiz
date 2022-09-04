package com.monquiz.utils

import android.content.Context
import android.content.SharedPreferences


object FirebaseToken {

    val ACCESS_TOKEN = "FireBaseToken"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun putKey(context: Context, Value: String?) {
        sharedPreferences = context.getSharedPreferences(ACCESS_TOKEN, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(ACCESS_TOKEN, Value)
        editor.apply()
    }

    fun getKey(context: Context): String {
        sharedPreferences = context.getSharedPreferences(ACCESS_TOKEN, Context.MODE_PRIVATE)
        return sharedPreferences.getString(ACCESS_TOKEN, "")!!
    }

    fun clearSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(ACCESS_TOKEN, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}