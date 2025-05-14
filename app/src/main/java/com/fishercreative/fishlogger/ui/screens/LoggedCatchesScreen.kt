package com.fishercreative.fishlogger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishercreative.fishlogger.ui.viewmodels.LoggedCatchesViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedCatchesScreen(
    viewModel: LoggedCatchesViewModel = viewModel()
) {
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
                Text(
                    text = "No catches logged yet",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = catch.species,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = catch.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (catch.lengthInches > 0) {
                                        Text("${catch.lengthInches}\"")
                                    }
                                    if (catch.weightPounds > 0 || catch.weightOunces > 0) {
                                        Text("${catch.weightPounds}lb ${catch.weightOunces}oz")
                                    }
                                }
                                
                                catch.waterBody.takeIf { it.isNotBlank() }?.let { waterBody ->
                                    Text(
                                        text = waterBody,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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