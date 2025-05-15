package com.fishercreative.fishlogger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Unauthenticated : AuthState()
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState
    
    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    init {
        // Initialize auth state
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = when (val user = firebaseAuth.currentUser) {
                null -> AuthState.Unauthenticated
                else -> AuthState.Authenticated(user)
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).await()
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Sign out failed")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                auth.currentUser?.delete()?.await()
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Account deletion failed")
            }
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.verifyBeforeUpdateEmail(newEmail)?.await()
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Email update failed")
            }
        }
    }

    fun clearAuthResult() {
        _authResult.value = null
    }
} 