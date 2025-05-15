package com.fishercreative.fishlogger.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.FishLoggerApp
import com.fishercreative.fishlogger.data.models.Catch
import kotlinx.coroutines.flow.*

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val catchDao = FishLoggerApp.database.catchDao()
    
    // Only show catches that have location data
    val catches: StateFlow<List<Catch>> = catchDao.getAllCatches()
        .map { catches ->
            catches.filter { catch ->
                catch.latitude != null && catch.longitude != null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        private const val TAG = "MapViewModel"
    }
} 