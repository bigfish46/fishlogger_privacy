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
import kotlinx.coroutines.flow.map

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