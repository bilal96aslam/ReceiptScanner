package org.shopbotics.receiptscanner.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.shopbotics.receiptscanner.data.KtorRemoteDataReceiptSource
import org.shopbotics.receiptscanner.data.ReceiptRepository
import org.shopbotics.receiptscanner.data.RemoteReceiptDataSource
import org.shopbotics.receiptscanner.receipthistory.ReceiptHistoryRepository
import org.shopbotics.receiptscanner.receipthistory.ReceiptHistoryVM
import org.shopbotics.receiptscanner.scanner.ReceiptScannerVM

val sharedModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 150_000
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 150_000
            }
        }
    }
    single<RemoteReceiptDataSource> { KtorRemoteDataReceiptSource(get()) }
    single { ReceiptRepository(get()) }
    viewModel { ReceiptScannerVM(get()) }
    single { ReceiptHistoryRepository(get()) }
    viewModel { ReceiptHistoryVM(get()) }
}
