package com.fishercreative.fishlogger.ui.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.FishLoggerApp
import com.fishercreative.fishlogger.data.models.Catch
import com.fishercreative.fishlogger.utils.CsvExporter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LoggedCatchesViewModel(application: Application) : AndroidViewModel(application) {
    private val catchDao = FishLoggerApp.database.catchDao()
    
    var catches by mutableStateOf<List<Catch>>(emptyList())
        private set
        
    var isLoading by mutableStateOf(false)
        private set
        
    var error by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var searchFilter by mutableStateOf(SearchFilter.ALL)
        private set

    private var allCatches = listOf<Catch>()

    private val _exportResult = MutableSharedFlow<ExportResult>()
    val exportResult = _exportResult.asSharedFlow()

    private val _openFileEvent = MutableSharedFlow<Uri>()
    val openFileEvent = _openFileEvent.asSharedFlow()
    
    init {
        loadCatches()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        filterCatches()
    }

    fun updateSearchFilter(filter: SearchFilter) {
        searchFilter = filter
        filterCatches()
    }

    fun hasWritePermission(): Boolean {
        // On Android 10 (Q) and above, we don't need WRITE_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun exportToCsv() {
        if (!hasWritePermission()) {
            viewModelScope.launch {
                _exportResult.emit(ExportResult.Error("Storage permission required to export catches"))
            }
            return
        }

        viewModelScope.launch {
            try {
                if (catches.isEmpty()) {
                    _exportResult.emit(ExportResult.Error("No catches to export"))
                    return@launch
                }

                val result = CsvExporter.exportCatches(getApplication(), catches)
                _exportResult.emit(ExportResult.Success("CSV file saved to Downloads folder"))
                
                // Emit event to open the file
                _openFileEvent.emit(result.uri)
            } catch (e: Exception) {
                _exportResult.emit(ExportResult.Error("Failed to export catches: ${e.message}"))
            }
        }
    }
    
    private fun filterCatches() {
        if (searchQuery.isBlank()) {
            catches = allCatches
            return
        }

        val query = searchQuery.trim().lowercase()
        catches = allCatches.filter { catch ->
            when (searchFilter) {
                SearchFilter.ALL -> {
                    catch.species.lowercase().contains(query) ||
                    catch.waterBody.lowercase().contains(query) ||
                    catch.baitType.lowercase().contains(query) ||
                    catch.baitColor.lowercase().contains(query) ||
                    catch.retrievalMethod.toString().lowercase().contains(query)
                }
                SearchFilter.SPECIES -> catch.species.lowercase().contains(query)
                SearchFilter.WATER_BODY -> catch.waterBody.lowercase().contains(query)
                SearchFilter.BAIT_TYPE -> catch.baitType.lowercase().contains(query)
                SearchFilter.BAIT_COLOR -> catch.baitColor.lowercase().contains(query)
                SearchFilter.RETRIEVAL_METHOD -> catch.retrievalMethod.toString().lowercase().contains(query)
            }
        }
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
                    allCatches = catchList.sortedByDescending { it.createdAt }
                    filterCatches()
                    isLoading = false
                }
        }
    }
}

enum class SearchFilter {
    ALL,
    SPECIES,
    WATER_BODY,
    BAIT_TYPE,
    BAIT_COLOR,
    RETRIEVAL_METHOD;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            SPECIES -> "Species"
            WATER_BODY -> "Water Body"
            BAIT_TYPE -> "Bait Type"
            BAIT_COLOR -> "Bait Color"
            RETRIEVAL_METHOD -> "Retrieval Method"
        }
    }
}

sealed class ExportResult {
    data class Success(val message: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
} 