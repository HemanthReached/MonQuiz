package com.monquiz.network

import com.monquiz.model.inputdata.LoginOTpSend_Data
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.model.inputdata.updateprofile.UpdateProfileInputBody
import com.monquiz.model.verifyotpmodel.VerifyOTP_Data
import com.monquiz.response.bankdetails.input.BankDetailsInput
import com.monquiz.response.bankdetails.response.BankDetailsResponse
import com.monquiz.response.bankdetails.response.GetBankDetailsResponse
import com.monquiz.response.getGamesResponse.Get_GamesResponse
import com.monquiz.response.getdailylimit.GetDailyLimit_INput
import com.monquiz.response.getdailylimit.GetDailyLimit_Response
import com.monquiz.response.getstakesresp.GetAll_StakesResponse
import com.monquiz.response.leaderboard.FinalScore_Input
import com.monquiz.response.leaderboard.input.ChecksumData
import com.monquiz.response.leaderboard.input.LeaderBoard_InputData
import com.monquiz.response.leaderboard.resp.CheckSum_Resp
import com.monquiz.response.leaderboard.resp.GetLeaderBoardPositionResponse
import com.monquiz.response.leaderboard.resp.LeaderBoard_Resp
import com.monquiz.response.leftroom.input.LeftLobyy_Input
import com.monquiz.response.login.LoginResponseOtp
import com.monquiz.response.review.input.CreateFeedBackInput
import com.monquiz.response.review.response.CreateFeedBackResponse
import com.monquiz.response.superover.exit.GameExit_Response
import com.monquiz.response.superover.exit.Game_Lobby_Exit_Input
import com.monquiz.response.superover.join.GameJoin_Response
import com.monquiz.response.superover.join.input.SuperOver_JoinLobby_Input
import com.monquiz.response.superover.qtns.input.SuperOver_Questions
import com.monquiz.response.superover.qtns.resp.SuperOVer_QtnsResponse
import com.monquiz.response.superover.qtns.submit.SuperOverQtn_Submit
import com.monquiz.response.superover.qtns.submit.resp.SuperOver_QtnSubmitResponse
import com.monquiz.response.transactionaapi.TransactionApi_Response
import com.monquiz.response.transactionaapi.input.TransactionApi_Input
import com.monquiz.response.updatecheck.UpdateResponse
import com.monquiz.response.updateprofile.Update_ProfileResponse
import com.monquiz.response.verifyotp_packageresponse.VerifyOtp_Response
import com.monquiz.response.wallet.input.WalletInput
import com.monquiz.response.wallet.input.verifyInput
import com.monquiz.response.wallet.res.GameDetails_Response
import com.monquiz.response.wallet.res.Wallet_Response
import com.monquiz.response.wallet.response.VerifyResponse
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface Retrofitapi {

    @POST("v1/login")
    fun login_phone(@Body loginOtpSend: LoginOTpSend_Data):Call<LoginResponseOtp>

    @POST("v1/verifyOTP")
    fun Verify_Otp(@Body verifyOtpBody: VerifyOTP_Data):Call<VerifyOtp_Response>

    //update profile
    @POST("v1/updateUserProfile")
    fun updateProfile(@Body updateInput:UpdateProfileInputBody,
                      @Header("Authorization") token: String = "Bearer " +
                              PrefsHelper().getPref(OwlizConstants.token)):Call<Update_ProfileResponse>

    @POST("v1/refferalBonus")
    fun Referral(@Body getProfileData:GetUserProfileInput,
                 @Header("Authorization") token: String = "Bearer " +
                         PrefsHelper().getPref(OwlizConstants.token)):Call<ResponseBody>
    @POST("v1/getUserById")
    fun getProfile(@Body getProfileData:GetUserProfileInput,
                   @Header("Authorization") token: String = "Bearer " +
                           PrefsHelper().getPref(OwlizConstants.token)):Call<LoginResponseOtp>

    @POST("v1/uploadProfilePicture/"+"{UserId}")
    fun uploadDp(@Path(value = "UserId", encoded = true) UserId: String,
                         @Body requestBody: RequestBody,
                 @Header("Authorization") token: String = "Bearer " +
                         PrefsHelper().getPref(OwlizConstants.token)) : Call<ResponseBody>

    @POST("v1/gamesCountByUserId")
    fun getPlayDetails(@Body requestBody: RequestBody,
                       @Header("Authorization") token: String = "Bearer "
                               + PrefsHelper().getPref(OwlizConstants.token)):Call<GameDetails_Response>

    @GET("v1/getGames",)
    fun getAllGames(@Header("Authorization") token: String = "Bearer " +
            PrefsHelper().getPref(OwlizConstants.token)):Call<Get_GamesResponse>

    @GET("v1/getStakes")
    fun getAllStacks(@Header("Authorization") token: String = "Bearer " +
            PrefsHelper().getPref(OwlizConstants.token)):Call<GetAll_StakesResponse>

    @POST("v1/getDailyLimit")
    fun getDailyLimit(@Body dailyLimit:GetDailyLimit_INput,
                      @Header("Authorization") token: String = "Bearer " +
                              PrefsHelper().getPref(OwlizConstants.token)):Call<GetDailyLimit_Response>

    @POST("v1/join")
    fun GameJoin_SuperOver(@Body join:SuperOver_JoinLobby_Input,
                           @Header("Authorization") token: String = "Bearer " +
                                   PrefsHelper().getPref(OwlizConstants.token)):Call<GameJoin_Response>

    @POST("v1/getPlayRoomQuestion")
    fun superOverQuestions(@Body qtnsInput:SuperOver_Questions
                            ,@Header("Authorization") token: String = "Bearer " +
                PrefsHelper().getPref(OwlizConstants.token)):Call<SuperOVer_QtnsResponse>

    @POST("v1/submitAnswer")
    fun superOverSubmitQuestions(@Body submitQtn:SuperOverQtn_Submit,
                                  @Header("Authorization") token: String = "Bearer " +
                PrefsHelper().getPref(OwlizConstants.token)):Call<SuperOver_QtnSubmitResponse>

    @POST("v1/leftGame")
    fun leftLobby(@Body exit:LeftLobyy_Input,
                 @Header("Authorization") token: String = "Bearer " +
                         PrefsHelper().getPref(OwlizConstants.token)):Call<GameExit_Response>

    @POST("v1/exitFromLobby")
    fun ExitFrom_Game(@Body exit:Game_Lobby_Exit_Input,
                      @Header("Authorization") token: String = "Bearer " +
                              PrefsHelper().getPref(OwlizConstants.token)):Call<GameExit_Response>


    //createOrUpdateReview
    @POST("v1/createOrUpdateReview")
    fun createReview(@Body input : CreateFeedBackInput,
                     @Header("Authorization") token: String = "Bearer " +
                             PrefsHelper().getPref(OwlizConstants.token)):Call<CreateFeedBackResponse>

    @POST("v1/submitLeaderBoardData")
    fun leaderBoard(@Body leaderboard:LeaderBoard_InputData,
                    @Header("Authorization") token: String = "Bearer " +
                            PrefsHelper().getPref(OwlizConstants.token)):Call<LeaderBoard_Resp>

    @POST("v1/initiateTransaction")
    fun getChecksum(@Body leaderboard: ChecksumData,
                    @Header("Authorization") token: String = "Bearer " +
                            PrefsHelper().getPref(OwlizConstants.token)) : Call<CheckSum_Resp>

    @POST("v1/getUserWalletBalance")
    fun getWalletData(@Body walletInput: WalletInput,
                    @Header("Authorization") token: String = "Bearer " +
                            PrefsHelper().getPref(OwlizConstants.token)) : Call<Wallet_Response>

    @POST("v1/verifiyPancard")
    fun verifyPan(@Body walletInput: verifyInput,
                  @Header("Authorization") token: String = "Bearer " +
                          PrefsHelper().getPref(OwlizConstants.token)) : Call<VerifyResponse>

    @POST("v1/getbankAndPanDetails")
    fun getBankDetails(@Body input : WalletInput,
                        @Header("Authorization") token: String = "Bearer " +
                                PrefsHelper().getPref(OwlizConstants.token)) : Call<GetBankDetailsResponse>

    // savebankDetails
    @POST("v1/savebankDetails")
    fun saveBankDetails(@Body input : BankDetailsInput,
                  @Header("Authorization") token: String = "Bearer " +
                          PrefsHelper().getPref(OwlizConstants.token)) : Call<BankDetailsResponse>

    //getLeaderBoard
    @POST("v1/getLeaderBoard")
    fun getLeaderBoard(@Body input : GetUserProfileInput,@Header("Authorization") token: String = "Bearer " +
            PrefsHelper().getPref(OwlizConstants.token)) : Call<GetLeaderBoardPositionResponse>

    @POST("v1/getTransactionHistory")
    fun getTransactions(@Body transactionInput: TransactionApi_Input,
                      @Header("Authorization") token: String = "Bearer " +
                              PrefsHelper().getPref(OwlizConstants.token)) : Call<TransactionApi_Response>

    //getWinnerAndLooser
    @POST("v1/getWinnerAndLooser")
    fun getscores(@Body finalScoreInput: FinalScore_Input,
                  @Header("Authorization") token: String = "Bearer " +
                          PrefsHelper().getPref(OwlizConstants.token)) : Call<CheckSum_Resp>

   // api/v1/order
   @POST("v1/order")
   fun createOrder(@Body requestBody: RequestBody) : Call<ResponseBody>

    //api/v1/capture
    @POST("v1/capture")
    fun updateOrder(@Body requestBody: RequestBody) : Call<ResponseBody>

    // api/v1/payout
    @POST("v1/payout")
    fun RedeemCash(@Body requestBody: RequestBody) : Call<ResponseBody>

    // api/v1/getLatestVersion
    @POST("v1/getLatestVersion")
    fun updateCheck() : Call<UpdateResponse>

}

