package org.shopbotics.receiptscanner.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ReviewScreen(
    receipt: EditableReceipt,
    onVendorChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTotalChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Review receipt", style = MaterialTheme.typography.headlineSmall)

        if (receipt.needsReview) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Needs review", style = MaterialTheme.typography.labelLarge)
                    receipt.reviewReason?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        OutlinedTextField(
            value = receipt.vendor,
            onValueChange = onVendorChange,
            label = { Text("Vendor") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = receipt.date,
            onValueChange = onDateChange,
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = receipt.total,
            onValueChange = onTotalChange,
            label = { Text("Total") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = onDiscard, modifier = Modifier.weight(1f)) {
                Text("Discard")
            }
            Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                Text("Confirm")
            }
        }
    }
}
