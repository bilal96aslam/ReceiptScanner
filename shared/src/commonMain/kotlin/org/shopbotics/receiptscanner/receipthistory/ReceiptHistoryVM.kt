package org.shopbotics.receiptscanner.receipthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.shopbotics.receiptscanner.model.ReceiptResponse

class ReceiptHistoryVM(
    private val repository: ReceiptHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadReceipts() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            repository.getReceipts()
                .onSuccess { _uiState.value = HistoryUiState.Success(it) }
                .onFailure { _uiState.value = HistoryUiState.Error(it.message ?: "Unknown error") }
        }
    }
}

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val receipts: List<ReceiptResponse>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}
