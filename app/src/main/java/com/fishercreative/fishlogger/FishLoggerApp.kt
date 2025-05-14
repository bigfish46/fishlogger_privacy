package com.fishercreative.fishlogger

import android.app.Application
import com.fishercreative.fishlogger.data.db.AppDatabase

class FishLoggerApp : Application() {
    
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Room database using singleton pattern
        database = AppDatabase.getDatabase(this)
    }
} 