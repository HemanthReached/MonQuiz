package com.monquiz.application

import android.app.Fragment
import android.content.Context
import android.view.MenuItem
import androidx.multidex.MultiDexApplication
import com.monquiz.response.superover.qtns.Question
import com.monquiz.utils.CommonMethods
import com.monquiz.utils.Constants.INITIAL_REWARD
import java.util.*
import kotlin.jvm.Synchronized

class AppController : MultiDexApplication() {
    var currentGameNumber: String? = null
    var selectedCategory: String? = null
    var selectedStake: String? = null
    var walletBalance: Double = INITIAL_REWARD
    var selectedTableId: String? = null
    private lateinit var questionLobby: Array<Question?>

    //   private FirebaseDatabase moneyGameFirebase;
    // private ValueEventListener valueEventListe
    var usernameContext: Context? = null

    @JvmName("getUsernameContext1")
    fun getUsernameContext(): Context? {
        return usernameContext
    }


    fun getQuestionLobby(): Array<Question?> {
        return questionLobby
    }

    fun setQuestionLobby(questionLobby: Array<Question?>) {
        this.questionLobby = questionLobby
    }

    @JvmName("setUsernameContext1")
    fun setUsernameContext(usernameContext: Context?) {
        this.usernameContext = usernameContext
    }


    fun getTimeOffsets(): Long {
        return timeOffset
    }

    @JvmName("setTimeOffset1")
    fun setTimeOffsets(timeOffset: Long) {
        this.timeOffset = timeOffset
    }

    fun getFragment(): Fragment? {
        return fragment
    }

    fun setFragment(fragment: Fragment?) {
        this.fragment = fragment
    }



    /*
       public LinkedList<UserStats> getListOfUserStats() {
           return listOfUserStats;
       }

       public void setListOfUserStats(LinkedList<UserStats> listOfUserStats) {
           this.listOfUserStats = listOfUserStats;
       }
   */
    private var fragment: Fragment? = null
    var timerContext: Context? = null

    /* public LinkedList<SubmitAnswer> getListOfSubAnswers() {
          return listOfSubAnswers;
      }
  
      public void setListOfSubAnswers(LinkedList<SubmitAnswer> listOfSubAnswers) {
          this.listOfSubAnswers = listOfSubAnswers;
      }*/
    // private Question[] questionLobby;
    var playItem: MenuItem? = null

    /*
      public LinkedHashMap<String, Answer> getMapOfUserDetails() {
          return mapOfUserDetails;
      }
  
      public void setMapOfUserDetails(LinkedHashMap<String, Answer> mapOfUserDetails) {
          this.mapOfUserDetails = mapOfUserDetails;
      }
  
      public LinkedHashMap<String, DailyChallenge> getMapOfDailyChallenges() {
          return mapOfDailyChallenges;
      }*/
    /*    public void setMapOfDailyChallenges(LinkedHashMap<String, DailyChallenge> mapOfDailyChallenges) {
              this.mapOfDailyChallenges = mapOfDailyChallenges;
          }
      
          public Question[] getQuestionLobby() {
              return questionLobby;
          }
      
          public void setQuestionLobby(Question[] questionLobby) {
              this.questionLobby = questionLobby;
          }*/
    /*private LinkedList<UserStats> listOfUserStats;
           private LinkedList<UserStats> topTenUserStats;
           private LinkedList<QuizMastersUserScore> quizMastersUserScoreLinkedList;*/
    var listOfCorrectAnswer: LinkedList<String>? = null

    /*    private void getGameNumber() {
          String reference = getString(R.string.api_base_url_game_id);
          valueEventListener = moneyGameFirebase.getReference(reference)
                  .addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          try {
                              String jsonResponse = new Gson().toJson(dataSnapshot.getValue());
                              if (jsonResponse == null) return;
                              String gameId = "" + dataSnapshot.getValue();
                              PreferenceConnector.writeString(mInstance, getString(R.string.current_game_id), gameId);
                           //   CommonMethods.infoLog("App Controller gameId:" + gameId);
                          } catch (Exception exception) {
                              CommonMethods.errorLog("Exception gameId" + exception);
                          }
                      }
  
                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {
                          CommonMethods.errorLog("dbError gameId" + databaseError.getMessage());
                      }
                  });
      }*//*  public Scoreboard[] getIplScoreBoard() {
        return iplScoreBoard;
    }

    public void setIplScoreBoard(Scoreboard[] iplScoreBoard) {
        this.iplScoreBoard = iplScoreBoard;
    }*/
    /*   private LinkedList<SubmitAnswer> listOfSubAnswers;
           private LinkedHashMap<String, Answer> mapOfUserDetails;
           private LinkedHashMap<String, DailyChallenge> mapOfDailyChallenges;
       
           private LinkedHashMap<String, QuizMasterQuestion> mapQuizMasterUserDetails;
           private LinkedHashMap<String, SuperOverUser> mapSuperOverUserDetails;*/
    var opponentId: String? = null

    /* public LinkedList<UserStats> getTopTenUserStats() {
          return topTenUserStats;
      }
  
      public void setTopTenUserStats(LinkedList<UserStats> topTenUserStats) {
          this.topTenUserStats = topTenUserStats;
      }
  
      public LinkedList<QuizMastersUserScore> getQuizMastersScoreBoard() {
          return quizMastersUserScoreLinkedList;
      }*/
    /*  public void setQuizMastersScoreBoard(LinkedList<QuizMastersUserScore> quizMastersUserScoreLinkedList) {
              this.quizMastersUserScoreLinkedList = quizMastersUserScoreLinkedList;
          }
      
          public void setMapQuizMasterUsersDetails(LinkedHashMap<String, QuizMasterQuestion> mapQuizMasterUserDetails) {
              this.mapQuizMasterUserDetails = mapQuizMasterUserDetails;
          }
      
          public LinkedHashMap<String, QuizMasterQuestion> getMapQuizMasterUsersDetails() {
              return mapQuizMasterUserDetails;
          }
      
          public void setMapSuperOverUsersDetails(LinkedHashMap<String, SuperOverUser> mapSuperOverUserDetails) {
              this.mapSuperOverUserDetails = mapSuperOverUserDetails;
          }
      
          public LinkedHashMap<String, SuperOverUser> getMapSuperOverUsersDetails() {
              return mapSuperOverUserDetails;
          }
      
          public void setQuestionQuizMLobby(Question[] questionQuizMLobby) {
              this.questionQuizMLobby = questionQuizMLobby;
          }*/
    /*  // public Question[] getQuestionQuizMLobby() {
              return questionQuizMLobby;
          }*/
    // private Question[] questionQuizMLobby;
    var listOfCorrectAnswerQuizM: LinkedList<String>? = null
    var mapQuizMasterUserOrder: List<String>? = null

    /*    private void initializeObjects() {
          moneyGameFirebase = MoneyGameFirebase.getFirebaseInstance();
      }
  
      public void getTimerDetailsFromFireBase(final TimerAction timerAction) {
          valueEventListener = moneyGameFirebase.getReference(Constants.REFERENCE_TIMER)
                  .addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if (dataSnapshot.getValue() != null) {
                              nextGameStartTime = (long) dataSnapshot.getValue();
                              FirebaseFunctions.getInstance("asia-east2")
                                      .getHttpsCallable(getString(R.string.get_server_time)).call("")
                                      .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                          @Override
                                          public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                              serverTime = (long) httpsCallableResult.getData();
                                              timeOffset = serverTime - System.currentTimeMillis();
                                              //  CommonMethods.infoLog("nextGame: " + (nextGameStartTime - serverTime));
                                              if (nextGameStartTime - serverTime < 0) {
                                                  moneyGameFirebase.getReference("userQueues/restartTimer").setValue(true);
                                              } else {
                                                  timerAction.execute(nextGameStartTime - serverTime);
                                              }
                                          }
                                      }).addOnFailureListener(exception ->
                              {
                                  exception.printStackTrace();
                                  CommonMethods.errorLog("excep getTimeServer: " + exception.toString());
                              });
                          }
                      }
  
                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {
                          CommonMethods.errorLog("dbError getTimeServer: " + databaseError.getMessage());
                      }
                  });
      }*/
    //  private Scoreboard[] iplScoreBoard;
    var nextGameStartTime: Long = 0
    var serverTime: Long = 0
    var timeOffset: Long = 0
    var scoreBoardLeaderDetails: String? = null
    override fun onCreate() {
        super.onCreate()
        // Fabric.with(this, Crashlytics())
        instance = this
        // initializeObjects()
        // getGameNumber()
        CommonMethods.infoLog("appcontroller")
    }

    override fun onTerminate() {
        super.onTerminate()
        //  moneyGameFirebase.getReference(Constants.REFERENCE_TIMER).removeEventListener(valueEventListener);
    }

    companion object {
        @get:Synchronized
        var instance: AppController? = null
            private set
    }
}