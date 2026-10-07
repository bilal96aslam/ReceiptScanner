package org.shopbotics.receiptscanner.data

import org.shopbotics.receiptscanner.model.ReceiptResponse

interface RemoteReceiptDataSource {
    suspend fun scan(
        imageBytes: ByteArray,
        fileName: String
    ): ReceiptResponse

    suspend fun getReceipts(): List<ReceiptResponse>
}