package com.monquiz.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.R
import com.monquiz.model.FaqModel

class FAQAdapter(var context : Context, var array : ArrayList<FaqModel>
                       /* var listener1 : IsFilterResultsEmpty*/):
    RecyclerView.Adapter<FAQAdapter.ViewHolder>(),Filterable {

    var filterable : MutableList<FaqModel> = array

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

       val baseview = LayoutInflater.from(context).inflate(R.layout.item_faq,
           parent,false)
        return ViewHolder(baseview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filterable[position]
        if (item.isHeading){
            holder.headingview.visibility = View.VISIBLE
            holder.headingview.text = item.Heading
            holder.questionview.visibility = View.GONE
            holder.answerview.visibility = View.GONE
        }else{
            holder.headingview.visibility = View.GONE
            holder.questionview.visibility = View.VISIBLE
            holder.answerview.visibility = View.VISIBLE
            holder.questionview.text = "Q. ${item.Question.trim()}"
            holder.answerview.text = "A. ${item.Answer.trim()}"
        }
    }

    override fun getItemCount() = filterable.size

    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filterlist : MutableList<FaqModel> = ArrayList<FaqModel>()

                if (constraint == null || constraint.isEmpty()) {
                    // set the Original result to return
                    results.count = array.size
                    results.values = array
                } else {
                    val constraint1 = constraint.toString().lowercase()
                    for (i in 0 until array.size) {
                        val data: String = array[i].Question
                        if (data.lowercase().contains(constraint1)) {
                            filterlist.add(array[i])
                        }
                    }
                    // set the Filtered result to return
                    results.count = filterlist.size
                    results.values = filterlist
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterable = results?.values as ArrayList<FaqModel>
                if (filterable.isEmpty()){
                   // listener1.onResultsEmpty(true)
                    notifyDataSetChanged()
                }else{
                  //  listener1.onResultsEmpty(false)
                    notifyDataSetChanged()
                }
            }
        }
        return filter
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var headingview : TextView = itemView.findViewById(R.id.headingTextview)
        var questionview : TextView = itemView.findViewById(R.id.questionTextview)
        var answerview : TextView = itemView.findViewById(R.id.answerTextview)
    }
}

/*
interface IsFilterResultsEmpty{
    fun onResultsEmpty(isEmpty : Boolean)
}*/
