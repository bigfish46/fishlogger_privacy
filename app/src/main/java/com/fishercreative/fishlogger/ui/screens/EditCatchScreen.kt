package com.fishercreative.fishlogger.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fishercreative.fishlogger.ui.navigation.Screen
import com.fishercreative.fishlogger.ui.viewmodels.EditCatchViewModel
import com.fishercreative.fishlogger.ui.viewmodels.LoadResult
import com.fishercreative.fishlogger.ui.viewmodels.SaveResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCatchScreen(
    catchId: String,
    onRequestLocationPermission: () -> Unit,
    navController: NavController,
    viewModel: EditCatchViewModel = viewModel(factory = EditCatchViewModel.Factory)
) {
    val context = LocalContext.current
    var isUpdating by remember { mutableStateOf(false) }

    // Load catch data when screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadCatch(catchId)
    }

    // Handle load result
    LaunchedEffect(Unit) {
        viewModel.loadResult.collectLatest { result ->
            when (result) {
                is LoadResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                }
                LoadResult.Success -> {
                    // Catch loaded successfully
                }
            }
        }
    }

    // Handle save result
    LaunchedEffect(Unit) {
        viewModel.saveResult.collectLatest { result ->
            isUpdating = false
            when (result) {
                SaveResult.Success -> {
                    Toast.makeText(context, "Catch updated successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.LoggedCatches.route) {
                        popUpTo(Screen.LoggedCatches.route) {
                            inclusive = true
                        }
                    }
                }
                is SaveResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Reuse the NewCatchScreen layout but with different button text and action
    NewCatchScreen(
        viewModel = viewModel,
        onRequestLocationPermission = onRequestLocationPermission,
        navController = navController,
        buttonText = if (isUpdating) "Updating..." else "Update Catch",
        onButtonClick = {
            if (!isUpdating) {
                isUpdating = true
                viewModel.updateCatch(catchId)
            }
        }
    )
} 