package com.monquiz.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.monquiz.db.daos.SuperoverQtns_Dao
import com.monquiz.db.daos.typeconverter.Deliverytypeconverter
import com.monquiz.response.superover.join.Question


@Database(
    entities = [Question::class],
    version = 1)
@TypeConverters(Deliverytypeconverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getsuperoverqtns(): SuperoverQtns_Dao


    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
            instance
                ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "MonQuiz.db"
            ).build()
    }
}