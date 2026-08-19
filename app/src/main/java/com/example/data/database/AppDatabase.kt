package com.example.data.database

import android.content.Context
import androidx.room.*

@Database(entities = [QueryHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun queryHistoryDao(): QueryHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "asistente_electoral_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
