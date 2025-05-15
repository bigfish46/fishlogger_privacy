package com.fishercreative.fishlogger.ui.screens

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishercreative.fishlogger.ui.viewmodels.LoggedCatchesViewModel
import com.fishercreative.fishlogger.ui.viewmodels.SearchFilter
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedCatchesScreen(
    viewModel: LoggedCatchesViewModel = viewModel()
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var showFilterMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val activity = context as? Activity
        activity?.currentFocus?.let { view ->
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Logged Catches",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Bar
        SearchBar(
            query = viewModel.searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            onSearch = { hideKeyboard() },
            active = false,
            onActiveChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Search catches...") },
            leadingIcon = { 
                IconButton(onClick = { hideKeyboard() }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            trailingIcon = {
                Row {
                    if (viewModel.searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { 
                                viewModel.updateSearchQuery("")
                                hideKeyboard()
                            }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                    Box {
                        TextButton(onClick = { showFilterMenu = true }) {
                            Text(viewModel.searchFilter.toString())
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            SearchFilter.values().forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter.toString()) },
                                    onClick = {
                                        viewModel.updateSearchFilter(filter)
                                        showFilterMenu = false
                                        hideKeyboard()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { }
        
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            viewModel.error != null -> {
                Text(
                    text = viewModel.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            viewModel.catches.isEmpty() -> {
                if (viewModel.searchQuery.isBlank()) {
                    Text(
                        text = "No catches logged yet",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text(
                        text = "No matches found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.catches) { catch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { /* TODO: Show details */ }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Species and Date/Time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = catch.species,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = catch.date.format(dateFormatter),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = catch.time.format(timeFormatter),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Size and Weight
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (catch.lengthInches > 0) {
                                        Text(
                                            text = "Length: ${catch.lengthInches}\"",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    if (catch.weightPounds > 0 || catch.weightOunces > 0) {
                                        Text(
                                            text = "Weight: ${catch.weightPounds}lb ${catch.weightOunces}oz",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                // Location Information
                                if (catch.waterBody.isNotBlank() || catch.nearestCity.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column {
                                        if (catch.waterBody.isNotBlank()) {
                                            Text(
                                                text = catch.waterBody,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        if (catch.nearestCity.isNotBlank()) {
                                            Text(
                                                text = catch.nearestCity,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                // Environmental Conditions
                                if (catch.temperature > 0 || catch.waterTemperature > 0 || 
                                    catch.cloudCover != null || catch.waterTurbidity != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            if (catch.temperature > 0) {
                                                Text(
                                                    text = "Air: ${catch.temperature}°F",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            if (catch.waterTemperature > 0) {
                                                Text(
                                                    text = "Water: ${catch.waterTemperature}°F",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = catch.cloudCover.toString().replace("_", " "),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = catch.waterTurbidity.toString().replace("_", " "),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }

                                // Fishing Details
                                if (catch.baitType.isNotBlank() || catch.baitColor.isNotBlank() ||
                                    catch.waterDepth > 0 || catch.fishingDepth > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        if (catch.baitType.isNotBlank() || catch.baitColor.isNotBlank()) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Bait: ${catch.baitType}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                if (catch.baitColor.isNotBlank()) {
                                                    Text(
                                                        text = "Color: ${catch.baitColor}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                        if (catch.waterDepth > 0 || catch.fishingDepth > 0) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                if (catch.waterDepth > 0) {
                                                    Text(
                                                        text = "Water depth: ${catch.waterDepth}ft",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                if (catch.fishingDepth > 0) {
                                                    Text(
                                                        text = "Fishing depth: ${catch.fishingDepth}ft",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 