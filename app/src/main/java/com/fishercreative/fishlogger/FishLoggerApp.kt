package com.fishercreative.fishlogger

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FishLoggerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Crashlytics in Debug mode
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
} 