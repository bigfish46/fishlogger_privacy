package com.fishercreative.fishlogger.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.data.models.Catch
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoggedCatchesViewModel : ViewModel() {
    private val db = Firebase.firestore
    
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
            isLoading = true
            error = null
            try {
                val snapshot = db.collection("catches")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                catches = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Catch::class.java)
                }
            } catch (e: Exception) {
                error = "Failed to load catches: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
} 