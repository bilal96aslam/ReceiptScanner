package org.shopbotics.receiptscanner.picker

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

actual class ImagePickerLauncher(
    private val gallery: () -> Unit,
    private val camera: () -> Unit
) {
    actual fun launchGallery() = gallery()
    actual fun launchCamera() = camera()
}

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (bytes: ByteArray, fileName: String) -> Unit): ImagePickerLauncher {
    val context = LocalContext.current
    var pendingPhoto by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                onImagePicked(bytes, "receipt.jpg")
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val file = pendingPhoto
        if (success && file != null) {
            onImagePicked(file.readBytes(), "receipt.jpg")
        }
        file?.delete()
        pendingPhoto = null
    }

    fun startCamera() {
        val directory = File(context.cacheDir, "camera_images").apply { mkdirs() }
        val file = File.createTempFile("receipt_", ".jpg", directory)
        pendingPhoto = file
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        cameraLauncher.launch(uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) startCamera()
    }

    return ImagePickerLauncher(
        gallery = { galleryLauncher.launch("image/*") },
        camera = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) startCamera() else permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    )
}
