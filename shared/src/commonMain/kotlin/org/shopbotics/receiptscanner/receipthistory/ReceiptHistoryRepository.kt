package org.shopbotics.receiptscanner.receipthistory

import kotlinx.coroutines.CancellationException
import org.shopbotics.receiptscanner.data.RemoteReceiptDataSource
import org.shopbotics.receiptscanner.model.ReceiptResponse

class ReceiptHistoryRepository(
    private val remoteDataSource: RemoteReceiptDataSource
) {
    suspend fun getReceipts(): Result<List<ReceiptResponse>> {
        return try {
            Result.success(remoteDataSource.getReceipts())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
