package com.fishercreative.fishlogger.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fishercreative.fishlogger.ui.navigation.Screen
import com.fishercreative.fishlogger.ui.viewmodels.*
import com.fishercreative.fishlogger.ui.theme.SilverMid
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var pendingEmailUpdate by remember { mutableStateOf<String?>(null) }
    val deleteResult by settingsViewModel.deleteAccountResult.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val authResult by authViewModel.authResult.collectAsState()

    // Google Sign-In setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("340540202883-mkbubsdca9v0gq1n0ns5ku41lqpltp49.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.signInWithGoogle(account)
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Sign in failed: General error"
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in cancelled by user"
                GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Sign in already in progress"
                GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> "Sign in required"
                GoogleSignInStatusCodes.INVALID_ACCOUNT -> "Invalid account"
                GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error"
                else -> "Sign in failed: ${e.statusCode} - ${e.message}"
            }
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(authResult) {
        when (authResult) {
            is AuthResult.Success -> {
                Toast.makeText(
                    context,
                    "Verification email sent. Please check your inbox and click the verification link to complete the email update.",
                    Toast.LENGTH_LONG
                ).show()
                authViewModel.clearAuthResult()
            }
            is AuthResult.Error -> {
                Toast.makeText(
                    context,
                    (authResult as AuthResult.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                authViewModel.clearAuthResult()
            }
            null -> {}
        }
    }

    LaunchedEffect(deleteResult) {
        when (deleteResult) {
            is DeleteAccountResult.Success -> {
                authViewModel.deleteAccount()
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.LoggedCatches.route) {
                    popUpTo(Screen.LoggedCatches.route) {
                        inclusive = true
                    }
                }
                settingsViewModel.clearDeleteResult()
            }
            is DeleteAccountResult.Error -> {
                Toast.makeText(
                    context,
                    (deleteResult as DeleteAccountResult.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                settingsViewModel.clearDeleteResult()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = SilverMid
                            )
                            Text(
                                text = "Account",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    when (authState) {
                        is AuthState.Authenticated -> {
                            // Email Section
                            val user = (authState as AuthState.Authenticated).user
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Email",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = user.email ?: "No email",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(onClick = { showEditEmailDialog = true }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit email",
                                        tint = SilverMid
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Sign Out Button
                            FilledTonalButton(
                                onClick = { authViewModel.signOut() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out")
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Delete Account Button
                            FilledTonalButton(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Account")
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            // Sign In Options
                            Text(
                                text = "Sign in with:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FilledTonalButton(
                                onClick = { 
                                    signInLauncher.launch(googleSignInClient.signInIntent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign in with Google")
                            }
                        }
                        AuthState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = {
                Text(
                    "Are you sure you want to delete your account? This action cannot be undone. " +
                    "All your data, including catches and photos, will be permanently deleted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        settingsViewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditEmailDialog) {
        var email by remember { 
            mutableStateOf((authState as? AuthState.Authenticated)?.user?.email ?: "") 
        }
        
        AlertDialog(
            onDismissRequest = { showEditEmailDialog = false },
            title = { Text("Edit Email") },
            text = {
                Column {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: You'll need to verify your new email address by clicking a link we'll send to it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditEmailDialog = false
                        pendingEmailUpdate = email
                        authViewModel.updateEmail(email)
                    }
                ) {
                    Text("Send Verification")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditEmailDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 