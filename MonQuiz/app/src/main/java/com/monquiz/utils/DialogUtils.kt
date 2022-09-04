package com.monquiz.utils

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.monquiz.R
import android.widget.TextView
import android.content.DialogInterface
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.Window
import com.monquiz.BuildConfig
import com.monquiz.ui.BaseActivity.Companion.hideStatusBarForDialog
import java.lang.Exception
import java.util.logging.Level
import java.util.logging.Logger

class DialogUtils {
    var dialog: Dialog? = null
    private var initialState = true
    var handlerBlink: Handler? = null
    var runnableBlink: Runnable? = null

    /**
     * to create and return the dialog
     *
     * @param context context as parameter
     * @param message title of dialog
     * @return dialog object
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun progressDialog(context: Context, message: String): Dialog? {
        dialog = null
        try {
            dialog = Dialog(context)
            if (dialog!!.window != null) {
                dialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                if (BuildConfig.PRO_VERSION) dialog!!.window!!
                    .setDimAmount(0.85f) else dialog!!.window!!.setDimAmount(0.65f)
                dialog!!.setContentView(R.layout.layout_progress_dialog)
                // dialog.setCancelable(false);
                val dialogParent = dialog!!.findViewById<View>(R.id.dialogParent)
                dialogParent.setOnClickListener { v: View? -> dialog!!.dismiss() }
                val titleTV = dialog!!.findViewById<TextView>(R.id.progressMessage)
                blinkDot(titleTV, context, message)
                dialog!!.show()
                dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                hideStatusBarForDialog(dialog!!)
                dialog!!.setOnDismissListener { dialog: DialogInterface? ->
                    handlerBlink!!.removeCallbacks(runnableBlink!!)
                }
            }
        } catch (e: Exception) {
            CommonMethods.errorLog("error prog dial:$e")
            Logger.getLogger(DialogUtils::class.java.name).log(Level.SEVERE, null, e)
        }
        return dialog
    }

    fun blinkDot(titleTV: TextView, context: Context, message: String) {
        handlerBlink = Handler()
        runnableBlink = Runnable {
            doTask(titleTV, context, message)
            blinkDot(titleTV, context, message)
        }
        handlerBlink!!.postDelayed(runnableBlink!!, 500)
    }

    fun doTask(titleTV: TextView, context: Context, message: String) {
        if (initialState) {
            // Reverse the boolean
            initialState = false
            // Change the TextView color to initial State
            val spannableColorString1 = SpannableString(message)
            spannableColorString1.setSpan(ForegroundColorSpan(Color.TRANSPARENT),
                message.length - 1, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            titleTV.text = spannableColorString1
        } else {
            // Reverse the boolean
            initialState = true
            // Change the TextView color to initial State
            val spannableColorString1 = SpannableString(message)
            spannableColorString1.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.dark_orange)),
                message.length - 1, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            titleTV.text = spannableColorString1
        }
    }

    companion object {
        private var dialogUtils: DialogUtils? = null
        val dialogUtilsInstance: DialogUtils?
            get() {
                if (dialogUtils == null) { dialogUtils = DialogUtils() }
                return dialogUtils
            }

        fun setDialogAttributes(dialog: Dialog) {
            try {
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
                dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                dialog.window!!.setDimAmount(0.80f)
            } catch (e: Exception) {
                CommonMethods.errorLog("setDialogAttributes excep:$e")
            }
        }

        fun removeDialogAttributes(dialog: Dialog) {
            try {
                dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            } catch (e: Exception) {
                CommonMethods.errorLog("removeDialAttributes excep:$e")
            }
        }
    }
}