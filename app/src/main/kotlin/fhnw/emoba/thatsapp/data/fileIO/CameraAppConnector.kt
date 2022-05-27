package fhnw.emoba.thatsapp.data.fileIO

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class CameraAppConnector(val activity: ComponentActivity) {
    private val photoFile by lazy { getFile("photo.jpg") }
    private val imageCaptureIntent by lazy { createImageCaptureIntent() }

    private var lastBitmap: Bitmap? = null

    private lateinit var onSuccess: (Bitmap) -> Unit
    private lateinit var onCanceled: () -> Unit

    private val cameraLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val options = BitmapFactory.Options().apply {
                    inMutable = true
                    if (lastBitmap != null) {
                        inBitmap = lastBitmap
                    }
                }
                val lastBitmap = BitmapFactory.decodeFile(photoFile.absolutePath, options)
                onSuccess(lastBitmap)
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                onCanceled()
            }
        }

    fun getBitmap(
        onSuccess: (Bitmap) -> Unit,
        onCanceled: () -> Unit
    ) {
        this.onSuccess = onSuccess
        this.onCanceled = onCanceled
        cameraLauncher.launch(imageCaptureIntent)
    }

    private fun createImageCaptureIntent(): Intent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            val providerFile = FileProvider.getUriForFile(
                activity,
                "fhnw.emoba.fileproviderthatsapp",
                photoFile
            )
            putExtra(
                MediaStore.EXTRA_OUTPUT,
                providerFile
            )
        }

    @SuppressLint("SetWorldWritable")
    private fun getFile(fileName: String): File =
        File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName).apply {
            createNewFile()
            setWritable(true, false)
        }
}