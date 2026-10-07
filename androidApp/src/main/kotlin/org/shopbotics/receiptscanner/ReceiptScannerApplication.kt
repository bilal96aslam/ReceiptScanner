package org.shopbotics.receiptscanner

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.shopbotics.receiptscanner.di.initKoin

class ReceiptScannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@ReceiptScannerApplication)
        }
    }
}
