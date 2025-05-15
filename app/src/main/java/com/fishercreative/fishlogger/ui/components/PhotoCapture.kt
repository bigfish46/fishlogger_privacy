package com.fishercreative.fishlogger.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider

@Composable
fun PhotoCapture(
    photoUri: Uri?,
    onPhotoTaken: (Uri) -> Unit,
    onPhotoDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { onPhotoTaken(it) }
        }
    }
    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempPhotoUri = createImageFileUri(context)
            tempPhotoUri?.let { cameraLauncher.launch(it) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        if (photoUri != null) {
            Box {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onPhotoDeleted,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete photo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Take photo",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add Photo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun createImageFileUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "FishLogger_${timeStamp}"
    
    // Get the DCIM directory for FishLogger
    val storageDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "FishLogger"
    )
    
    // Create the directory if it doesn't exist
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    
    // Create the photo file
    val photoFile = File(storageDir, "$imageFileName.jpg")
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
} 