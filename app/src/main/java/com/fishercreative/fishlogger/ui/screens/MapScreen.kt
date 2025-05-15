package com.fishercreative.fishlogger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.fishercreative.fishlogger.ui.viewmodels.MapViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.fishercreative.fishlogger.ui.navigation.Screen

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = viewModel()
) {
    val catches by viewModel.catches.collectAsState()
    var selectedCatchId by remember { mutableStateOf<String?>(null) }
    
    // Default camera position (center of US)
    val defaultCameraPosition = remember {
        CameraPosition.builder()
            .target(LatLng(39.8283, -98.5795))
            .zoom(3f)
            .build()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                position = defaultCameraPosition
            },
            properties = MapProperties(
                isMyLocationEnabled = false
            )
        ) {
            catches.forEach { catch ->
                catch.latitude?.let { lat ->
                    catch.longitude?.let { lng ->
                        Marker(
                            state = MarkerState(position = LatLng(lat, lng)),
                            title = catch.species,
                            snippet = "${catch.date} - ${catch.waterBody}",
                            onClick = {
                                selectedCatchId = catch.id
                                true
                            }
                        )
                    }
                }
            }
        }

        // Info card for selected catch
        selectedCatchId?.let { id ->
            val selectedCatch = catches.find { it.id == id }
            selectedCatch?.let { catch ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = catch.species,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = catch.waterBody,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${catch.date} - ${catch.time}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (catch.lengthInches > 0) {
                            Text(
                                text = "Length: ${catch.lengthInches} inches",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (catch.weightPounds > 0 || catch.weightOunces > 0) {
                            Text(
                                text = "Weight: ${catch.weightPounds}lbs ${catch.weightOunces}oz",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                navController.navigate(Screen.EditCatch.createRoute(catch.id))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Details")
                        }
                    }
                }
            }
        }
    }
} 