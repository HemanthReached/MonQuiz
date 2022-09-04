package com.monquiz.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.R
import com.monquiz.response.transactionaapi.ResponseData
import com.monquiz.utils.AppUtils
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import es.dmoral.toasty.Toasty
import java.util.*

class TransactionsHistoryAdapter(var context: Context, var listTransHistory: LinkedList<ResponseData>?,
                                 var listener : DetailsArrowClickListener
) : RecyclerView.Adapter<TransactionsHistoryAdapter.ViewHolder?>() {

    var orderId: String? = null
    var date = ""

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(@NonNull viewHolder: ViewHolder, position: Int) {
        val item =  listTransHistory!![position]
        viewHolder.tvGame_Id.visibility = View.GONE
        viewHolder.tvGameId_desc.visibility = View.GONE
        viewHolder.ivGID_copy.visibility = View.GONE
        if (position == 0){
            viewHolder.tvMonthName.visibility = View.VISIBLE
            if (AppUtils.checkCurrentMonth(item.monthName!!)){
                viewHolder.tvMonthName.text = "This Month"
            }else{
                viewHolder.tvMonthName.text = item.monthName
            }
        }
        else if (item.monthName != listTransHistory!![position -1].monthName){
            viewHolder.tvMonthName.visibility = View.VISIBLE
            viewHolder.tvMonthName.text = item.monthName
        }
        else{
            viewHolder.tvMonthName.visibility = View.GONE
        }
        if (item.IsDetailsVisible){
            viewHolder.ivDetails_arrow.scaleY = -1f
            viewHolder.tDetails_layout.visibility = View.VISIBLE
        }else{
            viewHolder.ivDetails_arrow.scaleY = 1f
            viewHolder.tDetails_layout.visibility = View.GONE
        }
        viewHolder.imageClickLayout.setOnClickListener {
            listener.onDetailsArrowClick(item,position)
        }

        if (listTransHistory != null && listTransHistory!![position].createdAt != null) {
            try {
                date = AppUtils.convertDateFormat(listTransHistory!![position].createdAt!!,
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM HH:mm")
            } catch (e: Exception) {
                Log.e("Log",e.toString())
            }
        }
        orderId = if (listTransHistory != null && listTransHistory!![position].orderId != null
            && !listTransHistory!![position].orderId.isNullOrEmpty()) {
            listTransHistory!![position].orderId
        } else{ listTransHistory!![position].id }

        viewHolder.transactionTime.text = date
        viewHolder.tvTransaction_Id.text = orderId
        viewHolder.ivTID_copy.setOnClickListener {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", viewHolder.tvTransaction_Id.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toasty.success(context, "Copied.", Toasty.LENGTH_SHORT).show()
        }

        val amount = listTransHistory!![position].amount
        if (amount != null){
            val ttype = listTransHistory!![position].transactionType
            val tstatus = listTransHistory!![position].transactionStatus
            if ( ttype == "deposit"){
                if (tstatus == "captured"){
                    viewHolder.tvTransaction_type.text = "Money Added"
                    viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_add))
                    viewHolder.tvTransaction_amount.text = "+" + context.getString(R.string.ruppee) + amount
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.colorGreen))
                    viewHolder.transactionStatus.visibility = View.GONE
                }else if (tstatus == "created" || tstatus == "authorized"){
                    viewHolder.tvTransaction_type.text = "Money Added"
                    viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_add))
                    viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black_60))
                    viewHolder.transactionStatus.visibility = View.VISIBLE
                    viewHolder.transactionStatus.text = "Processing"
                    viewHolder.transactionStatus.setTextColor(ContextCompat.getColor(context,R.color.yellow_100))
                }else if (tstatus == "refunded"){
                    viewHolder.tvTransaction_type.text = "Refund"
                    viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_refund_icon))
                    viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black_60))
                    viewHolder.transactionStatus.visibility = View.GONE
                }else if (tstatus == "failed"){
                    viewHolder.tvTransaction_type.text = "Money Added"
                    viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_add))
                    viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                    viewHolder.transactionStatus.visibility = View.VISIBLE
                    viewHolder.transactionStatus.text = "Failed"
                    viewHolder.transactionStatus.setTextColor(ContextCompat.getColor(context,R.color.red))
                }else{
                    viewHolder.tvTransaction_type.text = "Money Added"
                    viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_add))
                    viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                    viewHolder.transactionStatus.visibility = View.VISIBLE
                    viewHolder.transactionStatus.text = "Failed"
                    viewHolder.transactionStatus.setTextColor(ContextCompat.getColor(context,R.color.red))
                }
            }
            else if (ttype == "withdraw"){
                if (!listTransHistory!![position].payoutStatus.isNullOrEmpty()){
                    val pstatus = listTransHistory!![position].payoutStatus
                    if (pstatus == "processed"){
                        viewHolder.tvTransaction_type.text = "Redeemed"
                        viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_withdraw))
                        viewHolder.tvTransaction_amount.text = "-" + context.getString(R.string.ruppee) + amount
                        viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                        viewHolder.transactionStatus.visibility = View.GONE
                    }else if (pstatus == "processing" || pstatus == "queued" || pstatus == "pending"){
                        viewHolder.tvTransaction_type.text = "Redeemed"
                        viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_withdraw))
                        viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                        viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                        viewHolder.transactionStatus.visibility = View.VISIBLE
                        viewHolder.transactionStatus.text = "Processing"
                        viewHolder.transactionStatus.setTextColor(ContextCompat.getColor(context,R.color.yellow_100))
                    }else if (pstatus == "rejected" || pstatus == "cancelled" || pstatus == "reversed"){
                        viewHolder.tvTransaction_type.text = "Redeemed"
                        viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_withdraw))
                        viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                        viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                        viewHolder.transactionStatus.visibility = View.VISIBLE
                        viewHolder.transactionStatus.text = "Failed"
                        viewHolder.transactionStatus.setTextColor(ContextCompat.getColor(context,R.color.red))
                    }else{
                        viewHolder.tvTransaction_type.text = "Redeemed"
                        viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_withdraw))
                        viewHolder.tvTransaction_amount.text = context.getString(R.string.ruppee) + amount
                        viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                        viewHolder.transactionStatus.visibility = View.VISIBLE
                        viewHolder.transactionStatus.text = "Failed"
                        viewHolder.transactionStatus.setTextColor(ContextCompat.getColor(context,R.color.red))
                    }
                }
            }
            else if (ttype == "REFFERRAL_BONUS"){
                viewHolder.tvTransaction_type.text = "MonQuiz Promo"
                viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.money_add))
                viewHolder.tvTransaction_amount.text = "+" + context.getString(R.string.ruppee) + amount
                viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.colorGreen))
                viewHolder.transactionStatus.visibility = View.GONE
            }
        }
        val stake = listTransHistory!![position].stakeAmount
        if (stake != null){
            if (listTransHistory!![position].winnerId == PrefsHelper().getPref(OwlizConstants.user_id)){
                val amt: Double
                if (listTransHistory!![position].gameStatus == "TIE"){
                    amt = ((stake.toDouble() * 10) /100)
                    viewHolder.tvTransaction_amount.text = "-" + context.getString(R.string.ruppee) + amt
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                } else{
                    amt = ((stake.toDouble() * 80) /100)
                    viewHolder.tvTransaction_amount.text = "+" + context.getString(R.string.ruppee) + amt
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.colorGreen))
                }
            }else{
                val amt: Double
                if (listTransHistory!![position].gameStatus == "TIE"){
                    amt = ((stake.toDouble() * 10) /100)
                    viewHolder.tvTransaction_amount.text = "-" + context.getString(R.string.ruppee) + amt
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                } else{
                    viewHolder.tvTransaction_amount.text = "-" + context.getString(R.string.ruppee) + stake.toDouble()
                    viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))
                }
              /*  viewHolder.tvTransaction_amount.text = "-" + context.getString(R.string.ruppee) + stake.toDouble()
                viewHolder.tvTransaction_amount.setTextColor(ContextCompat.getColor(context,R.color.black))*/
            }
            viewHolder.tvTransaction_type.text = "Super Over"
            viewHolder.ivTransaction_type.setImageDrawable(AppCompatResources.getDrawable(context,
                R.drawable.super_over_category1))
            viewHolder.transactionStatus.visibility = View.GONE
        }

    }
    override fun getItemCount(): Int { return listTransHistory!!.size }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tDetails_layout : ConstraintLayout = itemView.findViewById(R.id.tDetails_layout)
        val imageClickLayout : RelativeLayout = itemView.findViewById(R.id.imageClickLayout)
        val ivTransaction_type : ImageView = itemView.findViewById(R.id.ivTransaction_type)
        val ivDetails_arrow : ImageView = itemView.findViewById(R.id.ivDetails_arrow)
        val ivTID_copy : ImageView = itemView.findViewById(R.id.ivTID_copy)
        val ivGID_copy : ImageView = itemView.findViewById(R.id.ivGID_copy)

        val tvTransaction_type : TextView = itemView.findViewById(R.id.tvTransaction_type)
        val transactionTime : TextView = itemView.findViewById(R.id.transactionTime)
        val tvTransaction_amount : TextView = itemView.findViewById(R.id.tvTransaction_amount)
        val tvTransaction_Id : TextView = itemView.findViewById(R.id.tvTransaction_Id)
        val tvGame_Id : TextView = itemView.findViewById(R.id.tvGame_Id)
        val tvGameId_desc : TextView = itemView.findViewById(R.id.tvGameId_desc)
        val tvMonthName : TextView = itemView.findViewById(R.id.monthname_view)
        val transactionStatus : TextView = itemView.findViewById(R.id.transaction_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_transation_history_item,
            parent, false)
        return ViewHolder(view)
    }

}

interface DetailsArrowClickListener{ fun onDetailsArrowClick(item: ResponseData,position: Int)}