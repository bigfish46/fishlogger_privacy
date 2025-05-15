package com.fishercreative.fishlogger.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.fishercreative.fishlogger.data.Constants
import com.fishercreative.fishlogger.data.models.CloudCover
import com.fishercreative.fishlogger.data.models.WaterTurbidity
import com.fishercreative.fishlogger.data.models.Catch
import com.fishercreative.fishlogger.data.models.RetrievalMethod
import com.fishercreative.fishlogger.ui.components.PhotoCapture
import com.fishercreative.fishlogger.ui.viewmodels.NewCatchViewModel
import com.fishercreative.fishlogger.ui.viewmodels.StateValidationResult
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.fishercreative.fishlogger.ui.navigation.Screen
import com.fishercreative.fishlogger.ui.viewmodels.SaveResult
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCatchScreen(
    viewModel: NewCatchViewModel,
    onRequestLocationPermission: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isSaving by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun hideKeyboard() {
        focusManager.clearFocus()
    }

    // Call onScreenShown when the screen is shown
    LaunchedEffect(Unit) {
        viewModel.onScreenShown()
    }

    LaunchedEffect(Unit) {
        viewModel.saveResult.collectLatest { result ->
            isSaving = false
            when (result) {
                SaveResult.Success -> {
                    Toast.makeText(context, "Catch saved successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.LoggedCatches.route) {
                        popUpTo(Screen.LoggedCatches.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                is SaveResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // State for dropdowns
    var speciesExpanded by remember { mutableStateOf(false) }
    var cloudCoverExpanded by remember { mutableStateOf(false) }
    var baitTypeExpanded by remember { mutableStateOf(false) }
    var baitColorExpanded by remember { mutableStateOf(false) }
    var turbidityExpanded by remember { mutableStateOf(false) }
    var retrievalMethodExpanded by remember { mutableStateOf(false) }

    // Filtered lists
    val filteredSpecies = remember(viewModel.species) {
        if (viewModel.species.isBlank()) {
            Constants.COMMON_FISH_SPECIES
        } else {
            Constants.COMMON_FISH_SPECIES.filter {
                it.contains(viewModel.species, ignoreCase = true)
            }
        }
    }

    val filteredBaitTypes = remember(viewModel.baitType) {
        if (viewModel.baitType.isBlank()) {
            Constants.BAIT_TYPES
        } else {
            Constants.BAIT_TYPES.filter {
                it.contains(viewModel.baitType, ignoreCase = true)
            }
        }
    }

    val filteredBaitColors = remember(viewModel.baitColor) {
        if (viewModel.baitColor.isBlank()) {
            Constants.BAIT_COLORS
        } else {
            Constants.BAIT_COLORS.filter {
                it.contains(viewModel.baitColor, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Picker
        OutlinedTextField(
            value = viewModel.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
            onValueChange = { },
            label = { Text("Date") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    showDatePicker(context, viewModel.date) { newDate ->
                        viewModel.updateDate(newDate)
                    }
                }) {
                    Text("📅")
                }
            }
        )

        // Time Picker
        OutlinedTextField(
            value = viewModel.time.format(DateTimeFormatter.ofPattern("HH:mm")),
            onValueChange = { },
            label = { Text("Time") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    showTimePicker(context, viewModel.time) { newTime ->
                        viewModel.updateTime(newTime)
                    }
                }) {
                    Text("🕒")
                }
            }
        )

        // Species Autocomplete with Dropdown
        ExposedDropdownMenuBox(
            expanded = speciesExpanded,
            onExpandedChange = { speciesExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.species,
                onValueChange = { 
                    viewModel.updateSpecies(it)
                    speciesExpanded = true
                },
                label = { Text("Species") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = speciesExpanded,
                onDismissRequest = { speciesExpanded = false }
            ) {
                filteredSpecies.forEach { species ->
                    DropdownMenuItem(
                        text = { Text(species) },
                        onClick = {
                            viewModel.updateSpecies(species)
                            speciesExpanded = false
                        }
                    )
                }
            }
        }

        // Length and Weight
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = if (viewModel.lengthInches > 0) viewModel.lengthInches.toString() else "",
                onValueChange = { value ->
                    value.toIntOrNull()?.let { viewModel.updateLengthInches(it) }
                },
                label = { Text("Length (inches)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )

            OutlinedTextField(
                value = if (viewModel.weightPounds > 0) viewModel.weightPounds.toString() else "",
                onValueChange = { value ->
                    value.toIntOrNull()?.let { viewModel.updateWeightPounds(it) }
                },
                label = { Text("Weight (lbs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )

            OutlinedTextField(
                value = if (viewModel.weightOunces > 0) viewModel.weightOunces.toString() else "",
                onValueChange = { value ->
                    value.toIntOrNull()?.let { viewModel.updateWeightOunces(it) }
                },
                label = { Text("Weight (oz)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )
        }

        // Weather with Cloud Cover Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = if (viewModel.temperature > 0) viewModel.temperature.toString() else "",
                onValueChange = { value ->
                    value.toDoubleOrNull()?.let { viewModel.updateTemperature(it) }
                },
                label = { Text("Temperature (°F)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = cloudCoverExpanded,
                onExpandedChange = { cloudCoverExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = viewModel.cloudCover.name.replace("_", " "),
                    onValueChange = { },
                    label = { Text("Cloud Cover") },
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cloudCoverExpanded) },
                    keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = cloudCoverExpanded,
                    onDismissRequest = { cloudCoverExpanded = false }
                ) {
                    CloudCover.values().forEach { cloudCover ->
                        DropdownMenuItem(
                            text = { Text(cloudCover.name.replace("_", " ")) },
                            onClick = {
                                viewModel.updateCloudCover(cloudCover)
                                cloudCoverExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Location
        Button(
            onClick = {
                if (hasLocationPermission(context)) {
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    try {
                        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        
                        lastKnownLocation?.let { location ->
                            viewModel.updateLocation(location)
                        } ?: run {
                            Toast.makeText(context, "Unable to get location. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: SecurityException) {
                        Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    onRequestLocationPermission()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.location != null) "Update Location" else "Get Location")
        }

        if (viewModel.location != null) {
            Text(
                "Lat: ${String.format("%.6f", viewModel.location?.latitude)}, Long: ${String.format("%.6f", viewModel.location?.longitude)}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // City and State
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.city,
                onValueChange = { viewModel.updateCity(it) },
                label = { Text("City") },
                modifier = Modifier.weight(2f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )
            
            var stateFieldFocused by remember { mutableStateOf(false) }
            
            OutlinedTextField(
                value = viewModel.stateTemp,
                onValueChange = { viewModel.updateState(it) },
                label = { Text("State (e.g., TX)") },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState: FocusState ->
                        // Only validate when losing focus and field was previously focused
                        if (!focusState.isFocused && stateFieldFocused) {
                            viewModel.validateAndUpdateState()
                        }
                        stateFieldFocused = focusState.isFocused
                    },
                keyboardActions = KeyboardActions(onDone = { 
                    viewModel.validateAndUpdateState()
                    hideKeyboard() 
                }),
                singleLine = true
            )
        }

        // State validation effect
        LaunchedEffect(Unit) {
            viewModel.stateValidationResult.collectLatest { result ->
                when (result) {
                    is StateValidationResult.Invalid -> {
                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    }
                    is StateValidationResult.Valid -> {
                        // Optionally handle valid state
                    }
                }
            }
        }

        // Water Body
        OutlinedTextField(
            value = viewModel.waterBody,
            onValueChange = { viewModel.updateWaterBody(it) },
            label = { Text("Water Body") },
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
            singleLine = true
        )

        // Bait Type and Color Dropdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = baitTypeExpanded,
                onExpandedChange = { baitTypeExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = viewModel.baitType,
                    onValueChange = { 
                        viewModel.updateBaitType(it)
                        baitTypeExpanded = true
                    },
                    label = { Text("Bait Type") },
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baitTypeExpanded) },
                    keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = baitTypeExpanded,
                    onDismissRequest = { baitTypeExpanded = false }
                ) {
                    filteredBaitTypes.forEach { baitType ->
                        DropdownMenuItem(
                            text = { Text(baitType) },
                            onClick = {
                                viewModel.updateBaitType(baitType)
                                baitTypeExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = baitColorExpanded,
                onExpandedChange = { baitColorExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = viewModel.baitColor,
                    onValueChange = { 
                        viewModel.updateBaitColor(it)
                        baitColorExpanded = true
                    },
                    label = { Text("Bait Color") },
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baitColorExpanded) },
                    keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = baitColorExpanded,
                    onDismissRequest = { baitColorExpanded = false }
                ) {
                    filteredBaitColors.forEach { color ->
                        DropdownMenuItem(
                            text = { Text(color) },
                            onClick = {
                                viewModel.updateBaitColor(color)
                                baitColorExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Water Turbidity Dropdown
        ExposedDropdownMenuBox(
            expanded = turbidityExpanded,
            onExpandedChange = { turbidityExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.waterTurbidity.toString(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Water Turbidity") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = turbidityExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = turbidityExpanded,
                onDismissRequest = { turbidityExpanded = false }
            ) {
                WaterTurbidity.values().forEach { turbidity ->
                    DropdownMenuItem(
                        text = { Text(turbidity.toString()) },
                        onClick = {
                            viewModel.updateWaterTurbidity(turbidity)
                            turbidityExpanded = false
                        }
                    )
                }
            }
        }

        // Retrieval Method Dropdown
        ExposedDropdownMenuBox(
            expanded = retrievalMethodExpanded,
            onExpandedChange = { retrievalMethodExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.retrievalMethod.toString(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Retrieval Method") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = retrievalMethodExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = retrievalMethodExpanded,
                onDismissRequest = { retrievalMethodExpanded = false }
            ) {
                RetrievalMethod.values().forEach { method ->
                    DropdownMenuItem(
                        text = { Text(method.toString()) },
                        onClick = {
                            viewModel.updateRetrievalMethod(method)
                            retrievalMethodExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = if (viewModel.waterTemperature > 0) viewModel.waterTemperature.toString() else "",
                onValueChange = { value ->
                    value.toDoubleOrNull()?.let { viewModel.updateWaterTemperature(it) }
                },
                label = { Text("Water Temp (°F)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )

            OutlinedTextField(
                value = if (viewModel.waterDepth > 0) viewModel.waterDepth.toString() else "",
                onValueChange = { value ->
                    value.toDoubleOrNull()?.let { viewModel.updateWaterDepth(it) }
                },
                label = { Text("Water Depth (ft)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )

            OutlinedTextField(
                value = if (viewModel.fishingDepth > 0) viewModel.fishingDepth.toString() else "",
                onValueChange = { value ->
                    value.toDoubleOrNull()?.let { viewModel.updateFishingDepth(it) }
                },
                label = { Text("Fishing Depth (ft)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Photo Capture
        PhotoCapture(
            photoUri = viewModel.photoUri?.let { Uri.parse(it) },
            onPhotoTaken = { uri -> viewModel.updatePhotoUri(uri.toString()) },
            onPhotoDeleted = { viewModel.updatePhotoUri(null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { 
                Log.d("NewCatchScreen", "Save button clicked, isSaving=$isSaving")
                if (!isSaving) {
                    Log.d("NewCatchScreen", "Starting save operation")
                    isSaving = true
                    Toast.makeText(context, "Saving catch...", Toast.LENGTH_SHORT).show()
                    viewModel.saveCatch()
                } else {
                    Log.d("NewCatchScreen", "Save already in progress")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving && viewModel.species.isNotBlank() // Basic validation
        ) {
            if (isSaving) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Saving...")
                }
            } else {
                Text("Save Catch")
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

private fun showDatePicker(
    context: Context,
    initialDate: java.time.LocalDate,
    onDateSelected: (java.time.LocalDate) -> Unit
) {
    val calendar = java.util.Calendar.getInstance()
    calendar.set(initialDate.year, initialDate.monthValue - 1, initialDate.dayOfMonth)

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(java.time.LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    ).show()
}

private fun showTimePicker(
    context: Context,
    initialTime: java.time.LocalTime,
    onTimeSelected: (java.time.LocalTime) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(java.time.LocalTime.of(hourOfDay, minute))
        },
        initialTime.hour,
        initialTime.minute,
        true
    ).show()
} 