package com.monquiz.ui

import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import com.monquiz.R
import android.view.inputmethod.EditorInfo
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import com.monquiz.utils.CommonMethods

class QueryActivity : BaseActivity(), View.OnClickListener {
    private var etQuery: EditText? = null
    private var ivQueryDialogClose: ImageView? = null
    private var btnSubmitQuery: TextView? = null
    private var reference = ""
    private val gameNumber = ""
    private val username = ""
    private val mobile = ""
    private val email = ""
    private var which: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_query)
        dataFromIntent
        initializeUi()
        initializeListeners()
    }

    private val dataFromIntent: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                which = bundle.getString(getString(R.string.query_intent_from))
                if (which != null) {
                    if (which == "DailyQuizSummaryActivity") {
                        CommonMethods.infoLog("intent from DailyQuiz summary ")
                      //  reference = getString(R.string.api_daily_quiz_user_ask_query)
                    } else {
                    }
                }
            } else {
                CommonMethods.infoLog("intent data is null")
            }
        }

    private fun initializeUi() {
        etQuery = findViewById(R.id.etQuery)
        etQuery!!.setImeOptions(EditorInfo.IME_ACTION_DONE)
        etQuery!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
        etQuery!!.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? -> false })
        ivQueryDialogClose = findViewById(R.id.ivQueryDialogClose)
        btnSubmitQuery = findViewById(R.id.btnSubmitQuery)
    }

    private fun initializeListeners() {
        ivQueryDialogClose!!.setOnClickListener(this)
        btnSubmitQuery!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.ivQueryDialogClose -> finish()
            R.id.btnSubmitQuery -> if (which.equals(
                    "DailyQuizSummaryActivity",
                    ignoreCase = true
                )
            ) /*sendQuery()*/ /*else*/  // sendFeedBack();
               // break
            else -> {
            }
        }
    }

   /* private fun sendQuery() {
        val queryRef = String.format(
            reference, FirebaseAuth.getInstance().currentUser!!
                .uid,
            PreferenceConnector.readString(this, getString(R.string.current_game_id), "")
        )
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(queryRef)
        val queryText = etQuery!!.text.toString()
        CommonMethods.infoLog("query text: $queryText")
        val submitQuery = SubmitQuery(queryText, gameNumber)
        databaseReference.setValue(submitQuery).addOnSuccessListener { aVoid ->
            CommonMethods.infoLog("query sent ")
            etQuery!!.setText("")
            finish()
        }
            .addOnFailureListener { e -> CommonMethods.errorLog("exception while query sent: " + e.toString()) }
    }*/
}