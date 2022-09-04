package com.monquiz.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.monquiz.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {

    fun hideKeyboard(context: Activity, view: View) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateFormat(date: String, currentFormat: String, requiredFormat: String): String {
        val formatter = SimpleDateFormat(currentFormat)
        val newDate = formatter.parse(date)
        val desiredFormat = SimpleDateFormat(requiredFormat).format(newDate)

        return desiredFormat.toString()
    }

    fun getFirebaseToken() : String{
        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            token = task.result!!
            Log.e("Token ",task.result!!)
        })
        return token
    }

    fun toTitleCase(str: String?): String? {
        if (str == null) { return null }
        var space = true
        val builder = StringBuilder(str)
        val len = builder.length
        for (i in 0 until len) {
            val c = builder[i]
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c))
                    space = false
                }
            } else if (Character.isWhitespace(c)) {
                space = true
            } else {
                builder.setCharAt(i, Character.toLowerCase(c))
            }
        }
        return builder.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun checkCurrentMonth(time: String): Boolean {
        val pattern = "MMM yyyy"
        try {
            val ctime = convertDateFormat(Calendar.getInstance().time.toString(),
                "EEE MMM dd HH:mm:ss zzz yyyy", "MMM yyyy")
            val sdf = SimpleDateFormat(pattern)
            val time1 = sdf.parse(time)
            val time2 = sdf.parse(ctime)
            return time1.equals(time2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun setStarSpan(view : TextView, context: Context){
        val string = view.text
        val spannable = SpannableString(string)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)),
            string.length-1, string.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = spannable
    }

}