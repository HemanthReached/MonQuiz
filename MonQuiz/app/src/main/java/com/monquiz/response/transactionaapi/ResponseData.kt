package com.monquiz.response.transactionaapi


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("amount")
    var amount: Double? = null,
    @SerializedName("couponCode")
    var couponCode: String? = null,
    @SerializedName("couponValue")
    var couponValue: Int? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("monthName")
    var monthName : String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("isCouponApplied")
    var isCouponApplied: Boolean? = null,
    @SerializedName("orderId")
    var orderId: String? = null,
    @SerializedName("paymentGatewayCharges")
    var paymentGatewayCharges: Double? = null,
    @SerializedName("paymentMode")
    var paymentMode: String? = null,
    @SerializedName("payoutStatus")
    var payoutStatus: String? = null,
    @SerializedName("paytmTransactionId")
    var paytmTransactionId: String? = null,
    @SerializedName("referenceId")
    var referenceId: String? = null,
    @SerializedName("refundId")
    var refundId: String? = null,
    @SerializedName("refundInitiatedAt")
    var refundInitiatedAt: String? = null,
    @SerializedName("refundInitiatedAtDate")
    var refundInitiatedAtDate: String? = null,
    @SerializedName("refundStatus")
    var refundStatus: String? = null,
    @SerializedName("signature")
    var signature: String? = null,
    @SerializedName("totalAmountToPay")
    var totalAmountToPay: Double? = null,
    @SerializedName("transactionDate")
    var transactionDate: String? = null,
    @SerializedName("transactionStatus")
    var transactionStatus: String? = null,
    @SerializedName("transactionType")
    var transactionType: String? = null,
    @SerializedName("transactionToken")
    var transactionToken: String? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("__v")
    var v: Int? = null,
    @SerializedName("winnerId")
    var winnerId: String? = null,
    @SerializedName("looserId")
    var looserId: String? = null,
    @SerializedName("stakeAmount")
    var stakeAmount: String? = null,
    @SerializedName("gameStatus")
    var gameStatus : String? = null,
    var IsDetailsVisible : Boolean = false
)