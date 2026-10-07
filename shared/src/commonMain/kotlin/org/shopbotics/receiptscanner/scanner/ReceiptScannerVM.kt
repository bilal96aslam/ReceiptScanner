package org.shopbotics.receiptscanner.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.shopbotics.receiptscanner.data.ReceiptRepository
import org.shopbotics.receiptscanner.model.ReceiptResponse

class ReceiptScannerVM(
    private val repository: ReceiptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _pickedImageBytes = MutableStateFlow<ByteArray?>(null)
    val pickedImageBytes = _pickedImageBytes.asStateFlow()

    private val _editableReceipt = MutableStateFlow(EditableReceipt())
    val editableReceipt = _editableReceipt.asStateFlow()

    fun scanReceipt(imageBytes: ByteArray, fileName: String) {
        _pickedImageBytes.value = imageBytes
        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading
            repository.scan(imageBytes, fileName)
                .onSuccess {
                    _editableReceipt.value = it.toEditable()
                    _uiState.value = ScanUiState.Success(it)
                }
                .onFailure { _uiState.value = ScanUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun updateVendor(value: String) = _editableReceipt.update { it.copy(vendor = value) }

    fun updateDate(value: String) = _editableReceipt.update { it.copy(date = value) }

    fun updateTotal(value: String) = _editableReceipt.update { it.copy(total = value) }

    fun reset() {
        _uiState.value = ScanUiState.Idle
        _pickedImageBytes.value = null
        _editableReceipt.value = EditableReceipt()
    }
}

data class EditableReceipt(
    val vendor: String = "",
    val date: String = "",
    val total: String = "",
    val needsReview: Boolean = false,
    val reviewReason: String? = null
)

private fun ReceiptResponse.toEditable() = EditableReceipt(
    vendor = vendor,
    date = date,
    total = total.toString(),
    needsReview = needs_review,
    reviewReason = review_reason
)

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val receipt: ReceiptResponse) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}