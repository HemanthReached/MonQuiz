package com.monquiz.interfaces

interface TimerCallBack {
    fun timerCount(time: String?, millisUntilFinished: Long)
    fun transitionOnUserDetailsOrTimerUpdate(
        millisUntilFinished: Long,
        enoughUsersAndGotQuestion: Boolean
    )
}