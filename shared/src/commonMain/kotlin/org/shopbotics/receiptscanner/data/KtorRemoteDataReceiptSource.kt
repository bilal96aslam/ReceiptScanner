package org.shopbotics.receiptscanner.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.shopbotics.receiptscanner.model.ReceiptResponse

// private const val BASE_URL = "http://10.0.2.2:8000" // emulator
// private const val BASE_URL = "http://192.168.10.3:8000" // physical device
private const val BASE_URL = "https://receipt-scanner-uk8c.onrender.com"
private const val API_KEY = "my-test-secret-123"

class KtorRemoteDataReceiptSource(
    private val httpClient: HttpClient
) : RemoteReceiptDataSource {

    override suspend fun scan(
        imageBytes: ByteArray,
        fileName: String
    ): ReceiptResponse {
        val response = httpClient.submitFormWithBinaryData(
            url = "$BASE_URL/scan",
            formData = formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ) {
            header("x-api-key", API_KEY)
        }
        return response.body()
    }

    override suspend fun getReceipts(): List<ReceiptResponse> {
        val response = httpClient.get("$BASE_URL/receipts") {
            header("x-api-key", API_KEY)
        }
        return response.body()
    }
}
