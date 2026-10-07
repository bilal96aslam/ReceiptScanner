package org.shopbotics.receiptscanner.data

import kotlinx.coroutines.CancellationException
import org.shopbotics.receiptscanner.model.ReceiptResponse

class ReceiptRepository(
    private val remoteDataSource: RemoteReceiptDataSource
) {
    suspend fun scan(imageBytes: ByteArray, fileName: String): Result<ReceiptResponse> {
        return try {
            val result = remoteDataSource.scan(imageBytes, fileName)
            Result.success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}