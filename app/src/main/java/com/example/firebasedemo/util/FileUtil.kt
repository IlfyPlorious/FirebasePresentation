package com.example.firebasedemo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File


fun loadBitmapFromAssets(context: Context, fileName: String): ImageBitmap? {
    return try {
        context.assets.open(fileName).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

fun Bitmap.rotate(angle: Float): Bitmap {
    val matrix: Matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.getWidth(), this.getHeight(), matrix, true)
}

fun Context.loadBitmapFromUri(uri: Uri): Bitmap {
    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open URI: $uri")
    return BitmapFactory.decodeStream(inputStream)
}

fun Context.saveBitmapToUri(bitmap: Bitmap): Uri {
    val file = File(filesDir, "bounding_box_image.jpg")
    contentResolver.openOutputStream(file.toUri())?.use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    } ?: throw IllegalArgumentException("Unable to open output stream for file: $file")

    return file.toUri()
}

fun Bitmap.applyBoundingBoxToImage(boundingBox: Rect, id: Int): Bitmap {
    val mutableBitmap = copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)

    val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    val textPaint = Paint().apply {
        color = Color.RED
        textSize = 40f
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    // Draw each bounding box

    canvas.drawRect(
        boundingBox, paint
    )

    canvas.drawText(
        id.toString(),
        boundingBox.left + 8f,      // small horizontal padding
        boundingBox.top + 40f,      // vertical padding to keep text inside
        textPaint
    )

    return mutableBitmap
}