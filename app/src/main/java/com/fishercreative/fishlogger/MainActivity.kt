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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fishercreative.fishlogger.ui.navigation.Screen
import com.fishercreative.fishlogger.ui.screens.*
import com.fishercreative.fishlogger.ui.theme.FishLoggerTheme
import com.fishercreative.fishlogger.ui.theme.SilverLight
import com.fishercreative.fishlogger.ui.theme.SilverMid
import com.fishercreative.fishlogger.ui.theme.SilverDark
import com.fishercreative.fishlogger.ui.viewmodels.NewCatchViewModel
import com.fishercreative.fishlogger.ui.viewmodels.EditCatchViewModel
import com.google.android.gms.location.*
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val newCatchViewModel: NewCatchViewModel by viewModels()
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
                    viewModel = newCatchViewModel,
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
                location?.let { newCatchViewModel.updateLocation(it) }
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
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fish Logger") },
                actions = {
                    IconButton(
                        onClick = { 
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentRoute == Screen.Settings.route)
                                MaterialTheme.colorScheme.primary
                            else
                                SilverMid
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SilverLight,
                contentColor = Color.Black
            ) {
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Logged Catches",
                            tint = if (currentRoute == Screen.LoggedCatches.route) 
                                MaterialTheme.colorScheme.error 
                            else 
                                Color.Black,
                            modifier = Modifier.size(if (currentRoute == Screen.LoggedCatches.route) 28.dp else 24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            "Catches",
                            color = if (currentRoute == Screen.LoggedCatches.route) 
                                MaterialTheme.colorScheme.error 
                            else 
                                Color.Black,
                            style = if (currentRoute == Screen.LoggedCatches.route)
                                MaterialTheme.typography.labelLarge
                            else
                                MaterialTheme.typography.labelMedium
                        ) 
                    },
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
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.error,
                        selectedTextColor = MaterialTheme.colorScheme.error,
                        indicatorColor = SilverLight,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Catch",
                            tint = if (currentRoute == Screen.NewCatch.route) 
                                MaterialTheme.colorScheme.error 
                            else 
                                Color.Black,
                            modifier = Modifier.size(if (currentRoute == Screen.NewCatch.route) 28.dp else 24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            "New",
                            color = if (currentRoute == Screen.NewCatch.route) 
                                MaterialTheme.colorScheme.error 
                            else 
                                Color.Black,
                            style = if (currentRoute == Screen.NewCatch.route)
                                MaterialTheme.typography.labelLarge
                            else
                                MaterialTheme.typography.labelMedium
                        ) 
                    },
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
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.error,
                        selectedTextColor = MaterialTheme.colorScheme.error,
                        indicatorColor = SilverLight,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Map View",
                            tint = if (currentRoute == Screen.CatchMap.route) 
                                MaterialTheme.colorScheme.error 
                            else 
                                Color.Black,
                            modifier = Modifier.size(if (currentRoute == Screen.CatchMap.route) 28.dp else 24.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            "Map",
                            color = if (currentRoute == Screen.CatchMap.route) 
                                MaterialTheme.colorScheme.error 
                            else 
                                Color.Black,
                            style = if (currentRoute == Screen.CatchMap.route)
                                MaterialTheme.typography.labelLarge
                            else
                                MaterialTheme.typography.labelMedium
                        ) 
                    },
                    selected = currentRoute == Screen.CatchMap.route,
                    onClick = { 
                        if (currentRoute != Screen.CatchMap.route) {
                            navController.navigate(Screen.CatchMap.route) {
                                popUpTo(Screen.LoggedCatches.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.error,
                        selectedTextColor = MaterialTheme.colorScheme.error,
                        indicatorColor = SilverLight,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.LoggedCatches.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.LoggedCatches.route) {
                LoggedCatchesScreen(navController = navController)
            }
            composable(Screen.NewCatch.route) {
                NewCatchScreen(
                    viewModel = viewModel,
                    onRequestLocationPermission = onRequestLocationPermission,
                    navController = navController
                )
            }
            composable(
                route = Screen.EditCatch.route,
                arguments = listOf(
                    navArgument("catchId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catchId = backStackEntry.arguments?.getString("catchId") ?: return@composable
                EditCatchScreen(
                    catchId = catchId,
                    onRequestLocationPermission = onRequestLocationPermission,
                    navController = navController
                )
            }
            composable(Screen.CatchMap.route) {
                MapScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}