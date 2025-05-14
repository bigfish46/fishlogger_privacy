package com.fishercreative.fishlogger.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fishercreative.fishlogger.data.models.Catch

@Database(
    entities = [Catch::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catchDao(): CatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fish_logger_db"
                )
                .fallbackToDestructiveMigration() // Only during development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 