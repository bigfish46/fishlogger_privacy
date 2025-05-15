package com.fishercreative.fishlogger.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishercreative.fishlogger.ui.viewmodels.SearchFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    currentFilter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { onSearch(query) },
            active = false,
            onActiveChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            placeholder = { Text("Search catches...") },
            leadingIcon = { 
                IconButton(onClick = { onSearch(query) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            trailingIcon = {
                Row {
                    AnimatedVisibility(visible = query.isNotBlank()) {
                        IconButton(
                            onClick = { onQueryChange("") }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                    Box {
                        TextButton(onClick = { showFilterMenu = true }) {
                            Text(currentFilter.toString())
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            SearchFilter.values().forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter.toString()) },
                                    onClick = {
                                        onFilterChange(filter)
                                        showFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { }
    }
} 