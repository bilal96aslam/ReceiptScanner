package org.shopbotics.receiptscanner.receipthistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.shopbotics.receiptscanner.model.ReceiptResponse
import receiptscanner.shared.generated.resources.Res
import receiptscanner.shared.generated.resources.ic_arrow_back

@Composable
fun ReceiptHistoryScreen(
    onBack: () -> Unit,
    viewModel: ReceiptHistoryVM = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadReceipts() }

    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_back),
                contentDescription = "Back",
                modifier = Modifier.clickable(onClick = onBack)
            )
            Text("Receipt history", style = MaterialTheme.typography.headlineSmall)
        }

        when (val state = uiState) {
            is HistoryUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }

            is HistoryUiState.Error -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: ${state.message}")
                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::loadReceipts) { Text("Retry") }
            }

            is HistoryUiState.Success ->
                if (state.receipts.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No receipts yet") }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.receipts, key = { it.id }) { ReceiptItem(it) }
                    }
                }
        }
    }
}

@Composable
private fun ReceiptItem(receipt: ReceiptResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(receipt.vendor, style = MaterialTheme.typography.titleMedium)
            Text(receipt.date, style = MaterialTheme.typography.bodyMedium)
            Text("Total: ${receipt.total}", style = MaterialTheme.typography.bodyMedium)
            if (receipt.needs_review) {
                Text(
                    "Needs review",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
