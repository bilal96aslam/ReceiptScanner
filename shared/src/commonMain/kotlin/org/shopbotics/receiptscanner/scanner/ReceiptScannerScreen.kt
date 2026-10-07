package org.shopbotics.receiptscanner.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.shopbotics.receiptscanner.picker.rememberImagePickerLauncher

@Composable
fun ReceiptScannerScreen(
    onOpenHistory: () -> Unit,
    viewModel: ReceiptScannerVM = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val pickedImageBytes by viewModel.pickedImageBytes.collectAsState()
    val editableReceipt by viewModel.editableReceipt.collectAsState()

    var showSourceDialog by remember { mutableStateOf(false) }

    val imagePicker = rememberImagePickerLauncher { bytes, fileName ->
        viewModel.scanReceipt(bytes, fileName)
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Select image source") },
            confirmButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    imagePicker.launchCamera()
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    imagePicker.launchGallery()
                }) { Text("Gallery") }
            }
        )
    }

    if (uiState is ScanUiState.Success) {
        ReviewScreen(
            receipt = editableReceipt,
            onVendorChange = viewModel::updateVendor,
            onDateChange = viewModel::updateDate,
            onTotalChange = viewModel::updateTotal,
            onConfirm = viewModel::reset,
            onDiscard = viewModel::reset
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        TextButton(
            onClick = onOpenHistory,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp)
        ) {
            Text("History")
        }

        Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (pickedImageBytes != null) {
                    AsyncImage(
                        model = pickedImageBytes,
                        contentDescription = "Selected receipt",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        "No image selected",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showSourceDialog = true }) {
                Text("Pick a receipt image")
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is ScanUiState.Idle -> Unit
                is ScanUiState.Loading -> Unit
                is ScanUiState.Success -> Unit
                is ScanUiState.Error -> Text("Error: ${state.message}")
            }
        }

        if (uiState is ScanUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
                    .pointerInput(Unit) { detectTapGestures { } },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
