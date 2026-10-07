package org.shopbotics.receiptscanner.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    usePinned { memcpy(it.addressOf(0), bytes, length) }
}

actual class ImagePickerLauncher(
    private val gallery: () -> Unit,
    private val camera: () -> Unit
) {
    actual fun launchGallery() = gallery()
    actual fun launchCamera() = camera()
}

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (bytes: ByteArray, fileName: String) -> Unit): ImagePickerLauncher {
    val viewController = LocalUIViewController.current

    val galleryDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: return
                result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
                    data ?: return@loadDataRepresentationForTypeIdentifier
                    onImagePicked(data.toByteArray(), "receipt.jpg")
                }
            }
        }
    }

    val cameraDelegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage ?: return
                val data = UIImageJPEGRepresentation(image, 0.9) ?: return
                onImagePicked(data.toByteArray(), "receipt.jpg")
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, completion = null)
            }
        }
    }

    return ImagePickerLauncher(
        gallery = {
            val configuration = PHPickerConfiguration().apply {
                filter = PHPickerFilter.imagesFilter()
                selectionLimit = 1
            }
            val picker = PHPickerViewController(configuration = configuration)
            picker.delegate = galleryDelegate
            viewController.presentViewController(picker, animated = true, completion = null)
        },
        camera = {
            val cameraSource = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            if (UIImagePickerController.isSourceTypeAvailable(cameraSource)) {
                val picker = UIImagePickerController()
                picker.sourceType = cameraSource
                picker.delegate = cameraDelegate
                viewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    )
}
