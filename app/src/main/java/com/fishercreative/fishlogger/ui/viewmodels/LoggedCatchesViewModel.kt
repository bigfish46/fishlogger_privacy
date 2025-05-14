package com.fishercreative.fishlogger.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.FishLoggerApp
import com.fishercreative.fishlogger.data.models.Catch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LoggedCatchesViewModel(application: Application) : AndroidViewModel(application) {
    private val catchDao = FishLoggerApp.database.catchDao()
    
    var catches by mutableStateOf<List<Catch>>(emptyList())
        private set
        
    var isLoading by mutableStateOf(false)
        private set
        
    var error by mutableStateOf<String?>(null)
        private set
    
    init {
        loadCatches()
    }
    
    fun loadCatches() {
        viewModelScope.launch {
            catchDao.getAllCatches()
                .onStart { isLoading = true }
                .catch { e -> 
                    error = "Failed to load catches: ${e.message}"
                    isLoading = false
                }
                .collect { catchList ->
                    catches = catchList.sortedByDescending { it.createdAt }
                    isLoading = false
                }
        }
    }
} 