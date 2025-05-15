package com.fishercreative.fishlogger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DeleteAccountResult {
    object Success : DeleteAccountResult()
    data class Error(val message: String) : DeleteAccountResult()
}

class SettingsViewModel : ViewModel() {
    private val _deleteAccountResult = MutableStateFlow<DeleteAccountResult?>(null)
    val deleteAccountResult: StateFlow<DeleteAccountResult?> = _deleteAccountResult

    // Temporary implementation without Firebase
    fun deleteAccount() {
        viewModelScope.launch {
            try {
                // TODO: Implement actual account deletion with Firebase
                _deleteAccountResult.value = DeleteAccountResult.Success
            } catch (e: Exception) {
                _deleteAccountResult.value = DeleteAccountResult.Error(
                    e.message ?: "Failed to delete account"
                )
            }
        }
    }

    fun clearDeleteResult() {
        _deleteAccountResult.value = null
    }
} 