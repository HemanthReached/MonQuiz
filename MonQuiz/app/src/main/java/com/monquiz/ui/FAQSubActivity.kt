package com.monquiz.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.R
import org.jetbrains.annotations.Nullable
import java.util.*

class FAQSubActivity : BaseActivity(){
    private var rlFAQSubBack: RelativeLayout? = null
    private var tvFAQSubItemTitle: TextView? = null
    private var etFAQSubSearch: EditText? = null
    private var ivFAQSubClear: ImageView? = null
    private var recyclerViewFAQSub: RecyclerView? = null
  //  private var faqSubAdapter: FAQSubAdapter? = null
    private var faqTopicName = ""
   // private var contentsArray: Array<Content>
    //private var contentList: LinkedList<Content>? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_faq_sub)
     //   dataFromIntent
       // initializeUi()
      //  initializeListeners()
      //  initializeAdapter()
    }

/*    private val dataFromIntent: Unit
        private get() {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                faqTopicName = bundle.getString(getString(R.string.faq_topic_name))
                val parcelables: Array<Parcelable>? =
                    intent.getParcelableArrayExtra(getString(R.string.faq_content))
                contentsArray =
                    Arrays.copyOf(parcelables, parcelables!!.size, Array<Content>::class.java)
                contentList = LinkedList<E>(Arrays.asList<Array<Content>>(*contentsArray))
            }
        }

    @SuppressLint("SetTextI18n")
    private fun initializeUi() {
        tvFAQSubItemTitle = findViewById<TextView>(R.id.tvFAQSubItemTitle)
        ivFAQSubClear = findViewById(R.id.ivFAQSubClear)
        etFAQSubSearch = findViewById<EditText>(R.id.etFAQSubSearch)
        rlFAQSubBack = findViewById<RelativeLayout>(R.id.rlFAQSubBack)
        recyclerViewFAQSub = findViewById(R.id.recyclerViewFAQSub)
        tvFAQSubItemTitle.setText(faqTopicName)
    }

    private fun initializeListeners() {
        ivFAQSubClear!!.setOnClickListener(this)
        rlFAQSubBack.setOnClickListener(this)
        etFAQSubSearch.addTextChangedListener(this)
    }

    private fun initializeAdapter() {
        faqSubAdapter = FAQSubAdapter(this, contentList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerViewFAQSub.setLayoutManager(layoutManager)
        recyclerViewFAQSub.setItemAnimator(DefaultItemAnimator())
        recyclerViewFAQSub.setAdapter(faqSubAdapter)
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.rlFAQSubBack -> onBackPressed()
            R.id.ivFAQSubClear -> {
                if (faqSubAdapter != null) faqSubAdapter.searchedText("")
                etFAQSubSearch.setText("")
            }
            else -> {
            }
        }
    }

    override fun beforeTextChanged(
        charSequence: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        if (faqSubAdapter != null) faqSubAdapter.searchedText(charSequence.toString())
    }

    override fun afterTextChanged(editable: Editable) {}*/
}