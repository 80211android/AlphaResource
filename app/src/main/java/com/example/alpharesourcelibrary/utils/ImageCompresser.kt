package com.example.alpharesourcelibrary.utils

import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.media.ExifInterface
import android.os.Environment
import com.example.alpharesourcelibrary.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageCompresser {

    // Average image size = 600-800 kb
    //private val maxHeight = 1280.0f  // 816, 400
    //private val maxWidth = 1280.0f   // 612, 300

    // Average image size = 40-60 kb
    //private val maxHeight = 400.0f  // 816, 400
    //private val maxWidth = 300.0f   // 612, 300

    // Average image size = 190 - 215 kb
    //private val maxHeight = 816.0f  // 816, 400
    //private val maxWidth = 612.0f   // 612, 300

    private val maxHeight = 1000.0f  // 816, 400
    private val maxWidth = 750.0f   // 612, 300

    companion object {
        val instance = ImageCompresser()
    }

    /**
     * Reduces the size of an image without affecting its quality.
     *
     * @param imagePath -Path of an image
     * @return
     */

    fun getCompressedImageFile(imagePath: String): File {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp: Bitmap? = BitmapFactory.decodeFile(imagePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(imagePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        var canvas = Canvas(scaledBitmap!!)
//        canvas.matrix = scaleMatrix
        canvas.drawBitmap(bmp!!, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(FILTER_BITMAP_FLAG))
        bmp.recycle()

        val exif: ExifInterface
        try {
            exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90.toFloat())
            } else if (orientation == 3) {
                matrix.postRotate(180.toFloat())
            } else if (orientation == 8) {
                matrix.postRotate(270.toFloat())
            }
            scaledBitmap =
                    Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        var fileOutputStream: FileOutputStream? = null
        val file = File(getFilename())
        try {
            fileOutputStream = FileOutputStream(file)
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
        //  return filename;
        // return scaledBitmap;
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getFilename(): String {
        val mediaStorageDir = File(
            "${Environment.getExternalStorageDirectory()}/Android/data/" + BuildConfig.APPLICATION_ID + "/files_root"
        )
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        val mImageName = "IMG_" + (System.currentTimeMillis()).toString() + ".jpg"
        return (mediaStorageDir.absolutePath + "/" + mImageName)
    }
}