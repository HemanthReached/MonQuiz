package com.monquiz.ui

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.widget.TextView
import android.widget.RelativeLayout
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.monquiz.R
import android.widget.Toast
import com.monquiz.utils.UnicodeReader
import es.dmoral.toasty.Toasty
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder

class ShowTextDocumentActivity : BaseActivity(), View.OnClickListener {

    private var key: String? = ""
    private val text = StringBuilder()
    private var ims: InputStream? = null
    private var assetManager: AssetManager? = null

    private var tvshowDocumentTitle: TextView? = null
    private var rlshowDocumentBack: ImageView? = null
    private var tandpWebView : WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.layout_show_text_document)
        assetManager = assets
        val b = intent.extras!!
        key = b.getString("keyTermsAndCondition")
        initUi()
       // readFile()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUi() {
       // tvshowDocumentText = findViewById(R.id.tvshowDocumentText)
        tandpWebView = findViewById(R.id.termswebview)
        tvshowDocumentTitle = findViewById(R.id.tvshowDocumentTitle)
        
        val webclient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) { view!!.loadUrl(url) }
                return true
            }
        }
        val chromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }

        tandpWebView!!.settings.javaScriptEnabled = true
        tandpWebView!!.settings.builtInZoomControls = true
        tandpWebView!!.settings.displayZoomControls = false
        tandpWebView!!.webViewClient = webclient
        tandpWebView!!.webChromeClient = chromeClient

        if (key.equals("terms", ignoreCase = true)) {
            tvshowDocumentTitle!!.text = "Terms of Use"
            tandpWebView!!.loadUrl("https://monquiz.in/terms-of-use")
        } else {
            tvshowDocumentTitle!!.text = "Privacy Policy"
            tandpWebView!!.loadUrl("https://monquiz.in/privacy-policy")
        }
        rlshowDocumentBack = findViewById(R.id.rlshowDocumentBack)
        rlshowDocumentBack!!.setOnClickListener(this)
    }

    private fun readFile() {
        if (key != null) {
            if (key.equals("terms", ignoreCase = true)) {
                try {
                    ims = assetManager!!.open("terms.txt")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    ims = assetManager!!.open("policy.txt")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
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
            Toasty.error(applicationContext, "Error reading file!", Toasty.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            //log the exception
          //  tvshowDocumentText!!.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD!!
           // tvshowDocumentText!!.text = text
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.rlshowDocumentBack) {
            finish()
        }
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