package com.monquiz.response.superover.exit

import com.google.gson.annotations.SerializedName
import com.monquiz.response.login.ResponseData

data class GameExit_Response (@SerializedName("message")
                                var message: String,
                              @SerializedName("responseData")
                                var responseData: ResponseData,
                              @SerializedName("status")
                                var status: Int)