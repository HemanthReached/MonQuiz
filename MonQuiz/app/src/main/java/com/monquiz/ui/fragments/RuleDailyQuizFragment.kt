package com.monquiz.ui.fragments

import android.widget.TextView
import android.content.res.AssetManager
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.monquiz.R
import android.widget.Toast
import android.graphics.text.LineBreaker
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.monquiz.utils.UnicodeReader
import es.dmoral.toasty.Toasty
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder

class RuleDailyQuizFragment : BaseFragment {
    private var tvRuleBookText: TextView? = null
    private val text = StringBuilder()
    private var ims: InputStream? = null
    private var assetManager: AssetManager? = null
    private var type: String? = ""

    constructor() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    constructor(context: Context?, type: String?) {
       // this.context = context
        this.type = type
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.layout_fragment_rule_book, container, false)
        tvRuleBookText = view.findViewById(R.id.tvRuleBookText)
        assetManager = requireActivity().assets
        if (type != null) {
            try {
                ims = assetManager!!.open("rules_daily_quiz.txt")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            BufferedReader(UnicodeReader(ims, "UTF-8")).use { reader ->
                // do reading, usually loop until end of file reading
                var mLine: String?
                while (reader.readLine().also { mLine = it } != null) {
                    text.append(mLine)
                    text.append('\n')
                }
            }
        } catch (e: IOException) {
            Toasty.error(requireContext(), "Error reading file!", Toasty.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            //log the exception
            tvRuleBookText!!.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            tvRuleBookText!!.text = text
        }
        return view
    }
}