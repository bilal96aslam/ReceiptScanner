package org.shopbotics.receiptscanner

import androidx.compose.ui.window.ComposeUIViewController
import org.shopbotics.receiptscanner.app.App
import org.shopbotics.receiptscanner.di.initKoin
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}