package com.fishercreative.fishlogger.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishercreative.fishlogger.ui.components.CatchCard
import com.fishercreative.fishlogger.ui.components.EmptyState
import com.fishercreative.fishlogger.ui.components.LoadingSkeleton
import com.fishercreative.fishlogger.ui.components.SearchHeader
import com.fishercreative.fishlogger.ui.viewmodels.ExportResult
import com.fishercreative.fishlogger.ui.viewmodels.LoggedCatchesViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoggedCatchesScreen(
    viewModel: LoggedCatchesViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading)

    fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val activity = context as? Activity
        activity?.currentFocus?.let { view ->
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.exportToCsv()
        } else {
            Toast.makeText(
                context,
                "Storage permission is required to export catches",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.exportResult.collect { result ->
            when (result) {
                is ExportResult.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                is ExportResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.openFileEvent.collect { uri ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/csv")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "No app found to open CSV files. The file is saved in your Downloads folder.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header with title and export button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Logged",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = "Catches",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                
                FilledTonalButton(
                    onClick = {
                        if (!viewModel.hasWritePermission()) {
                            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            viewModel.exportToCsv()
                        }
                    },
                    enabled = viewModel.catches.isNotEmpty(),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Export Catches")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search header
        SearchHeader(
            query = viewModel.searchQuery,
            onQueryChange = { query ->
                viewModel.updateSearchQuery(query)
                hideKeyboard()
            },
            currentFilter = viewModel.searchFilter,
            onFilterChange = viewModel::updateSearchFilter,
            onSearch = { query -> 
                hideKeyboard()
                viewModel.updateSearchQuery(query)
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Content area with pull-to-refresh
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.loadCatches() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                viewModel.isLoading -> {
                    LoadingSkeleton()
                }
                viewModel.error != null -> {
                    EmptyState(
                        title = "Oops!",
                        message = viewModel.error ?: "Something went wrong",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                viewModel.catches.isEmpty() -> {
                    EmptyState(
                        title = if (viewModel.searchQuery.isBlank()) "No Catches Yet" else "No Matches Found",
                        message = if (viewModel.searchQuery.isBlank()) 
                            "Start logging your catches to see them here" 
                        else 
                            "Try adjusting your search or filter",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    AnimatedContent(
                        targetState = viewModel.catches,
                        transitionSpec = {
                            fadeIn() + slideInVertically() with 
                            fadeOut() + slideOutVertically()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { catches ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = catches,
                                key = { it.id }
                            ) { catch ->
                                CatchCard(
                                    catch = catch,
                                    onClick = { /* TODO: Show details */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 