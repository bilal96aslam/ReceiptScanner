package org.shopbotics.receiptscanner.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.shopbotics.receiptscanner.receipthistory.ReceiptHistoryScreen
import org.shopbotics.receiptscanner.scanner.ReceiptScannerScreen

@Composable
@Preview
fun App() {
    var showHistory by rememberSaveable { mutableStateOf(false) }

    MaterialTheme {
        if (showHistory) {
            ReceiptHistoryScreen(onBack = { showHistory = false })
        } else {
            ReceiptScannerScreen(onOpenHistory = { showHistory = true })
        }
    }
}
