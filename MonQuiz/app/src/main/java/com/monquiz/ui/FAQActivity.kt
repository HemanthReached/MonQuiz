package com.monquiz.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.R
import com.monquiz.model.FaqModel
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class FAQActivity : BaseActivity(), View.OnClickListener {

    private var etFAQSearch: EditText? = null
    private var ivFAQClear: ImageView? = null
    private var recyclerViewFAQ: RecyclerView? = null
    private var rlFAQBack: ImageView? = null
    private var faqArray = ArrayList<FaqModel>()
    private lateinit var fAdapter : FAQAdapter
    private var faqWebView : WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.layout_faq)
        initializeUi()
        initializeListeners()
    }

    private fun initializeUi() {
        recyclerViewFAQ = findViewById(R.id.recyclerViewFAQ)
        ivFAQClear = findViewById(R.id.ivFAQClear)
        etFAQSearch = findViewById(R.id.etFAQSearch)
        rlFAQBack = findViewById(R.id.rlFAQBack)
        faqWebView = findViewById(R.id.faq_webview)
    }

    private fun initializeListeners() {
        ivFAQClear!!.setOnClickListener(this)
        rlFAQBack!!.setOnClickListener(this)

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

        faqWebView!!.settings.javaScriptEnabled = true
        faqWebView!!.settings.builtInZoomControls = true
        faqWebView!!.settings.displayZoomControls = false
        faqWebView!!.webViewClient = webclient
        faqWebView!!.webChromeClient = chromeClient
        faqWebView!!.loadUrl("https://monquiz.in/faq")


        /*fAdapter = FAQAdapter(this,faqArray)
        recyclerViewFAQ!!.layoutManager = LinearLayoutManager(this)
        recyclerViewFAQ?.adapter = fAdapter
        loadData()
        etFAQSearch!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (faqArray.isNotEmpty()) {
                        if (s.length >= 2) fAdapter.filter.filter(s)
                        else fAdapter.filter.filter("")
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })*/
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.rlFAQBack -> onBackPressed()
            R.id.ivFAQClear ->                /* if (faqAdapter != null)
                    faqAdapter.searchedText("");*/etFAQSearch!!.setText("")
            else -> {}
        }
    }

    private fun loadData(){
        try {
            val jObj = JSONObject(loadJSONFromAsset())
            if (jObj.length() > 0){
                val jArray = jObj.getJSONArray("FAQ's")
                if (jArray.length() > 0){
                    for (i in 0 until jArray.length()){
                        val jObj1 = jArray.getJSONObject(i)
                        faqArray.add(FaqModel(jObj1.getString("Heading"),"","",true))
                        val jArray1 = jObj1.getJSONArray("QAs")
                        if (jArray1.length() > 0){
                            for (j in 0 until jArray1.length()){
                                val jObj2 = jArray1.getJSONObject(j)
                                val model = FaqModel()
                                model.Heading = ""
                                model.isHeading = false
                                model.Question = jObj2.getString("Q")
                                model.Answer = jObj2.getString("A")
                                faqArray.add(model)
                            }
                        }
                    }
                    fAdapter?.notifyDataSetChanged()
                }
            }
        }catch (e : Exception){
            Log.e("dataLoading",e.toString())
        }
    }

    fun loadJSONFromAsset(): String {
        var json = ""
        json = try {
            val istream: InputStream = this.assets.open("faqdata.json")
            val size: Int = istream.available()
            val buffer = ByteArray(size)
            istream.read(buffer)
            istream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
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