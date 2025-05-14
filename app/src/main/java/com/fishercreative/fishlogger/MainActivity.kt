package com.fishercreative.fishlogger

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fishercreative.fishlogger.ui.navigation.Screen
import com.fishercreative.fishlogger.ui.screens.LoggedCatchesScreen
import com.fishercreative.fishlogger.ui.screens.NewCatchScreen
import com.fishercreative.fishlogger.ui.theme.FishLoggerTheme
import com.fishercreative.fishlogger.ui.viewmodels.NewCatchViewModel
import com.google.android.gms.location.*

class MainActivity : ComponentActivity() {
    private val viewModel: NewCatchViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLastLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        
        setContent {
            FishLoggerTheme {
                MainScreen(
                    viewModel = viewModel,
                    onRequestLocationPermission = {
                        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let { viewModel.updateLocation(it) }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    viewModel: NewCatchViewModel,
    onRequestLocationPermission: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.LoggedCatches.route
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fish Logger") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Logged Catches") },
                    label = { Text("Catches") },
                    selected = currentRoute == Screen.LoggedCatches.route,
                    onClick = { 
                        if (currentRoute != Screen.LoggedCatches.route) {
                            navController.navigate(Screen.LoggedCatches.route) {
                                popUpTo(Screen.LoggedCatches.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "New Catch") },
                    label = { Text("New") },
                    selected = currentRoute == Screen.NewCatch.route,
                    onClick = { 
                        if (currentRoute != Screen.NewCatch.route) {
                            navController.navigate(Screen.NewCatch.route) {
                                popUpTo(Screen.LoggedCatches.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.LoggedCatches.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.LoggedCatches.route) {
                LoggedCatchesScreen()
            }
            composable(Screen.NewCatch.route) {
                NewCatchScreen(
                    viewModel = viewModel,
                    onRequestLocationPermission = onRequestLocationPermission,
                    navController = navController
                )
            }
        }
    }
}