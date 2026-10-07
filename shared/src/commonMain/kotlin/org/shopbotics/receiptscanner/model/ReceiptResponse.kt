package org.shopbotics.receiptscanner.model

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptResponse(
    val id: Int,
    val vendor: String,
    val date: String,
    val total: Double,
    val needs_review: Boolean,
    val review_reason: String? = null
)