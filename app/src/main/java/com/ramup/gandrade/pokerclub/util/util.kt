package com.example.gandrade.pokerclub.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.io.File
import java.util.*


fun showMessage(view: View, message: String, length: Int = Snackbar.LENGTH_INDEFINITE) {
    Snackbar.make(view, message, length).setAction("Action", null).show()
}

fun hideSoftKeyboard(activity: Activity) {
    var inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}

fun isEmpty(textView: TextView) = textView.text.isEmpty()
fun passwordsMatch(textView: TextView, textView1: TextView) = textView.text.toString().equals(textView1.text.toString())

@Throws(WriterException::class)
fun TextToImageEncode(value: String): Bitmap? {
    val bitMatrix: BitMatrix
    try {
        bitMatrix = MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, 500, 500, null)
    } catch (Illegalargumentexception: IllegalArgumentException) {
        return null
    }
    val bitMatrixWidth = bitMatrix.getWidth()
    val bitMatrixHeight = bitMatrix.getHeight()
    val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
    for (y in 0 until bitMatrixHeight) {
        val offset = y * bitMatrixWidth
        for (x in 0 until bitMatrixWidth) {
            pixels[offset + x] = if (bitMatrix.get(x, y)) 0x000000 else 0xffffff
        }
    }
    val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
    return bitmap
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height = options.outHeight;
    val width = options.outWidth;
    var inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        val halfHeight = height / 2;
        val halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}

fun bitmapToUriConverter(mBitmap: Bitmap, activity: Activity): Uri {
    lateinit var uri: Uri
    try {
        var options = BitmapFactory.Options();
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 100, 100);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        val newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200, true);
        val file = File(activity.getFilesDir(), "Image" + Random().nextInt() + ".jpeg");
        val out = activity.openFileOutput(file.getName(), Context.MODE_PRIVATE);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        //get absolute path
        val realPath = file.getAbsolutePath();
        val f = File(realPath);
        uri = Uri.fromFile(f);

    } catch (e: Exception) {
        Log.e("Your Error Message", e.message);
    }
    return uri;
}

fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

fun disableABCShowHideAnimation(actionBar: ActionBar) {
    try {
        actionBar::class.java.getDeclaredMethod("setShowHideAnimationEnabled", Boolean::class.javaPrimitiveType).invoke(actionBar, false)
    } catch (exception: Exception) {
        try {
            val mActionBarField = actionBar::class.java.getSuperclass().getDeclaredField("mActionBar")
            mActionBarField.setAccessible(true)
            val icsActionBar = mActionBarField.get(actionBar)
            val mShowHideAnimationEnabledField = icsActionBar.javaClass.getDeclaredField("mShowHideAnimationEnabled")
            mShowHideAnimationEnabledField.setAccessible(true)
            mShowHideAnimationEnabledField.set(icsActionBar, false)
            val mCurrentShowAnimField = icsActionBar.javaClass.getDeclaredField("mCurrentShowAnim")
            mCurrentShowAnimField.setAccessible(true)
            mCurrentShowAnimField.set(icsActionBar, null)
            //icsActionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(icsActionBar, false);
        } catch (e: Exception) {
            //....
        }

    }
}