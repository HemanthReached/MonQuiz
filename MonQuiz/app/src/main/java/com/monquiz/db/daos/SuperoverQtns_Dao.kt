package com.monquiz.db.daos

import androidx.room.Dao

@Dao
interface SuperoverQtns_Dao {
    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLearnerData(learnerData: Question)
*//*    @Query
        ("SELECT*FROM LearnerTable where idss= $LearnerDbID")
    fun getLearnerData(): List<Data>*//*

    @Query("SELECT*FROM SuperOver_qtns where idss=$superoverqtns")
    suspend fun getLearnerDetails():List<Question>

    @Query("DELETE FROM SuperOver_qtns")
    fun deleteAll()*/

}