package com.monquiz.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.monquiz.R

class RuleBookActivity : BaseActivity(), View.OnClickListener {
    private var rlRuleBookBack: ImageView? = null
    private var rlSuperOver : RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_rule_book)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        initializeUi()
    }

    private fun initializeUi() {
        rlRuleBookBack = findViewById(R.id.rlRuleBookBack)
        rlSuperOver = findViewById(R.id.rlSuperOver)
        rlRuleBookBack!!.setOnClickListener(this)
        rlSuperOver!!.setOnClickListener {
            startActivity(Intent(this,SuperOverRulesActivity::class.java))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {R.id.rlRuleBookBack -> { onBackPressed() } }
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }
}