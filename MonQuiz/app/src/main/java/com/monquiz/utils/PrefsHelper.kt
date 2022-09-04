package com.monquiz.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class PrefsHelper {

    private var context: Context = OwlizApp.getInstance()._context!!
    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor

    private var sharedPreferencesRemember: SharedPreferences
    private var editorR: SharedPreferences.Editor

    val prefsFileRemember = OwlizApp.getInstance()._context!!.packageName + "rem"
    val prefsFile = OwlizApp.getInstance()._context!!.packageName

    init {

        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        /*rembember me funcnality*/

        sharedPreferencesRemember = context.getSharedPreferences(prefsFileRemember, Context.MODE_PRIVATE)
        // sharedPreferencesR = PreferenceManager.getDefaultSharedPreferences(context)
        editorR = sharedPreferencesRemember.edit()
    }


    companion object {
        @Volatile
        private var INSTANCE: PrefsHelper? = null

    }

    val instance: PrefsHelper by lazy {
        INSTANCE ?: synchronized(this) {
            INSTANCE
                ?: PrefsHelper().also { INSTANCE }
        }
    }


    /*
   *  for deleting data according to key
   * */
    fun delete(key: String) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit()
        }
    }

    /*
    *  for save  data according to key  and value
    * */

    fun savePref(key: String, value: Any?) {
        delete(key)
        if (value is Boolean) {
            editor.putBoolean(key, value)
        } else if (value is Int) {
            editor.putInt(key, value)
        } else if (value is Float) {
            editor.putFloat(key, value)
        } else if (value is Long) {
            editor.putLong(key, value)
        } else if (value is String) {
            editor.putString(key, value as String?)
        } else if (value is Enum<*>) {
            editor.putString(key, value.toString())
        } else if (value != null) {
            throw RuntimeException("Attempting to save non-primitive preference")
        }
        editor.commit()
    }

    /*
    *  for save  data according to key  and value for rembember me
    * */

    fun saveRembemberPref(key: String, value: Any?) {
        delete(key)
        if (value is String) {
            editorR.putString(key, value as String?)
        } else if (value is Boolean) {
            editorR.putBoolean(key, value as Boolean)
        } else if (value != null) {
            throw RuntimeException("Attempting to save non-primitive preference")
        }
        editorR.commit()
    }

    /*
   *  for get all key data
   * */
    fun <T> getPref(key: String): T {
        return sharedPreferences.all[key] as T
    }

    /*
     *  for get all key data for rembember me
     * */
    fun <T> getRembemberPref(key: String, defValue: T): T {
        val returnValue = sharedPreferencesRemember.all[key] as T
        return returnValue ?: defValue
    }

    /*
   *  for get value according to key
   * */
    fun <T> getPref(key: String, defValue: T): T {
        val returnValue = sharedPreferences.all[key] as T
        return returnValue ?: defValue
    }

    /*
   *  for checking key is exists or not
   * */
    fun isPrefExists(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    /*
   *  for checking key is exists or not for rembember me
   * */
    fun isPrefExistsRembembar(key: String): Boolean {
        return sharedPreferencesRemember.contains(key)
    }


    /*
   *  for clear all data from local
   * */
    fun clearAllPref() {
        editor.clear()
        editor.commit()
    }
}