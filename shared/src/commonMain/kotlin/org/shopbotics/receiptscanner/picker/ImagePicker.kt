package org.shopbotics.receiptscanner.picker

import androidx.compose.runtime.Composable

expect class ImagePickerLauncher {
    fun launchGallery()
    fun launchCamera()
}

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (bytes: ByteArray, fileName: String) -> Unit): ImagePickerLauncher
